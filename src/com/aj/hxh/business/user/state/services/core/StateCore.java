package com.aj.hxh.business.user.state.services.core;

import com.aj.hxh.business.user.state.db.StateDB;
import com.mongodb.DBCollection;

/**
 * @author AJoan*/
public class StateCore{
	public static DBCollection collection = StateDB.collection;
}
