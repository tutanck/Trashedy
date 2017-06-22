package com.aj.hxh.business.user.follow.db;

import com.aj.hxh.tools.db.DBManager;
import com.mongodb.DBCollection;


/**
 * @author AJoan */
public class FollowDB {

	public static DBCollection collection = DBManager.collection("follows");	
}