package com.aj.jeez.templating;

import java.util.Set;

import com.aj.jeez.core.FormalParamTypeControler;
import com.aj.jeez.core.exceptions.ParamNamingException;
import com.aj.jeez.core.exceptions.ParamRulingException;
import com.aj.jeez.core.exceptions.ParamTypingException;
import com.aj.tools.__;

public class TemplateParam {

	private final String name;
	private final Class<?> type;
	private final Set<String> rules;

	public TemplateParam(
			String name
			) throws ParamTypingException, ParamNamingException, ParamRulingException{
		this(name,String.class,null);
	}

	public TemplateParam(
			String name,
			Class<?> type
			) throws ParamTypingException, ParamNamingException, ParamRulingException{
		this(name,type,null);
	}

	public TemplateParam(
			String name,
			Class<?> type,
			Set<String> rules
			) throws ParamTypingException, ParamNamingException, ParamRulingException{		
		FormalParamTypeControler.validParam(name, type, rules);

		this.type=type;
		this.name=name;
		this.rules=(__.stringSet(rules));
	}


	@Override
	public boolean equals(Object obj) {
 		if(obj==null)return false; 
		return obj instanceof TemplateParam ? 
				((TemplateParam)obj).name.equals(name) : false;
	}
	
	@Override
	public int hashCode() {
		return this.name.hashCode();
	}

	@Override
	public String toString() {
		return "(name:"+name+",type:"+type+",rules"+rules+")";
	}

	public Set<String> getRules() {return rules;}
	public Class<?> getType() {return type;}
	public String getName() {return name;}
}
