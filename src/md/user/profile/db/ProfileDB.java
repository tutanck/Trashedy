package md.user.profile.db;

import com.mongodb.DBCollection;

import md.user.io.db.UserDB;

/**
 * @author AJoan */
public class ProfileDB {

	public static DBCollection collection = UserDB.collection;
}