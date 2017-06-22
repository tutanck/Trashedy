package hxh.business.post.io.db;

import com.mongodb.DBCollection;

import hxh.tools.db.DBManager;


/**
 * @author AJoan */
public class PostDB {

	public static DBCollection collection = DBManager.collection("posts");	
}