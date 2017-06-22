package com.aj.hxh.business.user.io.db;

import com.aj.hxh.tools.db.DBManager;
import com.mongodb.DBCollection;


/**
 * @author AJoan */
public class UserDB {

	public static DBCollection collection = DBManager.collection("users");	
}