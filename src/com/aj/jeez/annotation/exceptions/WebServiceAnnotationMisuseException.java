package com.aj.jeez.annotation.exceptions;

public class WebServiceAnnotationMisuseException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebServiceAnnotationMisuseException() {}
	
	public WebServiceAnnotationMisuseException(
			String msg
			) {
		super(msg);
	}

}
