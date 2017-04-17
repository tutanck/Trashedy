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
import com.aj.jeez.annotation.exceptions.WebInitParameterSettingException;
import com.aj.jeez.annotation.exceptions.WebServiceAnnotationMisuseException;
import com.aj.jeez.policy.GetServlet;
import com.aj.jeez.policy.PostServlet;
import com.aj.tools.Stretch;

public class ServletsManager {
	
	public static String JZID="JZID12101992";

	static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException{
		JSONObject router = new JSONObject();

		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignClassServlets(sc,classServices,router);
		return router;
	}


	static JSONObject assignClassServlets(
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


	static JSONObject assignClassServlet(
			ServletContext sc,
			String className,
			Method service,
			JSONObject router
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException{

		boolean invalid=false;

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

		/*** VERIFICATION PHASE */

		//Policy checking 
		if(Modifier.isAbstract(policy.getModifiers()))
			throw new WebServiceAnnotationMisuseException(servletID+": Dynamic sevlet policy class : '"+className+"' must not be abstract");				

		//Static parameters typing test 
		StaticTypedParamControler.paramsAreValid(className,servletID,expIN,expOut,optIN,optOut);

		//Parameter name collisions detection
		detectParamNameCollision(servletID,"request",expIN,optIN);
		detectParamNameCollision(servletID,"jsonout",optOut,expOut);

		//Test class existence (check if the class is loaded in the webApp container)
		for(Class<?> checkClazz : clazzTab) 
			Class.forName(checkClazz.getCanonicalName());


		/*** REGISTRATION PHASE */

		ServletRegistration.Dynamic sr = sc.addServlet(servletID,policy);
		sr.addMapping(urlPattern);
		sr.setLoadOnStartup(ws.loadOnStartup());
		sr.setAsyncSupported(ws.asyncSupported());
		sr.setInitParameter(JZID+"expIn", Stretch.join(expIN));
		sr.setInitParameter(JZID+"expOut", Stretch.join(expOut));
		sr.setInitParameter(JZID+"optIn", Stretch.join(optIN));
		sr.setInitParameter(JZID+"optOut", Stretch.join(optOut));
		sr.setInitParameter(JZID+"auth", auth?"true":"false");
		sr.setInitParameter(JZID+"ck",Stretch.joinClasses(clazzTab));
		sr.setInitParameter(JZID+"sc", className);
		sr.setInitParameter(JZID+"sm", service.getName());

		Map<String,String> initParams = new HashMap<>();
		for(WebInitParam wip:ws.initParams())//TODO tester
			initParams.put(wip.name(),wip.value());
		sr.setInitParameters(initParams);


		/*** DRIVER SETTINGS PHASE */

		//Servlet communication driver settings
		JSONObject driver = new JSONObject();
		driver.put("url",urlPattern);
		driver.put("auth",auth);
		driver.put("expIN",expIN);
		driver.put("expOut",expOut);
		driver.put("optIN",optIN);
		driver.put("optOut",optOut);

		//Get override over method definition in case of multiple legacy
		if(GetServlet.class.isAssignableFrom(policy))
			driver.put("httpM", 0);
		else if(PostServlet.class.isAssignableFrom(policy))
			driver.put("httpM", 1);
		else 
			throw new WebServiceAnnotationMisuseException
			("WebService policy must be a descendant of one of the following : "
			+GetServlet.class.getCanonicalName()+","+PostServlet.class.getCanonicalName());

		return router.put(servletID,driver);
	}


	private void detectInitParamSettingFailure(
			boolean status,
			String servletID,
			String paramName
			) throws WebInitParameterSettingException{
		if(!status)
			throw new WebInitParameterSettingException
			(servletID+": Fail to set servlet init parameter '"+paramName+"'");
	}


	private static void detectParamNameCollision(
			String servletID,
			String group,
			Param[]...paramsTabTabs
			) throws WebServiceAnnotationMisuseException{
		Set<String>nameSet=new HashSet<>();	
		for(Param[]paramsTab : paramsTabTabs)
			for(Param param : paramsTab)
				if(!nameSet.add(param.value()))
					throw new WebServiceAnnotationMisuseException
					(servletID+": Collision detected between "+group+" parameters : '"+param.value()+"' is duplicated");
	}

}
