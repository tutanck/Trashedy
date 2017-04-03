package com.aj.jeez.codegen;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.ServletRegistration;
import javax.servlet.annotation.WebListener;

@WebListener 
public class StartupListener
               implements ServletContextListener{

    //Run this before web application is started
	@Override
	public void contextInitialized(ServletContextEvent sce) {
		System.out.println("-------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener started");
		System.out.println("-------------------------------------------------");
		
		ServletContext sc = sce.getServletContext();
		// Register Servlet
		ServletRegistration sr = sc.addServlet("DynamicServlet",
		"mood.users.io.servlets.SignupServlet");
		sr.setInitParameter("servletInitName", "servletInitValue");
		sr.addMapping("/dynamic");
	}
	
	@Override
	public void contextDestroyed(ServletContextEvent sce) {
		System.out.println("---------------------------------------------------");
		System.out.println("StartupListener--> ServletContextListener destroyed");
		System.out.println("---------------------------------------------------");

	}
	
}