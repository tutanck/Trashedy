package com.aj.jeez.annotation.core;

import java.util.Set;
import java.util.regex.Pattern;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.exceptions.ParamNamingException;
import com.aj.jeez.annotation.exceptions.ParamRulingException;
import com.aj.jeez.annotation.exceptions.ParamTypingException;
import com.aj.tools.__;


public class FormalParamTypeControler {
	
	 static void paramsAreValid(
			String className,
			String servletName,
			Param[]... paramsTabs
			) throws ParamTypingException, ParamNamingException{
		for(Param[] params:paramsTabs)
			paramsAreValid(className,servletName,params);
	}
	
	static void paramsAreValid(
			String className,
			String servletName,
			Param... params
			) throws ParamTypingException, ParamNamingException{
		for(Param param:params)
			paramIsValid(className,servletName,param);
	}


	static void paramIsValid(
			String className,
			String serviceName,
			Param param
			) throws ParamNamingException, ParamTypingException{		
		try {
			validParam(param.value(),param.type(),__.toSet(param.rules()));
		} 
		catch (ParamNamingException e) {
			throw new ParamNamingException
			("WebService '"+serviceName+"' in class '"+className+"' specifies an invalid parameter name : "+e);
		}
		catch (ParamTypingException e) {
			throw new ParamTypingException
			("WebService '"+serviceName+"' in class '"+className+"' specifies invalid type for parameter '"+param.value()+"' : "+e);
		} 
		catch (ParamRulingException e) {
			throw new ParamNamingException
			("WebService '"+serviceName+"' in class '"+className+"' specifies an invalid rule for parameter '"+param.value()+"' : "+e);
		}			
	}
	
	
	
	public static void validParam(
			String name,
			Class<?> type,
			Set<String> rules
			)throws ParamTypingException, ParamNamingException, ParamRulingException{
		
		if(__.isWhiteSpace(name))
			throw new ParamNamingException("Param name must not be empty");
		
		if(!Pattern.compile("\\w+").matcher(name).matches())
			throw new ParamNamingException("Param name must only contain alphanumeric characters or/and _");

		if(!typeIsValid(type))
			throw new ParamTypingException("Param type must only be int,long,float,double,boolean or String");
		
		for(String rule : __.stringSet(rules))
			if(__.isWhiteSpace(rule))
				throw new ParamRulingException("Each rule must not be empty");
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
			) throws ParamTypingException{
		if(String.class.isAssignableFrom(type)) return 0; 
		if(int.class.isAssignableFrom(type)) return 1; 
		if(long.class.isAssignableFrom(type))return 2; 
		if(float.class.isAssignableFrom(type))return 3; 
		if(double.class.isAssignableFrom(type))return 4; 
		if(boolean.class.isAssignableFrom(type))return 5;
		throw new ParamTypingException("Unknown type '"+type+"'");
	}

}
