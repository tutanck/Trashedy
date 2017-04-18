package com.aj.jeez.annotation.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
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

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.jeez.annotation.exceptions.ParameterNamingException;
import com.aj.jeez.annotation.exceptions.ParameterTypingException;
import com.aj.jeez.annotation.exceptions.WebInitParameterSettingException;
import com.aj.jeez.annotation.exceptions.WebServiceAnnotationMisuseException;
import com.aj.jeez.policy.GetServlet;
import com.aj.jeez.policy.PostServlet;

public class ServletsManager {

	public static String JZID=DigestUtils.shaHex("JZ"+new Date()+"JZ");

	static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ParameterNamingException, WebInitParameterSettingException{
		
		HashSet<String> hs = new HashSet<>();
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			for(Method service : classServices.getValue()){
				WebService ws = service.getAnnotation(WebService.class);
				String wsid=ws.value();
				if(!hs.add(wsid) || wsid.length()==0)
					throw new WebServiceAnnotationMisuseException
					("Invalid service url pattern detected '"+wsid+"' for WebService '"+classServices.getKey().getCanonicalName()+"."+service.getName()+"' : A service url must be unique and not empty");
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

		String serviceID = className+"."+service.getName();

		WebService ws = service.getAnnotation(WebService.class);

		String url = ws.value();
		boolean auth=ws.requireAuth();
		Params rp = ws.requestParams();
		Params jop = ws.jsonOutParams();

		Param [] expIN=rp.value();
		Param [] optIN=rp.optionals();
		Param [] expOut=jop.value();
		Param [] optOut=jop.optionals();

		Class<? extends HttpServlet>policy=ws.policy();
		Class<?>[]clazzTab = ws.checkClasses();


		/*** VERIFICATION PHASE */

		//Policy checking 
		if(Modifier.isAbstract(policy.getModifiers()))
			throw new WebServiceAnnotationMisuseException(serviceID+" : Dynamic sevlet policy class : '"+className+"' must not be abstract");				

		//JSONOUTParams use --> return type checking
		if((optOut.length>0 || expOut.length>0)&& !JSONObject.class.isAssignableFrom(service.getReturnType()))
			throw new WebServiceAnnotationMisuseException(serviceID+" : Declaring at least one JSONOUTParam force the WebService return type to be JSONObject or descendant");				
		
		//Static parameters typing test 
		StaticTypedParamControler.paramsAreValid(className,serviceID,expIN,expOut,optIN,optOut);

		//Parameter name collisions detection
		detectParamNameCollision(serviceID,"request",expIN,optIN);
		detectParamNameCollision(serviceID,"jsonout",optOut,expOut);

		//Test class existence (check if the class is loaded in the webApp container)
		for(Class<?> checkClazz : clazzTab) 
			Class.forName(checkClazz.getCanonicalName());


		/*** DRIVER SETTINGS PHASE */

		//Servlet communication driver settings
		JSONObject jzcDriver = new JSONObject()				
				.put("auth",auth)
				.put("httpM", determineHTTPMethod(policy))
				.put("expIN",stretchParams(expIN))
				.put("expOut",stretchParams(expOut))
				.put("optIN",stretchParams(optIN))
				.put("optOut",stretchParams(optOut));


		/*** REGISTRATION PHASE */

		//Servlet parameters setting
		ServletRegistration.Dynamic sr = sc.addServlet(serviceID,policy);
		sr.addMapping(url);
		sr.setAsyncSupported(ws.asyncSupported());
		sr.setLoadOnStartup(ws.loadOnStartup());		
		for(WebInitParam wip:ws.initParams())
			detectInitParamSettingFailure(sr.setInitParameter(wip.name(),wip.value()),serviceID,wip.name());

		//Service parameters setting
		JSONObject jzsDriver=new JSONObject(jzcDriver.toMap())
				.put("url",url)
				.put("ckC",stretchClasses(clazzTab))
				.put("sC", className)
				.put("sM", service.getName());
		detectInitParamSettingFailure(sr.setInitParameter(JZID,jzsDriver.toString()),serviceID,JZID);

		return router.put(url,jzcDriver);
	}

	
	

	private static void detectInitParamSettingFailure(
			boolean status,
			String serviceID,
			String paramName
			) throws WebInitParameterSettingException{
		if(!status)
			throw new WebInitParameterSettingException
			(serviceID+": Fail to set servlet's init parameter '"+paramName+"'");
	}


	private static void detectParamNameCollision(
			String serviceID,
			String group,
			Param[]...paramsTabTabs
			) throws WebServiceAnnotationMisuseException{
		Set<String>nameSet=new HashSet<>();	
		for(Param[]paramsTab : paramsTabTabs)
			for(Param param : paramsTab)
				if(!nameSet.add(param.value()))
					throw new WebServiceAnnotationMisuseException
					(serviceID+": Collision detected between "+group+" parameters : '"+param.value()+"' is duplicated");
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


	private static JSONArray stretchParams(
			Param[]params
			){
		JSONArray jar = new JSONArray();
		for(Param param : params)
			jar.put(new JSONObject()
					.put("name", param.value())
					.put("type", param.type().getCanonicalName())
					.put("rules", param.rules()));
		return jar;
	}

	
	private static String stretchClasses(
			Class<?>[]clazzs
			){
		String s="";
		Iterator<Class<?>> it = new HashSet<>(Arrays.asList(clazzs)).iterator();
		
		while(it.hasNext()){
			s+=it.next().getCanonicalName();
			if(it.hasNext())s+=",";
		}
		return s;
	}
	
}
