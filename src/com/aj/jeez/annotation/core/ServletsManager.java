package com.aj.jeez.annotation.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

import org.json.JSONObject;

import com.aj.jeez.JEEZServlet;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.jeez.annotation.exceptions.ParameterNamingException;
import com.aj.jeez.annotation.exceptions.ParameterTypingException;
import com.aj.jeez.annotation.exceptions.ServletInstantiationExceptionAdvise;
import com.aj.jeez.annotation.exceptions.WebServiceAnnotationMisuseException;
import com.aj.jeez.policy.GetServlet;
import com.aj.jeez.policy.PostServlet;
import com.aj.tools.Stretch;

public class ServletsManager {

	public static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ServletInstantiationExceptionAdvise, ParameterNamingException{
		JSONObject router = new JSONObject();

		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignClassServlets(sc,classServices,router);
		return router;
	}


	public static JSONObject assignClassServlets(
			ServletContext sc,
			Entry<Class<?>,Set<Method>> classServices,
			JSONObject router
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ServletInstantiationExceptionAdvise, ParameterNamingException{
		Class<?> clazz = classServices.getKey();
		String className = clazz.getCanonicalName();

		for(Method service : classServices.getValue())
			assignClassServlet(sc,className,service,router);
		return router;
	}


	public static JSONObject assignClassServlet(
			ServletContext sc,
			String className,
			Method service,
			JSONObject router
			) throws WebServiceAnnotationMisuseException, ServletInstantiationExceptionAdvise, ParameterTypingException, ClassNotFoundException, ParameterNamingException{

		WebService ws = service.getAnnotation(WebService.class);
		String [] expIN=ws.expectedIn();
		String [] expOut=ws.expectedOut();
		String [] optIN=ws.optionalIn();
		String [] optOut=ws.optionalOut();
		boolean auth=ws.requireAuth();
		WebServlet webServlet = ws.webServlet();
		String servletID = webServlet.name();
		Class<? extends HttpServlet>policy=ws.policy();
		Class<?>[]clazzTab = ws.checkClasses();

		// Register Servlet
		if(Modifier.isAbstract(policy.getModifiers()))
			throw new WebServiceAnnotationMisuseException("Dynamic sevlet policy class : '"+className+"' must not be abstract");		

		if(servletID.length()==0)
			throw new WebServiceAnnotationMisuseException("The service '"+className+"."+service.getName()+"' 's servlet name must be unique among the servlets and not null");		

		JSONObject driver = new JSONObject();

		ServletRegistration.Dynamic sr = sc.addServlet(servletID,policy);
		try{
			String[] paths = webServlet.urlPatterns();
			sr.addMapping(paths);
			driver.put("paths",paths);
		}catch(NullPointerException npe)
		{
			npe.printStackTrace();
			throw new ServletInstantiationExceptionAdvise
			("Advise : Check if two differents servlets have the same name.");
		}

		sr.setLoadOnStartup(webServlet.loadOnStartup());
		sr.setAsyncSupported(webServlet.asyncSupported());

		//Static parameters typing test 
		StaticTypedParamControler.paramsAreValid(className,servletID,expIN);
		StaticTypedParamControler.paramsAreValid(className,servletID,expOut);
		StaticTypedParamControler.paramsAreValid(className,servletID,optIN);
		StaticTypedParamControler.paramsAreValid(className,servletID,optOut);
		
		//ParamName collision detection
		Set<String>expINNameSet=new HashSet<>();
		Set<String>optINNameSet=new HashSet<>();
		
		for(String exp : expIN)
			expINNameSet.add(exp.split("\\:")[0].trim());
		
		for(String opt : optIN)
			expINNameSet.add(opt.split("\\:")[0].trim());
	
		for(String paramName :optINNameSet)
			if(expINNameSet.contains(paramName))
				throw new WebServiceAnnotationMisuseException("Collision detected between parameters : '"+paramName+"' is present in both expectedIN and optionalIN parameters");


		String clazzSetStr="";
		int i=0;
		//test classes presence test by a mockable instantiation and listing/setting

		for(Class<?> checkClazz : clazzTab){
			Class.forName(checkClazz.getCanonicalName());
			if(i++<clazzTab.length-1)
				clazzSetStr+=checkClazz.getCanonicalName()+",";
			else
				clazzSetStr+=checkClazz.getCanonicalName();
		}

		if(JEEZServlet.class.isAssignableFrom(policy)){
			sr.setInitParameter("expectedIn", Stretch.join(expIN));
			driver.put("expIN",expIN);

			sr.setInitParameter("expectedOut", Stretch.join(expOut));
			driver.put("expOut",expOut);

			sr.setInitParameter("optionalIn", Stretch.join(optIN));
			driver.put("optIN",optIN);

			sr.setInitParameter("optionalOut", Stretch.join(optOut));
			driver.put("optOut",optOut);

			sr.setInitParameter("requireAuth", auth?"true":"false");
			driver.put("auth",auth);

			if(PostServlet.class.isAssignableFrom(policy))
				driver.put("httpM", 1);

			//Get override over method definition in case of multiple legacy
			if(GetServlet.class.isAssignableFrom(policy))
				driver.put("httpM", 0);

			sr.setInitParameter("checkClasses", clazzSetStr);
		}

		sr.setInitParameter("serviceClass", className);
		sr.setInitParameter("serviceMethod", service.getName());

		/**
		 * sr.setInitParameters(initParams); must be at the end of the script to prevent collisions with JEEZ parameters names
		 * http://docs.oracle.com/javaee/6/api/javax/servlet/Registration.html#setInitParameters(java.util.Map)
		 * setInitParameters
			java.util.Set<java.lang.String> setInitParameters(java.util.Map<java.lang.String,java.lang.String> initParameters)
			Sets the given initialization parameters on the Servlet or Filter that is represented by this Registration. 
			The given map of initialization parameters is processed by-value, i.e., for each initialization parameter contained in the map, this method calls setInitParameter(String,String). 
			If that method would return false for any of the initialization parameters in the given map, no updates will be performed, and false will be returned.
			Likewise, if the map contains an initialization parameter with a null name or value, no updates will be performed, and an IllegalArgumentException will be thrown. 
			true if the update was successful, i.e., an initialization parameter with the given name did not already exist for the Servlet or Filter represented by this Registration, and false otherwise */
		Map<String,String> initParams = new HashMap<>();
		for(WebInitParam wip:webServlet.initParams())//TODO tester
			initParams.put(wip.name(),wip.value());
		sr.setInitParameters(initParams);

		return router.put(servletID,driver);
	}



}
