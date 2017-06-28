package tproject.business.user.contact.core;

import com.mongodb.DBCollection;

import tproject.business.user.io.db.UserDB;


/**
 * @author AJoan*/
public class ContactCore{
	
	public static DBCollection userdb = UserDB.collection;
}
