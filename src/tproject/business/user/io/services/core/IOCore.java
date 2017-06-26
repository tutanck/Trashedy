package tproject.business.user.io.services.core;

import com.mongodb.DBCollection;

import tproject.business.user.io.db.SessionDB;
import tproject.business.user.io.db.UserDB;


/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class IOCore{

	public static DBCollection userdb = UserDB.collection;
	public static DBCollection sessiondb = SessionDB.collection;

}
