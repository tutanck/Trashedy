package com.aj.jeez.templating;

import java.util.HashSet;
import java.util.Set;

public class TemplateParam {

	private final String name;

	private final Class<?> type;

	private final Set<String> rules;

	public TemplateParam(String name){
		this(name,String.class,null);
	}

	public TemplateParam(String name,Class<?> type){
		this(name,type,null);
	}

	public TemplateParam(
			String name, //TODO interdire les caracteres invalides
			Class<?> type,
			Set<String> rules
			){
		if(name.length()==0)
			throw new IllegalArgumentException("Name must not be empty");

		for(String rule : rules)
			if(rule.length()==0)
				throw new IllegalArgumentException("Each rule must not be empty");

		if(type==String.class
				||type==int.class
				||type==long.class
				||type==float.class
				||type==double.class
				||type==boolean.class)
			this.type=type;
		else 
			throw new IllegalArgumentException
			("Param type must only be int,long,float,double,boolean or String");

		this.name=name;
		this.rules=(rules!=null?rules:new HashSet<>());
	}


	@Override
	public boolean equals(Object obj) {
		if(obj==null)return false; 
		return obj instanceof TemplateParam ? 
				((TemplateParam)obj).name.equals(name) : false;
	}

	@Override
	public String toString() {
		return "(name:"+name+",type:"+type+",rules"+rules+")";
	}

	public Set<String> getRules() {return rules;}
	public Class<?> getType() {return type;}
	public String getName() {return name;}
}
