package com.aj.jeez.codegen;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import javax.servlet.annotation.WebServlet;

/**
 * @author AJoan */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebService {
	
   WebServlet webServlet();
   
   Class parent() ; 
   
   String [] expectedIn()default {}; 
   
   String [] expectedOut()default {};
   
   String [] optionalIn()default {};
   
   String [] optionalOut()default {};
   
   boolean requireAuth() default false;
   
   Class[] testClasses()default {};
}