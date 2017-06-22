package hxh.business.user.state.db;

import com.mongodb.DBCollection;

import hxh.tools.db.DBManager;

/**
 * @author AJoan */
public class StateDB {

	public static DBCollection collection = DBManager.collection("state");
}