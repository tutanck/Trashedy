package com.aj.jeez.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebInitParam;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.core.exceptions.CheckoutAnnotationMisuseException;
import com.aj.jeez.core.exceptions.InconsistentParametersException;
import com.aj.jeez.core.exceptions.JEEZError;
import com.aj.jeez.core.exceptions.ParamNamingException;
import com.aj.jeez.core.exceptions.ParamRulingException;
import com.aj.jeez.core.exceptions.ParamTypingException;
import com.aj.jeez.core.exceptions.WebInitParameterSettingException;
import com.aj.jeez.core.exceptions.WebServiceAnnotationMisuseException;
import com.aj.jeez.defaults.checks.CheckExpectedOut;
import com.aj.jeez.defaults.policy.GetServlet;
import com.aj.jeez.defaults.policy.PostServlet;
import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.jeez.representation.templates.JEEZServletDriver;
import com.aj.jeez.representation.templates.TemplateParam;
import com.aj.jeez.representation.templates.TemplateParams;

public class ServletsManager {

	static final String JZID=DigestUtils.shaHex("JZ"+new Date()+"JZ");

	static final Map<String,JEEZServletDriver> server_router =  new HashMap<>();

	static JSONObject assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, 
	ParamTypingException, ClassNotFoundException, ParamNamingException,
	WebInitParameterSettingException, ParamRulingException,
	CheckoutAnnotationMisuseException, InconsistentParametersException{

		HashSet<String> hs = new HashSet<>();
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			for(Method service : classServices.getValue()){
				WebService ws = service.getAnnotation(WebService.class);
				String wsid=ws.value().trim();
				if(wsid.length() == 0 || !hs.add(wsid))
					throw new WebServiceAnnotationMisuseException
					("Invalid service url pattern detected '"+wsid+"' for WebService '"+classServices.getKey().getCanonicalName()+"."+service.getName()+"' : A service url must be unique and not empty");
			}		

		JSONObject client_router = new JSONObject();
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignClassServlets(sc,classServices,client_router);
		return client_router;
	}


	static JSONObject assignClassServlets(
			ServletContext sc,
			Entry<Class<?>,Set<Method>> classServices,
			JSONObject client_router
			) throws WebServiceAnnotationMisuseException,
	ParamTypingException, ClassNotFoundException, ParamNamingException,
	WebInitParameterSettingException, ParamRulingException,
	CheckoutAnnotationMisuseException, InconsistentParametersException{
		Class<?> clazz = classServices.getKey();
		String className = clazz.getCanonicalName();

		for(Method service : classServices.getValue())
			assignServiceServlet(sc,className,service,client_router);
		return client_router;
	}


	static JSONObject assignServiceServlet(
			ServletContext sc,
			String className,
			Method service,
			JSONObject client_router
			) throws WebServiceAnnotationMisuseException, 
	ParamTypingException, ClassNotFoundException, ParamNamingException, 
	WebInitParameterSettingException, ParamRulingException, 
	CheckoutAnnotationMisuseException, InconsistentParametersException {

		String serviceID = className+"."+service.getName();

		WebService ws = service.getAnnotation(WebService.class);

		String url = ws.value().trim();
		boolean requireAuth = ws.requireAuth();
		Class<? extends JEEZServlet>policy = ws.policy();
		Set<Class<?>> checkClazzs = new HashSet<>(Arrays.asList(ws.checkClasses()));
		
		TemplateParams rp ,jop;

		try {	
			 rp = ParamsTranslator.translate(ws.requestParams());
			 jop = ParamsTranslator.translate(ws.jsonOutParams());
		} 
		catch (ParamNamingException e) {
			throw new ParamNamingException
			("WebService '"+service.getName()+"' in class '"+className+"' specifies an invalid parameter name : "+e);
		}
		catch (ParamTypingException e) {
			throw new ParamTypingException
			("WebService '"+service.getName()+"' in class '"+className+"' specifies invalid type : "+e);
		} 
		catch (ParamRulingException e) {
			throw new ParamRulingException
			("WebService '"+service.getName()+"' in class '"+className+"' specifies an invalid rule : "+e);
		}		

		Set<TemplateParam> expIN=rp.getExpecteds();
		Set<TemplateParam> optIN=rp.getOptionals();
		Set<TemplateParam> expOut=jop.getExpecteds();
		Set<TemplateParam> optOut=jop.getOptionals();


		/*** VERIFICATION PHASE */

		//Policy checking 
		if(Modifier.isAbstract(policy.getModifiers()))
			throw new WebServiceAnnotationMisuseException(serviceID+" : Dynamic sevlet policy class : '"+className+"' must not be abstract");				

		//JSONOUTParams use --> return type checking
		if((expOut.size()>0 ||optOut.size()>0))
			if( !JSONObject.class.isAssignableFrom(service.getReturnType()))
				throw new WebServiceAnnotationMisuseException(serviceID+" : Declaring at least one JSONOUTParam force the WebService return type to be JSONObject or descendant");
			else 
				checkClazzs.add(CheckExpectedOut.class); //default checkout

		//Test class existence (check if the class is loaded in the webApp container)
		for(Class<?> checkClazz : checkClazzs) 
			Class.forName(checkClazz.getCanonicalName());


		/*** REGISTRATION PHASE */

		ServletRegistration.Dynamic sr = sc.addServlet(serviceID,policy);
		sr.addMapping(url);
		sr.setAsyncSupported(ws.asyncSupported());
		sr.setLoadOnStartup(ws.loadOnStartup());		
		for(WebInitParam wip:ws.initParams())
			detectInitParamSettingFailure(sr.setInitParameter(wip.name(),wip.value()),serviceID,wip.name());
		detectInitParamSettingFailure(sr.setInitParameter(JZID,url),serviceID,JZID);


		/*** DRIVERS SETTINGS PHASE */

		//Service driver setting
		server_router.put(url,
				new JEEZServletDriver(
						url, className,	service.getName(),
						determineHTTPMethodCode(policy),
						requireAuth, policy, rp, jop,
						CheckoutsRadar.findAnnotatedCheckouts(
								checkClazzs
								)));

		//Servlet communication driver settings
		JSONObject jzcDriver = new JSONObject()				
				.put("auth",requireAuth)
				.put("httpm",determineHTTPMethod(policy))
				.put("httpmc",determineHTTPMethodCode(policy))
				.put("expin",serialize(expIN))
				.put("expout",serialize(expOut))
				.put("optin",serialize(optIN))
				.put("optout",serialize(optOut));
		client_router.put(url,jzcDriver);

		return client_router;
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


	private static String determineHTTPMethod(
			Class<?> policy
			) throws WebServiceAnnotationMisuseException{
		if(GetServlet.class.isAssignableFrom(policy))
			return "get";
		else if(PostServlet.class.isAssignableFrom(policy))
			return "post";
		else 
			throw new WebServiceAnnotationMisuseException
			("WebService policy must be a descendant of one of the following : "
					+GetServlet.class.getCanonicalName()+","+PostServlet.class.getCanonicalName());
	}


	private static int determineHTTPMethodCode(
			Class<?> policy
			) throws WebServiceAnnotationMisuseException {
		if(GetServlet.class.isAssignableFrom(policy))
			return 0;
		else if(PostServlet.class.isAssignableFrom(policy))
			return 1;
		else 
			throw new WebServiceAnnotationMisuseException
			("WebService policy must be a descendant of one of the following : "
					+GetServlet.class.getCanonicalName()+","+PostServlet.class.getCanonicalName());
	}

	private static JSONArray serialize(
			Set<TemplateParam> params
			){
		JSONArray jar = new JSONArray();

		for(TemplateParam param : params){
			JSONObject jo = new JSONObject();
			try {
				jo.put("type", param.typeToInt());
			}catch (ParamTypingException e) {
				throw new JEEZError("#SNO : internal typing error");
			}
			jar.put(jo
					.put("name", param.getName())
					.put("rules", param.getRules()));
		}
		return jar;
	}


}