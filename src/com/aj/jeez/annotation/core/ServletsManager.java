package com.aj.jeez.annotation.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.http.HttpServlet;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

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

public class ServletsManager {

	public static String JZID=DigestUtils.shaHex("JZ"+new Date().toString()+"JZ");

	static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException, WebInitParameterSettingException{
		
		HashSet<String> hs = new HashSet<>();
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			for(Method service : classServices.getValue()){
				WebService ws = service.getAnnotation(WebService.class);
				String wsid=ws.ID();
				if(!hs.add(wsid) || wsid.length()==0)
					throw new WebServiceAnnotationMisuseException
					("Invalid service ID detected '"+wsid+"' for WebService '"+classServices.getKey().getCanonicalName()+"."+service.getName()+"' : A service ID must be unique and not empty");
			}		

		JSONObject router = new JSONObject();
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignClassServlets(sc,classServices,router);
		return router;
	}


	static JSONObject assignClassServlets(
			ServletContext sc,
			Entry<Class<?>,Set<Method>> classServices,
			JSONObject router
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException, WebInitParameterSettingException{
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
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException, WebInitParameterSettingException{

		String servletID = className+"."+service.getName()+"Servlet";

		WebService ws = service.getAnnotation(WebService.class);

		String id = ws.ID();
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


		/*** DRIVER SETTINGS PHASE */

		//Servlet communication driver settings
		JSONObject driver = new JSONObject()
				.put("sid",id)
				.put("url",urlPattern)
				.put("auth",auth)
				.put("httpM", determineHTTPMethod(policy))
				.put("expIN",stretchParams(expIN))
				.put("expOut",stretchParams(expOut))
				.put("optIN",stretchParams(optIN))
				.put("optOut",stretchParams(optOut));


		/*** REGISTRATION PHASE */

		//Servlet parameters setting
		ServletRegistration.Dynamic sr = sc.addServlet(servletID,policy);
		sr.addMapping(urlPattern);
		sr.setAsyncSupported(ws.asyncSupported());
		sr.setLoadOnStartup(ws.loadOnStartup());		
		for(WebInitParam wip:ws.initParams())
			detectInitParamSettingFailure(
					sr.setInitParameter(wip.name(),wip.value()),servletID,wip.name());

		//Service parameters setting
		JSONObject jzParams=new JSONObject(driver.toMap())
				.put("ck",clazzTab)
				.put("sc", className)
				.put("sm", service.getName());
		detectInitParamSettingFailure(sr.setInitParameter(JZID,jzParams.toString()),servletID,JZID);

		return router.put(servletID,driver);
	}


	private static void detectInitParamSettingFailure(
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

	private static int determineHTTPMethod(
			Class<?> policy
			) throws WebServiceAnnotationMisuseException{
		if(GetServlet.class.isAssignableFrom(policy))
			return 0;
		else if(PostServlet.class.isAssignableFrom(policy))
			return 1;
		else 
			throw new WebServiceAnnotationMisuseException
			("WebService policy must be a descendant of one of the following : "
					+GetServlet.class.getCanonicalName()+","+PostServlet.class.getCanonicalName());

	}


	private static String stretchParams(
			Param[]params
			){
		JSONArray jar = new JSONArray();
		for(Param param : params){
			int typeInt = 0; //String by default
			Class<?> type=param.type();
			if(int.class.isAssignableFrom(type)) typeInt=1;
			if(long.class.isAssignableFrom(type))typeInt=2;
			if(float.class.isAssignableFrom(type))typeInt=3;
			if(double.class.isAssignableFrom(type))typeInt=4;
			if(boolean.class.isAssignableFrom(type))typeInt=5;
			jar.put(new JSONObject()
					.put("name", param.value())
					.put("type", typeInt)
					.put("rules", param.rules()));
		}
		return jar.toString();
	}

}
