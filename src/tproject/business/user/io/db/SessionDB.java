package tproject.business.user.io.db;

import org.json.JSONObject;

import com.mongodb.DBCollection;

import tproject.tools.db.DBException;
import tproject.tools.db.DBManager;
import tproject.tools.services.ToolBox;

import com.aj.jeez.jr.JR;
import com.aj.jeez.regina.THINGS;

/**
 * @author AJoan */
public class SessionDB {

	public static DBCollection collection = DBManager.collection("sessions");

	public final static String _userID="uid";
	public final static String _deviceID="did";
	public final static String _sessionKey="skey";


	/**
	 * Returns the userID associated to
	 * the given sessionKey in params 
	 * and wrap it in the 'same' JSONObject containing others/previous params  
	 * @param params
	 * @return 
	 * @throws DBException 
	 * @throws  */
	public static JSONObject decrypt(
			JSONObject params,
			String decryptedKeyName
			) throws DBException{
		return JR.replace(params,_sessionKey,decryptedKeyName,
				THINGS.getOne(
						JR.wrap(_sessionKey,ToolBox.scramble(params.getString(_sessionKey))),
						collection
						).get(_userID));
	}

}