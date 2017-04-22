package com.aj.jeez.annotation.annotations;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.annotation.ElementType;

import javax.servlet.annotation.WebInitParam;

import com.aj.jeez.JEEZServlet;
import com.aj.jeez.policy.GetServlet;

/**
 * @author AJoan */

@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface WebService {   

	/**For WebServlet*/

	String value(); //urlPattern

	boolean asyncSupported() default false;

	WebInitParam[] initParams() default {};

	int loadOnStartup() default -1;

	/**For WebService*/

	Class<? extends JEEZServlet> policy() default GetServlet.class;

	boolean requireAuth() default false;

	Params requestParams() default @Params;

	Params jsonOutParams() default @Params ;

	Class<?>[] checkClasses()default {};
}