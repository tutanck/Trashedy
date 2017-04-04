package com.aj.jeez.codegen;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
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
		
		Set<String> finalList = new HashSet<>();
		try {
			ClassPathScanner.configure(classesPath, packageName);
			System.out.println("LOL : "+ClassPathScanner.getClasses(
					classesPath+packageName,sce.getServletContext(), finalList));
		} catch (ClassPathScannerNotConfiguredException e) {throw new RuntimeException(e);}
		
		
		
		
		
		
		
		
		/*ServletContext sc = sce.getServletContext();
		// Register Servlet
		ServletRegistration sr = sc.addServlet("DynamicServlet",
		"mood.users.io.servlets.SignupServlet");
		sr.setInitParameter("servletInitName", "servletInitValue");
		sr.addMapping("/dynamic");*/
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("---------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener destroyed");
		System.out.println("---------------------------------------------------");

	}
	
}