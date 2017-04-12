package com.aj.jeez.annotations;

import com.aj.jeez.annotation.exceptions.ParameterTypingException;

public class StaticTypeControler {


	public static boolean paramsAreValid(
			String className,
			String servletName,
			String... typedParams
			) throws ParameterTypingException{
		for(String typedParam:typedParams)
			paramIsValid(className,servletName,typedParam);
		return true;
	}


	public static boolean paramIsValid(
			String className,
			String servletName,
			String typedParam
			) throws ParameterTypingException{		
		String[] typedParameterTab = typedParam.split("\\:");

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
