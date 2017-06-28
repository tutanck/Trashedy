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
	/*Grades*/
	public final static String _grades = "grades";
	public final static String _grade = "grade";
	public final static String _gradeDate = "gdate";
	public final static String _nid = "nid";//need id 
	//TODO maybe mid (mission id) as a new instance of a need 
	/*Contacts*/
	public static final String _contacts = "contacts";
	public static final String _cid = "cid";
	public static final String _contactDate = "cdate";
	
	
}