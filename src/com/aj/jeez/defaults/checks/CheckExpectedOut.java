package com.aj.jeez.defaults.checks;

import org.json.JSONObject;

import com.aj.jeez.annotations.Checkout;
import com.aj.jeez.templating.TemplateParam;
import com.aj.jeez.templating.TemplateParams;

public class CheckExpectedOut {

	@Checkout
	public static boolean checkExpectedOut(
			Object result,
			TemplateParams jsonOut
			){
		if(! (result instanceof JSONObject)) 
			return false;
		
		for(TemplateParam tp : jsonOut.getExpecteds())
			if(! ((JSONObject) result).has(tp.getName()) ) 
				return false;
		
		return true;
	}	
}
