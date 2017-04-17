package com.aj.jeez.annotation.core;

import com.aj.jeez.annotation.exceptions.ParameterNamingException;
import com.aj.jeez.annotation.exceptions.ParameterTypingException;

public class StaticTypedParamControler {


	public static boolean paramsAreValid(
			String className,
			String servletName,
			String... typedParams
			) throws ParameterTypingException, ParameterNamingException{
		for(String typedParam:typedParams)
			paramIsValid(className,servletName,typedParam);
		return true;
	}


	public static boolean paramIsValid(
			String className,
			String servletName,
			String typedParam
			) throws ParameterTypingException, ParameterNamingException{		
		String[] typedParameterTab = typedParam.split("\\:");
		String paramName = typedParameterTab[0].trim();
		
		if(paramName.length()==0)
			throw new ParameterNamingException
			("The typed parameter '"+typedParam+"' specifies an empty name for servlet '"+servletName+"' in class '"+className+"'");
		//TODO interdire les caracteres spéciaux qui ne passe pas ds l url
		
		if (typedParameterTab.length >= 2) {//typedef is provided in the template
			String paramType = typedParameterTab[1].trim().toLowerCase();
			if(paramType.length()==0)
				return true;
			if(!typeIsValid(paramType))
				throw new ParameterTypingException
				("The typed parameter '"+typedParam+"' specifies an unknown type '"+paramType+"' for servlet '"+servletName+"' in class '"+className+"'");
		}
		return true;
	}


	public static boolean typeIsValid(
			String type
			){		
		switch (type) {
		case "int":return true;
		case "long":return true;
		case "float":return true;
		case "double":return true;
		case "boolean":return true;
		case "string" :return true;
		default:return false;
		}
	}

}
