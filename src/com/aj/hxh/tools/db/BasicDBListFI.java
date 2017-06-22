package com.aj.hxh.tools.db;

import java.util.Collection;

import com.mongodb.BasicDBList;

/**
 * In software engineering, a fluent interface (as first coined by Eric Evans and Martin Fowler) is an implementation of an object oriented API that aims to provide more readable code.
 * A fluent interface is normally implemented by using method cascading (concretely method chaining) to relay the instruction context of a subsequent call (but a fluent interface entails more than just method chaining [1]). Generally, the context is
 * defined through the return value of a called method
 * self-referential, where the new context is equivalent to the last context
 * terminated through the return of a void context.
 * @author AJoan */
public class BasicDBListFI {

	/**
	 * Fluent interface for BasicDBList.addAll
	 * @param coll
	 * @return */
	public static BasicDBList addAll(Collection<? extends Object> coll){
		BasicDBList bdbl=new BasicDBList();
		bdbl.addAll(coll);
		return bdbl;
	}
	
	/**
	 * Fluent interface for BasicDBList.add
	 * @param obj
	 * @return */
	public static BasicDBList add(Object obj){
		BasicDBList al=new BasicDBList();
		al.add(obj);
		return al;
	}
 }