package com.aj.jeez.exceptions;

public class JEEZException extends Exception {

	private static final long serialVersionUID = 1L;

	public JEEZException() {}
	
	public JEEZException(
			String msg
			) {
		super(msg);
	}

}
