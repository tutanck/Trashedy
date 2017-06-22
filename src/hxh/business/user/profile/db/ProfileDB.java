package hxh.business.user.profile.db;

import com.mongodb.DBCollection;

import hxh.business.user.io.db.UserDB;

/**
 * @author AJoan */
public class ProfileDB {

	public static DBCollection collection = UserDB.collection;
}