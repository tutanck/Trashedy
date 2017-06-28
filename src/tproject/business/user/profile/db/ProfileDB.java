package tproject.business.user.profile.db;

import com.mongodb.DBCollection;

import tproject.business.user.io.db.UserDB;

/**
 * @author AJoan */
public class ProfileDB {

	public static DBCollection collection = UserDB.collection;
	
}