package com.aj.hxh.business.post.io.db;

import com.aj.hxh.tools.db.DBManager;
import com.mongodb.DBCollection;


/**
 * @author AJoan */
public class PostDB {

	public static DBCollection collection = DBManager.collection("posts");	
}