package mood.user.profile.db;

import com.mongodb.DBCollection;

import mood.user.io.db.UserDB;

/**
 * @author AJoan */
public class ProfileDB {

	public static DBCollection collection = UserDB.collection;
}