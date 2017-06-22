package com.aj.hxh.business.user.io.db;

import org.json.JSONObject;

import com.mongodb.DBCollection;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.db.DBManager;
import com.aj.hxh.tools.services.ToolBox;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

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