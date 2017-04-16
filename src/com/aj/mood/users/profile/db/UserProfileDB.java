package com.aj.mood.users.profile.db;

import com.mongodb.DBCollection;
import tools.db.DBConnectionManager;

/**
 * @author AJoan */
public class UserProfileDB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("users");	
}