package tproject.business.user.io.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;


/**
 * @author AJoan */
public class UserDB {

	public static DBCollection collection = DBManager.collection("users");	
	
	/** Collection's attributes */
	/*User*/
	public final static String _email="mail";
	public final static String _pass="pass";
	public final static String _verified="verifd";
	public final static String _registrationDate="regdate";
	/*Profile*/
	public final static String _firstName="fname";
	public final static String _lastName="lname";
	public final static String _societyName="sname";
	public final static String _type="type";
	public final static String _birthDate="bdate";
	public final static String _phone="phone";
	public final static String _skills="skills";
	public final static String _description="desc";
	public final static String _unqualilified="unqlf";
	public static final String _grade = "grade";
	
}