package com.aj.jeez.templating;

import java.util.HashSet;
import java.util.Set;

public class Params {

	private final Set<Param> expecteds;
	private final Set<Param> optionals;

	Params(){
		this.expecteds=new HashSet<>();
		this.optionals=new HashSet<>();
	}

	public void addExpected(Param expected){
		if(expected==null) return;
		if(expecteds.contains(expected))
			throw new IllegalArgumentException
			("Trying to add existing param name in expecteds");

		expecteds.add(expected);
	}

	public void addOptional(Param optional){
		if(optional==null) return;
		if(expecteds.contains(optional))
			throw new IllegalArgumentException
			("Trying to add existing param name in optional");
		optionals.add(optional);
	}

	public Set<Param> getOptionals() {return optionals;}
	public Set<Param> getExpecteds() {return expecteds;}
	
	@Override
	public String toString() {
		return "expecteds :"+expecteds+" - optionals:"+optionals;
	}
}
