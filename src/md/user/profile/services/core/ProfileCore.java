package md.user.profile.services.core;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBCollection;

import md.user.io.db.SessionDB;
import md.user.io.db.UserDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;

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
