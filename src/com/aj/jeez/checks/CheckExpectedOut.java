package com.aj.jeez.checks;

import com.aj.jeez.annotations.Checkout;

public class CheckExpectedOut {

	@Checkout(clientsafe=true,name="CheckExpOut")
	public static boolean checkExpectedOut(
			Object result,
			String[]expectedOut,
			String[]optionalOut
			){
		int i=(int)result;
		return false;
	};	
}
