package business.user.state.db;

import com.mongodb.DBCollection;

import tools.db.DBManager;

/**
 * @author AJoan */
public class StateDB {

	public static DBCollection collection = DBManager.collection("state");
}