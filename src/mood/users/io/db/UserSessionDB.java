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

	public static DBCollection collection = 
			DBConnectionManager.getMongoDBCollection("session");

	
	/**
	 * Returns the {uid}(user ID) associated to
	 * the given {raw-skey}(raw sessionKey) in params
	 * @param token
	 * @param did
	 * @return */
	public static String uid (
			String rawskey
			){
		return (String) THINGS.getOne(
				JSONRefiner.wrap("skey", ServicesToolBox.scramble(rawskey)),
				collection
				).get("skey");
	}

	
	/**
	 * Returns the {uid}(user ID) associated to
	 * the given {raw-skey}(raw sessionKey) in params 
	 * and wrap it in the 'same' JSONObject containing others/previous params  
	 * @param params
	 * @return */
	public static JSONObject clarifyParams(
			JSONObject params
			){
		return JSONRefiner.replace(params,"skey","uid",uid(params.getString("skey")));	
	}
	
	
	
	/**
	 * Check if a session exists for a given {skey}(sessionKey) in params
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static boolean sessionExists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JSONRefiner.wrap(
						"skey",ServicesToolBox.scramble(params.getString("skey"))),
				UserSessionDB.collection);
	}

}