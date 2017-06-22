package com.aj.hxh.business.user.profile.services.core;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.hxh.business.user.io.db.SessionDB;
import com.aj.hxh.business.user.io.db.UserDB;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ServiceCodes;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCollection;

/**
 * @author AJoan*/
public class ProfileCore{
	public static DBCollection collection = UserDB.collection;
	
	
	/**
	 * Check if email's input is valid (internal service's core)
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static JSONObject checkEmail(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject decrypted = SessionDB.decrypt(params,"uid");
		
		if(THINGS.exists(JR.slice(decrypted,"email")
				.put("_id",JR.wrap("$ne",decrypted.get("uid"))),collection))
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);

		return null; //all right
	}


	/**
	 * Check if username's input is valid (internal service's core)
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static JSONObject checkUsername(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject decrypted = SessionDB.decrypt(params,"uid");
		
		if(THINGS.exists(JR.slice(decrypted,"uname")
				.put("_id",JR.wrap("$ne",decrypted.get("uid"))),collection))
			return Response.issue(ServiceCodes.USERNAME_IS_TAKEN);
		
		return null; //all right
	}
	
	
	/**
	 * Check if username's input is valid (internal service's core)
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static JSONObject checkPhone(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject decrypted = SessionDB.decrypt(params,"uid");
		
		if(THINGS.exists(JR.slice(decrypted,"phone")
				.put("_id",JR.wrap("$ne",decrypted.get("uid"))),collection))
			return Response.issue(ServiceCodes.PHONE_IS_TAKEN);	
		
		return null; //all right
	}
	
}
