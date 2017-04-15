package com.aj.jeez.annotations.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebInitParam;
import javax.servlet.annotation.WebServlet;

import com.aj.jeez.JEEZServlet;
import com.aj.jeez.annotations.WebService;
import com.aj.jeez.annotations.exceptions.ParameterTypingException;
import com.aj.jeez.annotations.exceptions.ServletInstantiationExceptionAdvise;
import com.aj.jeez.annotations.exceptions.WebServiceAnnotationMisuseException;
import com.aj.tools.Stretch;

public class ServletsManager {

	public static void assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ServletInstantiationExceptionAdvise{
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignServlet(sc,classServices);
	}


	public static void assignServlet(
			ServletContext sc,
			Entry<Class<?>, Set<Method>> classServices
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException, ServletInstantiationExceptionAdvise{
		Class<?> clazz = classServices.getKey();
		String className = clazz.getCanonicalName();

		for(Method service : classServices.getValue()){
			WebService ws = service.getAnnotation(WebService.class);
			// Register Servlet
			if(Modifier.isAbstract(ws.policy().getModifiers()))
				throw new WebServiceAnnotationMisuseException("Dynamic sevlet policy class : '"+className+"' must not be abstract");
			
			WebServlet webServlet = ws.webServlet();

			String servletID = webServlet.name();

			if(servletID.length()==0){
				servletID=className+"."+service.getName()+"Servlet";
				System.out.println("Empty Servlet name : JEEZ will choose a name.. what about '"+servletID+"'");
			}

			ServletRegistration.Dynamic sr = sc.addServlet(servletID,ws.policy());
			try{
				sr.addMapping(webServlet.urlPatterns());
			}catch(NullPointerException npe)
			{
				npe.printStackTrace();
				throw new ServletInstantiationExceptionAdvise
				("Advise : Check if two differents servlets have the same name.");
			}
		
			for(WebInitParam wip:webServlet.initParams())//TODO tester
				sr.setInitParameter(wip.name(),wip.value());
			
			sr.setLoadOnStartup(webServlet.loadOnStartup());
			sr.setAsyncSupported(webServlet.asyncSupported());

			//Static parameters typing test 
			StaticTypeControler.paramsAreValid(className,webServlet.name(),ws.expectedIn());
			StaticTypeControler.paramsAreValid(className,webServlet.name(),ws.expectedOut());
			StaticTypeControler.paramsAreValid(className,webServlet.name(),ws.optionalIn());
			StaticTypeControler.paramsAreValid(className,webServlet.name(),ws.optionalOut());

			String clazzSetStr="";
			int i=0;
			//test classes presence test by a mockable instantiation and listing/setting
			Class<?>[]clazzTab = ws.checkClasses();
			for(Class<?> checkClazz : clazzTab){
				Class.forName(checkClazz.getCanonicalName());
				if(i++<clazzTab.length-1)
					clazzSetStr+=checkClazz.getCanonicalName()+",";
				else
					clazzSetStr+=checkClazz.getCanonicalName();
			}

			if(JEEZServlet.class.isAssignableFrom(ws.policy())){
				sr.setInitParameter("expectedIn", Stretch.join(ws.expectedIn()));
				sr.setInitParameter("expectedOut", Stretch.join(ws.expectedOut()));
				sr.setInitParameter("optionalIn", Stretch.join(ws.optionalIn()));
				sr.setInitParameter("optionalOut", Stretch.join(ws.optionalOut()));
				sr.setInitParameter("requireAuth", ws.requireAuth()?"true":"false");
				sr.setInitParameter("checkClasses", clazzSetStr);
			}


			/**
			 * TODO Returns: http://docs.oracle.com/javaee/6/api/javax/servlet/Registration.html#setInitParameters(java.util.Map)
			 * setInitParameters
java.util.Set<java.lang.String> setInitParameters(java.util.Map<java.lang.String,java.lang.String>�initParameters)
Sets the given initialization parameters on the Servlet or Filter that is represented by this Registration. 
The given map of initialization parameters is processed by-value, i.e., for each initialization parameter contained in the map, this method calls setInitParameter(String,String). If that method would return false for any of the initialization parameters in the given map, no updates will be performed, and false will be returned. Likewise, if the map contains an initialization parameter with a null name or value, no updates will be performed, and an IllegalArgumentException will be thrown. 
true if the update was successful, i.e., an initialization parameter with the given name did not already exist for the Servlet or Filter represented by this Registration, and false otherwise 
			 */

			sr.setInitParameter("serviceClass", className);
			sr.setInitParameter("serviceMethod", service.getName());
		}
	}
}