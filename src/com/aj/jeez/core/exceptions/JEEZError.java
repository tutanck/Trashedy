package com.aj.jeez.core.exceptions;

public class JEEZError extends Error {

	private static final long serialVersionUID = 1L;

	public JEEZError() {}
	
	public JEEZError(
			String msg
			) {
		super(msg);
	}

}
