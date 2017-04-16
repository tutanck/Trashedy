package com.aj.mood.users.io.db;

import com.aj.moodtools.db.DBConnectionManager;
import com.mongodb.DBCollection;


/**
 * @author AJoan */
public class UserIODB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("users");	
}