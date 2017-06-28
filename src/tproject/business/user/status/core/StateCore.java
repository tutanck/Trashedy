package tproject.business.user.status.core;

import com.mongodb.DBCollection;

import tproject.business.user.status.db.StateDB;

/**
 * @author AJoan*/
public class StateCore{
	public static DBCollection statedb = StateDB.collection;
}
