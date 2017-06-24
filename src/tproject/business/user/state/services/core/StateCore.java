package tproject.business.user.state.services.core;

import com.mongodb.DBCollection;

import tproject.business.user.state.db.StateDB;

/**
 * @author AJoan*/
public class StateCore{
	public static DBCollection collection = StateDB.collection;
}
