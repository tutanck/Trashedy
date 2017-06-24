package tproject.business.user.io.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;


/**
 * @author AJoan */
public class UserDB {

	public static DBCollection collection = DBManager.collection("users");	
	
	/** Collection's attributes */
	public final static String _pass="pass";
	public final static String _email="mail";
	public final static String _verified="verifd";
	public final static String _registrationDate="regdate";

	
}