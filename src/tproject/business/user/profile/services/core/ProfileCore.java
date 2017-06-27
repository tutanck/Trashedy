package tproject.business.user.profile.services.core;

import com.mongodb.DBCollection;

import tproject.business.user.io.db.UserDB;

/**
 * @author AJoan*/
public class ProfileCore{
	public static DBCollection userdb = UserDB.collection;
	
}
