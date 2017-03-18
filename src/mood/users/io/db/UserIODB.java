package mood.users.io.db;

import com.mongodb.DBCollection;
import tools.db.DBConnectionManager;


/**
 * @author AJoan */
public class UserIODB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("users");	
}