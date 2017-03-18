package mood.users.profile.db;

import com.mongodb.DBCollection;
import tools.db.DBConnectionManager;
import tools.db.DBException;

/**
 * @author AJoan */
public class UserProfileDB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("users");	

	public static String searchUser(
			String uid,String query
			) throws DBException{
		return "";
	}
}