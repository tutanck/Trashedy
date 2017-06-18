package business.user.io.db;

import com.mongodb.DBCollection;

import tools.db.DBManager;


/**
 * @author AJoan */
public class UserDB {

	public static DBCollection collection = DBManager.collection("users");	
}