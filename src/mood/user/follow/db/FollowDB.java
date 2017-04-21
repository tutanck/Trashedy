package mood.user.follow.db;

import com.mongodb.DBCollection;

import tools.db.DBConnectionManager;


/**
 * @author AJoan */
public class FollowDB {

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("follow");	
}