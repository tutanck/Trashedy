package com.aj.jeez.annotation.exceptions;

public class WebInitParameterSettingException extends Exception {

	private static final long serialVersionUID = 1L;

	public WebInitParameterSettingException() {}
	
	public WebInitParameterSettingException(
			String msg
			) {
		super(msg);
	}

}
