package md.user.group.db;

import com.mongodb.DBCollection;

import tools.db.DBManager;


/**
 * @author AJoan */
public class GroupDB {

	public static DBCollection collection = DBManager.collection("groups");	
}