package com.aj.jeez.annotation.exceptions;

public class ParamNamingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParamNamingException() {}
	
	public ParamNamingException(
			String msg
			) {
		super(msg);
	}

}
