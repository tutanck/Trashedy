package com.aj.jeez.checks;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Checkout;
import com.aj.jeez.templating.TemplateParam;
import com.aj.jeez.templating.TemplateParams;

public class CheckExpectedOut {

	@Checkout
	public static boolean checkExpectedOut(
			JSONObject result,
			TemplateParams jsonOut
			){
		for(TemplateParam tp : jsonOut.getExpecteds())
			if(!result.has(tp.getName())) return false;
		return true;
	};	
}
