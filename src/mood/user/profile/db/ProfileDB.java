package mood.user.profile.db;

import com.mongodb.DBCollection;

import mood.user.io.db.UserIODB;

/**
 * @author AJoan */
public class ProfileDB {

	public static DBCollection collection = UserIODB.collection;
}