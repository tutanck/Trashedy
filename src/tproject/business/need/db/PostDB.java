package tproject.business.need.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;


/**
 * @author AJoan */
public class PostDB {

	public static DBCollection collection = DBManager.collection("posts");	
}