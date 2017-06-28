package tproject.business.user.need.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;


/**
 * @author AJoan */
public class NeedDB {

	public static DBCollection collection = DBManager.collection("need");

	public final static String _query="query";
	public final static String _description="desc";
	public final static String _type="type";
	public final static String _unqualified="unqlf";
	public final static String _beginDate="start";
	public final static String _endDate="end";
	public final static String _place="place";
	public final static String _pay="pay";
	public final static String _activityDuration="dur";
	public final static String _title = "title";
	public final static String _owner="owner";
	
}