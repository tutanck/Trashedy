package com.aj.jeez.annotation.exceptions;

public class ParamRulingException extends Exception {

	private static final long serialVersionUID = 1L;

	public ParamRulingException() {}
	
	public ParamRulingException(
			String msg
			) {
		super(msg);
	}

}