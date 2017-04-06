package com.aj.jeez.codegen;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletRegistration;

import com.aj.jeez.codegen.exceptions.WebServiceAnnotationMisuseException;
import com.aj.utils.Utils;

public class ServletsManager {

	public static void assignServlets(
			ServletContext sc,
			Map<Class<?>, Set<Method>> servicesMap
			) throws WebServiceAnnotationMisuseException{
		for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
			assignServlet(sc,classServices);
	}


	public static void assignServlet(
			ServletContext sc,
			Entry<Class<?>, Set<Method>> classServices
			) throws WebServiceAnnotationMisuseException{
		Class<?> clazz = classServices.getKey();
		String className = clazz.getCanonicalName();

		for(Method service : classServices.getValue()){
			WebService ws = service.getAnnotation(WebService.class);
			// Register Servlet
			if(Modifier.isAbstract(ws.parent().getModifiers()))
				throw new WebServiceAnnotationMisuseException("Dynamic sevlet parent : '"+className+"' must not be abstract");
			
			ServletRegistration sr = sc.addServlet(ws.webServlet().name(),ws.parent());
			sr.addMapping(ws.webServlet().urlPatterns());
			sr.setInitParameter("expectedIn", Utils.join(ws.expectedIn()));
			sr.setInitParameter("expectedOut", Utils.join(ws.expectedOut()));
			sr.setInitParameter("optionalIn", Utils.join(ws.optionalIn()));
			sr.setInitParameter("optionalOut", Utils.join(ws.optionalOut()));
			sr.setInitParameter("requireAuth", ws.requireAuth()?"true":"false"); 
			sr.setInitParameter("testClasses", Utils.joinClasses(ws.testClasses()));
			
			sr.setInitParameter("serviceClass", className);
			sr.setInitParameter("serviceMethod", service.getName());
			
			//TODO COMPLETUDE webServlet
		}
	}
}
