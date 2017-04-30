package md.user.group.services.core;

import com.mongodb.DBCollection;

import md.user.group.db.GroupDB;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class GroupCore{

	public static DBCollection collection = GroupDB.collection;
}
