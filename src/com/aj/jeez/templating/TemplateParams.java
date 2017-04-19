package com.aj.jeez.templating;

import java.util.HashSet;
import java.util.Set;

public class TemplateParams {

	private final Set<TemplateParam> expecteds;
	private final Set<TemplateParam> optionals;

	TemplateParams(){
		this.expecteds=new HashSet<>();
		this.optionals=new HashSet<>();
	}

	public void addExpected(TemplateParam expected){
		if(expected==null) return;
		if(expecteds.contains(expected))
			throw new IllegalArgumentException
			("Trying to add existing param name in expecteds");

		expecteds.add(expected);
	}

	public void addOptional(TemplateParam optional){
		if(optional==null) return;
		if(expecteds.contains(optional))
			throw new IllegalArgumentException
			("Trying to add existing param name in optional");
		optionals.add(optional);
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
