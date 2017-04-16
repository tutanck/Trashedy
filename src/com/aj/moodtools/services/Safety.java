package com.aj.moodtools.services;

public class Safety {
	
	public static void explode(Throwable e) {
		e.printStackTrace();
		throw new RuntimeException(e);
	}
}
