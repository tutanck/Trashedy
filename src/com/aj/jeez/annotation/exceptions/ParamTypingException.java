package com.aj.jeez.annotation.exceptions;

public class ParamTypingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParamTypingException() {}
	
	public ParamTypingException(
			String msg
			) {
		super(msg);
	}

}
