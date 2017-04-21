package mood.user.profile.service.core;

import com.mongodb.DBCollection;

import mood.user.io.db.UserIODB;

/**
 * @author AJoan*/
public class UserProfileCore{
	public static DBCollection collection = UserIODB.collection;
}
