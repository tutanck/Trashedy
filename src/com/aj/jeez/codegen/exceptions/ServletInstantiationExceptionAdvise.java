package com.aj.jeez.codegen.exceptions;

public class ServletInstantiationExceptionAdvise extends Exception {

	private static final long serialVersionUID = 1L;

	public ServletInstantiationExceptionAdvise() {}
	
	public ServletInstantiationExceptionAdvise(
			String msg
			) {
		super(msg);
	}

}
