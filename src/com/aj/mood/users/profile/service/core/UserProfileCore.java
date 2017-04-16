package com.aj.mood.users.profile.service.core;

import com.mongodb.DBCollection;

import com.aj.mood.users.io.db.UserIODB;

/**
 * @author AJoan*/
public class UserProfileCore{
	public static DBCollection collection = UserIODB.collection;
}
