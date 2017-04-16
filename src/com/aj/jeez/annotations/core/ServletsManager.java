package com.aj.jeez.annotations.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
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
import com.aj.jeez.annotations.WebService;
import com.aj.jeez.annotations.exceptions.ParameterTypingException;
import com.aj.jeez.annotations.exceptions.ServletInstantiationExceptionAdvise;
import com.aj.jeez.annotations.exceptions.WebServiceAnnotationMisuseException;
import com.aj.tools.Stretch;

public class ServletsManager {

	public static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ServletInstantiationExceptionAdvise{
		JSONObject router = new JSONObject();

		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignClassServlets(sc,classServices,router);
		return router;
	}


	public static JSONObject assignClassServlets(
			ServletContext sc,
			Entry<Class<?>,Set<Method>> classServices,
			JSONObject router
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ServletInstantiationExceptionAdvise{
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
			) throws WebServiceAnnotationMisuseException, ServletInstantiationExceptionAdvise, ParameterTypingException, ClassNotFoundException{

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

		if(servletID.length()==0){
			servletID=className+"."+service.getName()+"Servlet";
			System.out.println("Empty Servlet name : JEEZ will choose a name.. what about '"+servletID+"'");
		}
		
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
		StaticTypeControler.paramsAreValid(className,servletID,expIN);
		StaticTypeControler.paramsAreValid(className,servletID,expOut);
		StaticTypeControler.paramsAreValid(className,servletID,optIN);
		StaticTypeControler.paramsAreValid(className,servletID,optOut);

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

		router.put(
				new Integer(servletID.hashCode()).toString(), 
				driver.put(
						"service",
						new JSONObject()
						.put("name",className+"."+service.getName())
						.put("id",servletID.hashCode())
						)
				);
		return router;
	}



}
