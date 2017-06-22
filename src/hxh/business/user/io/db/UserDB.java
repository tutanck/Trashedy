package hxh.business.user.io.db;

import com.mongodb.DBCollection;

import hxh.tools.db.DBManager;


/**
 * @author AJoan */
public class UserDB {

	public static DBCollection collection = DBManager.collection("users");	
}