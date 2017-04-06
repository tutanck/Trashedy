package com.aj.jeez.codegen;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.json.JSONObject;

import com.aj.jeez.codegen.exceptions.WebServiceAnnotationMisuseException;

public class ServicesRadar {

	public static Map<Class<?>,Set<Method>> findAnnotatedServices (
			Set<String> classesQNSet
			) throws ClassNotFoundException, WebServiceAnnotationMisuseException{
		Map<Class<?>,Set<Method>> classesMethods = new HashMap<>();
		for(String classQN : classesQNSet){
			Class<?> clazz =  Class.forName(classQN);
			classesMethods.put(clazz,findAnnotatedServices(clazz));
		}
		return classesMethods;
	}

	public static Set<Method> findAnnotatedServices (
			String classQN
			) throws ClassNotFoundException, WebServiceAnnotationMisuseException{
		return findAnnotatedServices(Class.forName(classQN));
	}


	public static Set<Method> findAnnotatedServices (
			Class<?> clazz
			) throws WebServiceAnnotationMisuseException {
		String str0="The registred WebService : 'public static JSONObject ";
		String str1 = "(JSONObject jsonObject);' must ";
		Set<Method> servicesSet = new HashSet<>();
		
		try {
			for ( Method m : clazz.getMethods() ) {

				if(m.getAnnotation(WebService.class)==null) continue;

				String msg =str0+clazz.getName()+"."+m.getName()+str1;

				if (Modifier.isAbstract(m.getModifiers())) 
					throw new WebServiceAnnotationMisuseException(msg+"not be abstract");
				
				if (!Modifier.isPublic(m.getModifiers())) 
					throw new WebServiceAnnotationMisuseException(msg+"be public");

				if (!Modifier.isStatic(m.getModifiers()) ) 
					throw new WebServiceAnnotationMisuseException(msg+"be static");

				Class<?>[] parameterTypes = m.getParameterTypes();
				if ( parameterTypes.length != 1 || !JSONObject.class.isAssignableFrom(parameterTypes[0]) ) 
					throw new WebServiceAnnotationMisuseException(msg+"have exactly one argument of type JSONObject");

				servicesSet.add(m);				
			}
			return servicesSet;
		} catch (SecurityException e1) {throw new RuntimeException(e1);}
	}
}
