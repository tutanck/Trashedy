package com.aj.jeez.annotations.exceptions;

public class ParameterNamingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParameterNamingException() {}
	
	public ParameterNamingException(
			String msg
			) {
		super(msg);
	}

}
