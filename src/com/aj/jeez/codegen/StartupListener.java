package com.aj.jeez.codegen;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;


@WebListener 
public class StartupListener
implements ServletContextListener{

	private String classesPath="/WEB-INF/classes/";
	private String packageName="mood";

	//Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("-------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener started");
		System.out.println("-------------------------------------------------");

		ClassPathScanner.configure(classesPath, packageName);

		Set<String> classesQNSet = new HashSet<>();
		Map<Class<?>,Set<Method>> servicesMap;

		try {
			classesQNSet = ClassPathScanner.getClassesQualifiedNames(classesPath+packageName,sce.getServletContext(),classesQNSet);
			System.out.println();
			for(String classQN :classesQNSet)
				System.out.println("ClassPathScanner/getClassFiles::classQualifiedName : "+classQN);//debug

			servicesMap = ServicesRadar.findAnnotatedServices(classesQNSet);
			System.out.println();
			for(Entry<Class<?>, Set<Method>> classServices : servicesMap.entrySet())
				System.out.println("StartupListener/contextInitialized::servicesMap : "+classServices);//debug

			System.out.println("StartupListener/contextInitialized::assignServlets : starting..");//debug
			ServletsManager.assignServlets(sce.getServletContext(),servicesMap);
			System.out.println("StartupListener/contextInitialized::assignServlets : sucessfull");//debug

		} catch (Exception e) {throw new RuntimeException(e);}
	}

	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("---------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener destroyed");
		System.out.println("---------------------------------------------------");
	}
}