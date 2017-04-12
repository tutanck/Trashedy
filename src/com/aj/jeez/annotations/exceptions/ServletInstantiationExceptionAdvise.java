package com.aj.jeez.annotations.exceptions;

public class ServletInstantiationExceptionAdvise extends Exception {

	private static final long serialVersionUID = 1L;

	public ServletInstantiationExceptionAdvise() {}
	
	public ServletInstantiationExceptionAdvise(
			String msg
			) {
		super(msg);
	}

}
