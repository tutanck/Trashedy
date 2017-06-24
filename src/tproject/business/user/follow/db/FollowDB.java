package tproject.business.user.follow.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;


/**
 * @author AJoan */
public class FollowDB {

	public static DBCollection collection = DBManager.collection("follows");	
}