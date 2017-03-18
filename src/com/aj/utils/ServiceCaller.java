package com.aj.utils;

public class ServiceCaller {

	public static String whoIsAskingPath(){
		String callerClass= Thread.currentThread().getStackTrace()[5].getClassName();
		System.out.println("whoIsAskingPath : "+callerClass);
		return callerClass;
	}

	public static String whoIsAskingClass(){
		String[] tmp= whoIsAskingPath().replace(".", " ").split(" ");
		System.out.println("whoIsAskingClass : "+tmp[tmp.length-1]);
		return tmp[tmp.length-1];
	}

	public static String whichServletIsAsking(){
		String res="";
		String tmp = whoIsAskingClass();
		if(tmp.contains("Servlet"))
			res=tmp.split("Servlet")[0];
		System.out.println("whichServletIsAsking : "+res);
		return res;
	}

	public static void main(String[] args) {
		System.out.println(whoIsAskingPath());
		System.out.println(whoIsAskingClass());
		System.out.println(whichServletIsAsking());
		//System.out.println("StringServlet".split("Servlet")[0]);
	}

}
