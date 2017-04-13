package com.aj.jeez.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

/**
 * @author AJoan */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebService {
	
   WebServlet webServlet();
   
   Class<? extends HttpServlet> policy() ; 
   
   boolean requireAuth() default false;
   
   String [] expectedIn()default {}; 
   
   String [] expectedOut()default {};
   
   String [] optionalIn()default {};
   
   String [] optionalOut()default {};
   
   Class<?>[] checkClasses()default {};
}