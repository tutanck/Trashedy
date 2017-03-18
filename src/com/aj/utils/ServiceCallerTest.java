package com.aj.utils;

public class ServiceCallerTest {
	
	public static StackTraceElement whoIsAsking(){
		StackTraceElement caller= Thread.currentThread().getStackTrace()[2];
		System.out.println("caller="+caller.getMethodName());
		return caller;
	}
	
	public static void whoIsAskingClassName1(){
		  whoIsAsking();
	}
	
	public static void whoIsAskingClassName2(){
		whoIsAskingClassName1();
	}
	
	public static void whoIsAskingClassName3(){
		whoIsAskingClassName2();
	}
	
	public static void main(String[] args) {
		whoIsAskingClassName3();
	}

}
