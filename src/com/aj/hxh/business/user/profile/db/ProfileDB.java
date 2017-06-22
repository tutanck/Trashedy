package com.aj.hxh.business.user.profile.db;

import com.aj.hxh.business.user.io.db.UserDB;
import com.mongodb.DBCollection;

/**
 * @author AJoan */
public class ProfileDB {

	public static DBCollection collection = UserDB.collection;
}