package com.aj.hxh.business.user.state.db;

import com.aj.hxh.tools.db.DBManager;
import com.mongodb.DBCollection;

/**
 * @author AJoan */
public class StateDB {

	public static DBCollection collection = DBManager.collection("state");
}