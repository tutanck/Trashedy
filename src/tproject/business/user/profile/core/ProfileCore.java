package tproject.business.user.profile.core;

import com.mongodb.DBCollection;

import tproject.business.user.profile.db.ProfileDB;

/**
 * @author AJoan*/
public class ProfileCore{
	public static DBCollection userdb = ProfileDB.collection;
	
}
