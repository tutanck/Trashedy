package com.aj.jeez.annotation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import javax.servlet.annotation.WebInitParam;

import com.aj.jeez.JEEZServlet;

/**
 * @author AJoan */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebService {   
   
   /**For WebServlet*/
   
   boolean asyncSupported() default false;
   
   WebInitParam[] initParams() default {};
   
   int loadOnStartup() default -1;
   
   String urlPattern(); 
   
   /**For WebService*/
   
   String id();
   
   Class<? extends JEEZServlet> policy();
   
   boolean requireAuth() default false;
   
   RequestParams requestParams() default @RequestParams;
   
   JSONOUTParams jsonOutParams() default @JSONOUTParams ;
   
   Class<?>[] checkClasses()default {};
}