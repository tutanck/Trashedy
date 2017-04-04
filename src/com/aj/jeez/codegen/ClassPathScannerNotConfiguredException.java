package com.aj.jeez.codegen;

public class ClassPathScannerNotConfiguredException extends Exception {

	private static final long serialVersionUID = 1L;

	public ClassPathScannerNotConfiguredException() {}
	
	public ClassPathScannerNotConfiguredException(
			String msg
			) {
		super(msg);
	}

}
