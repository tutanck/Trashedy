package mood.user.io.db;

import com.mongodb.DBCollection;

import tools.db.DBManager;


/**
 * @author AJoan */
public class UserIODB {

	public static DBCollection collection = DBManager.collection("users");	
}