package tproject.business.user.status.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;

/**
 * @author AJoan */
public class StateDB {

	public static DBCollection collection = DBManager.collection("status");

	public final static String _status ="status"; 
	public final static String _position ="pos"; 

}