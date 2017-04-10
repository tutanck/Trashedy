package com.aj.jeez.codegen;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.aj.jeez.JEEZServlet;
import com.aj.jeez.codegen.exceptions.ParameterTypingException;
import com.aj.jeez.codegen.exceptions.WebServiceAnnotationMisuseException;
import com.aj.tools.Utils;

public class ServletsManager {

	public static void assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException{
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignServlet(sc,classServices);
	}


	public static void assignServlet(
			ServletContext sc,
			Entry<Class<?>, Set<Method>> classServices
			) throws WebServiceAnnotationMisuseException, ParameterTypingException, ClassNotFoundException{
		Class<?> clazz = classServices.getKey();
		String className = clazz.getCanonicalName();

		for(Method service : classServices.getValue()){
			WebService ws = service.getAnnotation(WebService.class);
			// Register Servlet
			if(Modifier.isAbstract(ws.policy().getModifiers()))
				throw new WebServiceAnnotationMisuseException("Dynamic sevlet policy class : '"+className+"' must not be abstract");

			ServletRegistration sr = sc.addServlet(ws.webServlet().name(),ws.policy());
			sr.addMapping(ws.webServlet().urlPatterns());
			//TODO completude http://docs.oracle.com/javaee/6/api/javax/servlet/annotation/WebServlet.html

			//Static parameters typing test 
			StaticTypeControler.paramsAreValid(className,ws.webServlet().name(),ws.expectedIn());
			StaticTypeControler.paramsAreValid(className,ws.webServlet().name(),ws.expectedOut());
			StaticTypeControler.paramsAreValid(className,ws.webServlet().name(),ws.optionalIn());
			StaticTypeControler.paramsAreValid(className,ws.webServlet().name(),ws.optionalOut());

			String clazzSetStr="";
			int i=0;
			
			//test classes presence test by a mockable instantiation and listing/setting
			Class<?>[]clazzTab = ws.testClasses();
			for(Class<?> testClazz : clazzTab){
				Class.forName(testClazz.getCanonicalName());
				if(i++<clazzTab.length-1)
					clazzSetStr+=testClazz.getCanonicalName()+",";
				else
					clazzSetStr+=testClazz.getCanonicalName();
			}

			if(JEEZServlet.class.isAssignableFrom(ws.policy())){
				System.out.println("Yay! '"+ws.policy()+"' is a JEEZServlet :)");
				sr.setInitParameter("expectedIn", Utils.join(ws.expectedIn()));
				sr.setInitParameter("expectedOut", Utils.join(ws.expectedOut()));
				sr.setInitParameter("optionalIn", Utils.join(ws.optionalIn()));
				sr.setInitParameter("optionalOut", Utils.join(ws.optionalOut()));
				sr.setInitParameter("requireAuth", ws.requireAuth()?"true":"false"); 
				sr.setInitParameter("testClasses", clazzSetStr);
			}



			/**
			 * TODO Returns: http://docs.oracle.com/javaee/6/api/javax/servlet/Registration.html#setInitParameters(java.util.Map)
			 * setInitParameters
java.util.Set<java.lang.String> setInitParameters(java.util.Map<java.lang.String,java.lang.String> initParameters)
Sets the given initialization parameters on the Servlet or Filter that is represented by this Registration. 
The given map of initialization parameters is processed by-value, i.e., for each initialization parameter contained in the map, this method calls setInitParameter(String,String). If that method would return false for any of the initialization parameters in the given map, no updates will be performed, and false will be returned. Likewise, if the map contains an initialization parameter with a null name or value, no updates will be performed, and an IllegalArgumentException will be thrown. 
true if the update was successful, i.e., an initialization parameter with the given name did not already exist for the Servlet or Filter represented by this Registration, and false otherwise 
			 */

			sr.setInitParameter("serviceClass", className);
			sr.setInitParameter("serviceMethod", service.getName());

			//TODO COMPLETUDE webServlet
		}
	}
}
