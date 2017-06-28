package tproject.business.user.need.core;

import com.mongodb.DBCollection;

import tproject.business.user.need.db.NeedDB;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class NeedCore{

	public static DBCollection needdb = NeedDB.collection;
}
