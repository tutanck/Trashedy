package com.aj.jeez.annotation.exceptions;

public class ParameterTypingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParameterTypingException() {}
	
	public ParameterTypingException(
			String msg
			) {
		super(msg);
	}

}
