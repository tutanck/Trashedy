package com.aj.tools;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Underline __
 * @author Joan */
public class __ {

	public static boolean truly(Object obj){return true;}

	public static boolean falsy(Object object){return false;}

	public static boolean isEmpty(String string) {return string.length()==0;}
		
	public static boolean isEmpty(Object ...objects) {return objects.length==0;}
	
	public static boolean isNull(Object object) {return object==null;}

	public static boolean isWhiteSpace(String string) {return isEmpty(string.trim());}

	public static Set<String> toSet(String... strings) {return (!isNull(strings)&&!isEmpty((Object[])strings))?new HashSet<String>(Arrays.asList(strings)):Collections.emptySet();}

	public static Set<Object> toSet(Object... objects) {return (!isNull(objects)&&!isEmpty(objects))?new HashSet<Object>(Arrays.asList(objects)):Collections.emptySet();}

	public static List<String> toList(String... strings) {return (!isNull(strings)&&!isEmpty((Object[])strings))?(List<String>) Arrays.asList(strings):Collections.emptyList();}

	public static List<Object> toList(Object... objects) {return (!isNull(objects)&&!isEmpty(objects))?Arrays.asList(objects):Collections.emptyList();}

	public static Set<?> safe(Set<?> set) {return !isNull(set)?set:Collections.EMPTY_SET;}

	public static Set<String> stringSet(Set<String> set) {return !isNull(set)?set:Collections.emptySet();}

	public static List<?> safe(List<?> list) {return !isNull(list)?list:Collections.EMPTY_LIST;}

	public static Map<?,?> safe(Map<?,?> map) {return !isNull(map)?map:Collections.EMPTY_MAP;}

	public static Void outln(Object object){System.out.println(object);return null;}

	public static Void out(Object object){System.out.print(object);return null;}

	public static Void errln(Object object){System.err.println(object);return null;}

	public static Void err(Object object){System.err.print(object);return null;}

	public static boolean civilized(String input,String ...rules) {return civilized(input,toSet(rules));}

	public static boolean civilized(String input,Set<String>rules) {for(String rule : stringSet(rules))	if(!Pattern.compile(rule).matcher(input).matches()) return falsy(outln("__/civilized : This rude '"+input+"' doesn't respect the following rule '"+rule+"'")); return truly (outln("__/civilized : This civilized '"+input+"' respect all the rules '"+rules+"'"));}

	public static void explode(Throwable e) {e.printStackTrace();throw new RuntimeException(e);}


}
