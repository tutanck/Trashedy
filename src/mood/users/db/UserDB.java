package mood.users.db;

import com.mongodb.DBCollection;
import tools.db.DBConnectionManager;
import tools.db.DBException;


/**
 * @author AJoan */
public class UserDB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("users");	

	public static String searchUser(
			String uid,String query
			) throws DBException{
		return "";
	}

}