package business.user.io.db;

import org.json.JSONObject;

import com.mongodb.DBCollection;

import tools.db.DBManager;
import tools.db.DBException;
import tools.services.ToolBox;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

/**
 * @author AJoan */
public class SessionDB {

	public static DBCollection collection = DBManager.collection("sessions");

	
	/**
	 * Returns the {uid}(user ID) associated to
	 * the given {raw-skey}(raw sessionKey) in params 
	 * and wrap it in the 'same' JSONObject containing others/previous params  
	 * @param params
	 * @return 
	 * @throws DBException 
	 * @throws  */
	public static JSONObject decrypt(
			JSONObject params,
			String decryptedKeyName
			) throws DBException{
		return JR.replace(params,"skey",decryptedKeyName,
				 THINGS.getOne(
							JR.wrap("skey",ToolBox.scramble(params.getString("skey"))),
							collection
							).get("uid"));
	}
	
	
	
	/**
	 * Check if a session exists for a given {skey}(sessionKey) in params
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static boolean exists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JR.wrap("skey",ToolBox.scramble(params.getString("skey"))),collection);
	}

}