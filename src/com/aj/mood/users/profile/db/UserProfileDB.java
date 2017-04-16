package com.aj.mood.users.profile.db;

import com.aj.moodtools.db.DBConnectionManager;
import com.mongodb.DBCollection;

/**
 * @author AJoan */
public class UserProfileDB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("users");	
}