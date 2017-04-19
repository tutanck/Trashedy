package com.aj.jeez.annotation.core;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.exceptions.ParameterNamingException;
import com.aj.jeez.annotation.exceptions.ParameterTypingException;


public class FormalParamTypeControler {
	
	 static void paramsAreValid(
			String className,
			String servletName,
			Param[]... paramsTabs
			) throws ParameterTypingException, ParameterNamingException{
		for(Param[] params:paramsTabs)
			paramsAreValid(className,servletName,params);
	}
	
	static void paramsAreValid(
			String className,
			String servletName,
			Param... params
			) throws ParameterTypingException, ParameterNamingException{
		for(Param param:params)
			paramIsValid(className,servletName,param);
	}


	static void paramIsValid(
			String className,
			String serviceName,
			Param param
			) throws ParameterTypingException, ParameterNamingException{		
		String paramName = param.value().trim();

		if(paramName.length()==0)
			throw new ParameterNamingException
			("WebService '"+serviceName+"' in class '"+className+"' specifies an empty parameter name");
		//TODO interdire les caracteres spéciaux qui ne passe pas ds l url 

		if(!typeIsValid(param.type()))
			throw new ParameterTypingException
			("WebService '"+serviceName+"' in class '"+className+"' specifies unsupported type for parameter '"+paramName+"'");
	}


	static boolean typeIsValid(
			Class<?> type
			){		
		if(int.class.isAssignableFrom(type)) return true;
		if(long.class.isAssignableFrom(type)) return true;
		if(float.class.isAssignableFrom(type)) return true;
		if(double.class.isAssignableFrom(type)) return true;
		if(boolean.class.isAssignableFrom(type)) return true;
		if(String.class.isAssignableFrom(type)) return true;
		return false;
	}

	 public static int typeToInt(
			Class<?> type
			) throws ParameterTypingException{
		if(String.class.isAssignableFrom(type)) return 0; 
		if(int.class.isAssignableFrom(type)) return 1; 
		if(long.class.isAssignableFrom(type))return 2; 
		if(float.class.isAssignableFrom(type))return 3; 
		if(double.class.isAssignableFrom(type))return 4; 
		if(boolean.class.isAssignableFrom(type))return 5;
		throw new ParameterTypingException("Unknown type '"+type+"'");
	}

}
