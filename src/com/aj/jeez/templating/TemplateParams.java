package com.aj.jeez.templating;

import java.util.HashSet;
import java.util.Set;

import com.aj.tools.__;

public class TemplateParams {

	private final Set<TemplateParam> expecteds;
	private final Set<TemplateParam> optionals;

	public TemplateParams(){
		this.expecteds=new HashSet<>();
		this.optionals=new HashSet<>();
	}

	public boolean addExpected(TemplateParam expected){
		if(expected==null) return false;
		if(expecteds.contains(expected)) return false;
		return __.truthy(expecteds.add(expected));
		
	}
	

	public boolean addOptional(TemplateParam optional){
		if(optional==null) return false;
		if(expecteds.contains(optional)) return false;
		return __.truthy(optionals.add(optional)); //TODO why return always true
	}

	public Set<TemplateParam> getOptionals() {return optionals;}
	public Set<TemplateParam> getExpecteds() {return expecteds;}

	public boolean isEmpty(){return optionalsEmpty() && expectedsEmpty();}
	public boolean expectedsEmpty(){return getExpecteds().isEmpty();}
	public boolean optionalsEmpty(){return getOptionals().isEmpty();}

	@Override
	public String toString() {
		return "{expecteds :"+expecteds+" - optionals:"+optionals+"}";
	}
}
