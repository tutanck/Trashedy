package hxh.business.user.state.services.core;

import com.mongodb.DBCollection;

import hxh.business.user.state.db.StateDB;

/**
 * @author AJoan*/
public class StateCore{
	public static DBCollection collection = StateDB.collection;
}
