package com.aj.jeez.annotations.core;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.aj.jeez.annotations.Checkout;
import com.aj.jeez.annotations.exceptions.CheckoutAnnotationMisuseException;

/**
 * The CheckoutsRadar is a checkouts finder from built classes (.class extension).
 * It scans the class and returns the methods annotated with the @Checkout annotation.   
 * @author Joan */
public class CheckoutsRadar {

	/**
	 * Return a map containing all the methods 
	 * annotated with the @Checkout annotation 
	 * for each class in the {classSet}
	 * @param classesSet
	 * @return 
	 * @throws CheckoutAnnotationMisuseException */
	public static Map<Class<?>,Set<Method>> findAnnotatedServices (
			Set<Class<?>> classSet
			) throws CheckoutAnnotationMisuseException{
		Map<Class<?>,Set<Method>> classesMethods = new HashMap<>();
		for(Class<?> clazz : classSet)
			classesMethods.put(clazz,findAnnotatedServices(clazz));
		return classesMethods;
	}


	/**
	 * Find all the methods annotated with the 
	 * @Checkout annotation in the class {clazz}
	 * @param clazz
	 * @return
	 * @throws CheckoutAnnotationMisuseException */
	public static Set<Method> findAnnotatedServices (
			Class<?> clazz
			) throws CheckoutAnnotationMisuseException {
		String str0="The registred Checkout : 'public static boolean ";
		String str1 = "(Object result,String[]expectedOut,String[]optionalOut);' must ";
		Set<Method> servicesSet = new HashSet<>();
	
		try {
			for ( Method m : clazz.getMethods() ) {

				if(m.getAnnotation(Checkout.class)==null) continue;

				String msg =str0+clazz.getName()+"."+m.getName()+str1;

				if (Modifier.isAbstract(m.getModifiers())) 
					throw new CheckoutAnnotationMisuseException(msg+"not be abstract");

				if (!Modifier.isPublic(m.getModifiers())) 
					throw new CheckoutAnnotationMisuseException(msg+"be public");

				if (!Modifier.isStatic(m.getModifiers()) ) 
					throw new CheckoutAnnotationMisuseException(msg+"be static");
				
				if (!(m.getReturnType().equals(boolean.class) || m.getReturnType().equals(Boolean.class))) 
					throw new CheckoutAnnotationMisuseException(msg+"return a boolean or a Boolean");

				if (m.getExceptionTypes().length>0) 
					throw new CheckoutAnnotationMisuseException(msg+"not throws exceptions");
				
				Class<?>[] parameterTypes = m.getParameterTypes();
				if (parameterTypes.length != 3) 
					throw new CheckoutAnnotationMisuseException(msg+"have exactly 3 arguments of the following types in this order :  Object,String[],String[]");
				else
					if(!(	Object.class.isAssignableFrom(parameterTypes[0])
							&&String[].class.isAssignableFrom(parameterTypes[1])
							&&String[].class.isAssignableFrom(parameterTypes[2])
							))
						throw new CheckoutAnnotationMisuseException(msg+"have exactly 3 arguments of the following types in this order :  Object,String[],String[]");

				 
				servicesSet.add(m);				
			}
			return servicesSet;
		} catch (SecurityException e1) {throw new RuntimeException(e1);}
	}
}