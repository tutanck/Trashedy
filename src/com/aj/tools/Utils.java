package com.aj.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {

	public static String join(
			Set<String> stringSet
			){
		String joined="";
		int i=0;
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
		Set<String> stringSet = new HashSet<>();
		stringSet.addAll(Arrays.asList(strings));
		return join(stringSet);
	}

	public static String joinClasses(
			Class<?>... classes
			) {
		Set<String> classNames = new HashSet<>();
		for(Class<?> c : classes)
			classNames.add(c.getCanonicalName());
		return join(classNames);
	}

	public static String[] split(
			String string
			) {
		if(string.equals(""))
			return new String[0];
		return string.split("\\,");
	}

	public static Set<String> splitToSet(
			String string
			) {
		String []strTab=split(string);
		return strTab.length==0?
				Collections.emptySet():new HashSet<String>(Arrays.asList(strTab));
	}

	public static List<String> splitToList(
			String string
			) {
		String []strTab=split(string);
		return strTab.length==0?
				Collections.emptyList():Arrays.asList(strTab);
	}

	public static Set<Class<?>> splitToClassSet(
			String string
			) throws ClassNotFoundException {
		Set<Class<?>> classes = new HashSet<>();
		for(String classQN : splitToSet(string))
			classes.add(Class.forName(classQN));
		return classes;
	}

	public static void reSet(
			Set<String>set,
			String paramListString
			){
		Set<String> paramSet=splitToSet(paramListString);
		set.clear();
		set.addAll(paramSet);
	}

	public static void addClassesToSet(
			Set<Class<?>>set,
			String paramListString
			) throws ClassNotFoundException{
		set.addAll(splitToClassSet(paramListString));
	}




	public static void main(String[] args) {
		System.out.println(splitToSet(join()).size()); //split an empty joined set of strings
		System.out.println(join("username","pass","email"));
		for(String s : split(join("username","pass","email")))
			System.out.println(s+" ");
		System.out.println(join(split(join("username","pass","email"))));
	}


}
