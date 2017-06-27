package tproject.business.talk.db;

import com.mongodb.DBCollection;

import tproject.tools.db.DBManager;


/**
 * @author AJoan */
public class TalkDB {

	public static DBCollection collection = DBManager.collection("talk");
	
	public final static String _form="from";
	public final static String _to="to";
	public final static String _msg="msg";
	public final static String _at="at";
	
}