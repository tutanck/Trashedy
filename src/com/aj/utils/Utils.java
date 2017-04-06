package com.aj.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class Utils {

	public static String join(
			Set<String> stringSet
			){
		String joined="";
		int i=0;
		System.out.println(stringSet.size());
		for(String s : stringSet)
			if(i++<stringSet.size()-1)
				joined+=s+",";
			else
				joined+=s;
		return joined;
		
	}
	
	public static String join(
			String... strings
			) {
		HashSet<String> stringSet = new HashSet<>();
		stringSet.addAll(Arrays.asList(strings));
		return join(stringSet);
	}

	public static String[] split(
			String string
			) {
		return string.split("\\,");
	}

	
	public static String joinClasses(Class<?>... testClasses) {
		Set<String> classes =new HashSet<>();
		for(Class<?> c:testClasses )
		classes.add(c.getCanonicalName());
			return join(classes);
	}
	

	public static void main(String[] args) {
		String joined =join("username","pass","email");
		System.out.println(joined);
		for(String s : split(joined))
			System.out.print(s+" ");
	}

	
}
