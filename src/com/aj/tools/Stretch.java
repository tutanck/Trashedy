package com.aj.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * Elastic Str(ing) : Cut - stick Strings separated by a comma.
 * TODO make it generic : choose the separator character
 * @author Joan */
public class Stretch { 

	public static String join(
			Set<String> stringSet
			){
		String joined="";
		Iterator<String> it = stringSet.iterator();
		while(it.hasNext()){
			joined+=it.next();
			if(it.hasNext())joined+=",";
		}
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


	public static void addStringsToSet(
			Set<String>set,
			String paramListString
			){
		set.addAll(splitToSet(paramListString));
	}


	public static void addStringsToSet(
			Set<String>set,
			String paramListString,
			boolean reset
			){
		Set<String> paramSet=splitToSet(paramListString);
		if(reset)set.clear();
		addStringsToSet(paramSet,paramListString);
	}


	public static Set<Class<?>> splitToClassSet(
			String string
			) throws ClassNotFoundException {
		Set<Class<?>> classes = new HashSet<>();
		for(String classQN : splitToSet(string))
			classes.add(Class.forName(classQN));
		return classes;
	}


	public static void addClassesToSet(
			Set<Class<?>>set,
			String paramListString
			) throws ClassNotFoundException{
		set.addAll(splitToClassSet(paramListString));
	}


	public static void addClassesToSet(
			Set<Class<?>>set,
			String paramListString,
			boolean reset
			) throws ClassNotFoundException{
		if(reset)set.clear();
		addClassesToSet(set,paramListString);
	}

	
	public static String stretchClasses(
			Class<?>[]clazzs
			){
		String s="";
		Iterator<Class<?>> it = new HashSet<>(Arrays.asList(clazzs)).iterator();
	
		while(it.hasNext()){
			s+=it.next().getCanonicalName();
			if(it.hasNext())s+=",";
		}
		return s;
	}
	
	
	public static void main(String[] args) {
		System.out.println(splitToSet(join()).size()); //split an empty joined set of strings
		System.out.println(join("username","pass","email"));
		for(String s : split(join("username","pass","email")))
			System.out.println(s+" ");
		System.out.println(join(split(join("username","pass","email"))));
	}
}