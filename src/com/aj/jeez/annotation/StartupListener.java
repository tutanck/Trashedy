package com.aj.jeez.annotation;
import java.lang.reflect.Method;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;

import com.aj.jeez.exceptions.JEEZException;
import com.aj.tools.PropertiesIO;


@WebListener 
public class StartupListener
implements ServletContextListener{

	private final String propFile="jeez.properties";
	private final String defaultClassPath="/WEB-INF/classes/";
	private String classPath=defaultClassPath;
	private String rootPackage="mood";

	//Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("-------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener started");
		System.out.println("-------------------------------------------------");

		try {
			/*PropertiesIO propIO=new PropertiesIO(propFile);

			if(!propIO.exist("rootpackage"))
				throw new JEEZException("The 'rootpackage' property must be defined in a '"+propFile+"' at the root of the project");

			rootPackage=propIO.get("rootpackage");

			if(propIO.exist("classpath"))
				classPath=propIO.get("classpath");
			else
				propIO.put("classpath->"+classPath);*/

			ClassPathScanner.configure(classPath, rootPackage);

			Set<String> classesQNSet;
			Map<Class<?>,Set<Method>> servicesMap;

			classesQNSet = ClassPathScanner.getClassesQualifiedNames(sce.getServletContext());
			System.out.println("StartupListener/contextInitialized:: classes qualified names list : ");//debug
			for(String classQN :classesQNSet)
				System.out.println("StartupListener/contextInitialized::classQualifiedName : "+classQN);//debug

			servicesMap = ServicesRadar.findAnnotatedServices(classesQNSet);
			System.out.println("StartupListener/contextInitialized::services list by class : ");//debug
			for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
				System.out.println("StartupListener/contextInitialized::servicesMap : "+classServices);//debug

			System.out.println("StartupListener/contextInitialized::assignServlets starting..");//debug
			ServletsManager.assignServlets(sce.getServletContext(),servicesMap);
			System.out.println("StartupListener/contextInitialized::assignServlets was sucessfull");//debug

		} catch (Exception e) {throw new RuntimeException(e);}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("---------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener destroyed");
		System.out.println("---------------------------------------------------");
	}
}