package com.aj.jeez.templating;

import org.json.JSONObject;

import com.aj.jeez.exceptions.JEEZError;
import com.aj.tools.__;

public class EffectiveParamTyper{

	static boolean valid(
			String name,
			int formalType,
			String effectiveStringValue,
			JSONObject validParams
			){
		try {
			switch (formalType) {
			case 0:
				return __.truly(validParams.put(name,effectiveStringValue));
			case 1:
				return __.truly(validParams.put(name,Integer.parseInt(effectiveStringValue)));
			case 2:
				return __.truly(validParams.put(name,Long.parseLong(effectiveStringValue)));
			case 3:
				return __.truly(validParams.put(name,Float.parseFloat(effectiveStringValue))); 
			case 4:
				return __.truly(validParams.put(name,Double.parseDouble(effectiveStringValue)));
			case 5:
				return __.truly(validParams.put(name,parseBoolean(effectiveStringValue)));
			default: throw new JEEZError("#SNO : internal typing error");					
			}
		} catch (IllegalArgumentException iae) {
			iae.printStackTrace();
			return false;
		}
	}


	private static boolean parseBoolean(String boolStr){
		if(!boolStr.equals("true") || !boolStr.equals("false"))
			throw new IllegalArgumentException(boolStr+" is not a boolean");
		return Boolean.parseBoolean(boolStr);
	}	
}