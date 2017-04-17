package com.aj.jeez.annotation.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
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
import com.aj.jeez.annotation.annotations.JSONOUTParams;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.RequestParams;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.jeez.annotation.exceptions.ParameterNamingException;
import com.aj.jeez.annotation.exceptions.ParameterTypingException;
import com.aj.jeez.annotation.exceptions.WebServiceAnnotationMisuseException;
import com.aj.jeez.policy.GetServlet;
import com.aj.jeez.policy.PostServlet;
import com.aj.tools.Stretch;

public class ServletsManager {

	public static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException{
		JSONObject router = new JSONObject();

		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignClassServlets(sc,classServices,router);
		return router;
	}


	public static JSONObject assignClassServlets(
			ServletContext sc,
			Entry<Class<?>,Set<Method>> classServices,
			JSONObject router
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException{
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
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException{

		String servletID = className+"."+service.getName()+"Servlet";

		WebService ws = service.getAnnotation(WebService.class);

		String id = ws.id();
		String urlPattern = ws.urlPattern();
		boolean auth=ws.requireAuth();
		RequestParams rp = ws.requestParams();
		JSONOUTParams jop = ws.jsonOutParams();

		Param [] expIN=rp.value();
		Param [] optIN=rp.optionals();
		Param [] optOut=jop.optionals();
		Param [] expOut=jop.value();

		Class<? extends HttpServlet>policy=ws.policy();
		Class<?>[]clazzTab = ws.checkClasses();

		//Policy checking 
		if(Modifier.isAbstract(policy.getModifiers()))
			throw new WebServiceAnnotationMisuseException("Dynamic sevlet policy class : '"+className+"' must not be abstract");				

		//Static parameters typing test 
		StaticTypedParamControler.paramsAreValid(className,servletID,expIN,expOut,optIN,optOut);

		//ParamName collision detection
		Set<String>expINNameSet=new HashSet<>();
		Set<String>optINNameSet=new HashSet<>();	

		for(String exp : expIN)
			expINNameSet.add(exp.split("\\:")[0].trim());

		for(String opt : optIN)
			expINNameSet.add(opt.split("\\:")[0].trim());

		for(String paramName :optINNameSet)
			if(expINNameSet.contains(paramName))
				throw new WebServiceAnnotationMisuseException("Collision detected between parameters : '"+paramName+"' is duplicated");

		//Test class existence (check the class is loaded in the webApp container)
		for(Class<?> checkClazz : clazzTab) 
			Class.forName(checkClazz.getCanonicalName());
		String clazzSetStr = Stretch.joinClasses(clazzTab);


		//Servlet registration
		ServletRegistration.Dynamic sr = sc.addServlet(servletID,policy);
		sr.addMapping(urlPattern);
		sr.setLoadOnStartup(ws.loadOnStartup());
		sr.setAsyncSupported(ws.asyncSupported());
		sr.setInitParameter("expectedIn", Stretch.join(expIN));
		sr.setInitParameter("expectedOut", Stretch.join(expOut));
		sr.setInitParameter("optionalIn", Stretch.join(optIN));
		sr.setInitParameter("optionalOut", Stretch.join(optOut));
		sr.setInitParameter("requireAuth", auth?"true":"false");
		sr.setInitParameter("checkClasses", clazzSetStr);
		sr.setInitParameter("serviceClass", className);
		sr.setInitParameter("serviceMethod", service.getName());

		Map<String,String> initParams = new HashMap<>();
		for(WebInitParam wip:ws.initParams())//TODO tester
			initParams.put(wip.name(),wip.value());
		sr.setInitParameters(initParams);


		//Servlet communication driver settings
		JSONObject driver = new JSONObject();
		driver.put("url",urlPattern);
		driver.put("auth",auth);
		driver.put("optOut",optOut);
		driver.put("optIN",optIN);
		driver.put("expOut",expOut);
		driver.put("expIN",expIN);
		if(PostServlet.class.isAssignableFrom(policy))
			driver.put("httpM", 1);

		//Get override over method definition in case of multiple legacy
		if(GetServlet.class.isAssignableFrom(policy))
			driver.put("httpM", 0);

		return router.put(servletID,driver);
	}



}
