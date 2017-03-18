package mood.users.io.db;

import org.json.JSONObject;

import com.mongodb.DBCollection;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.regina.THINGS;
import tools.db.DBConnectionManager;
import tools.db.DBException;
import tools.services.ServicesToolBox;

/**
 * @author AJoan */
public class UserSessionDB {

	public static DBCollection collection = DBConnectionManager.getMongoDBCollection("session");

	public static String uid (
			String token,
			String did //deviceID
			){
		return (String) THINGS.getOne(
				new JSONObject()
				.put("skey", ServicesToolBox.scramble(token+did)),
				collection
				).get("skey");
	}

	public static boolean sessionExists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JSONRefiner.slice(params,new String[]{"skey"}), UserSessionDB.collection);
	}

}