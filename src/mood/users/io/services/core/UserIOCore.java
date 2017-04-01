package mood.users.io.services.core;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.JSONRefiner;
import com.mongodb.DBCollection;

import org.json.JSONException;
import org.json.JSONObject;

import mood.users.io.db.UserIODB;
import mood.users.io.db.UserSessionDB;
import tools.general.PatternsHolder;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UserIOCore{

	public static DBCollection collection = UserIODB.collection;
	public static DBCollection session = UserSessionDB.collection;


	/**
	 * Check if email's input is valid (internal service's core)
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static JSONObject checkEmailCore(
			JSONObject params
			) throws JSONException, ShouldNeverOccurException, DBException, AbsentKeyException{

		//--FORMAT VALIDATION (do all format validations bf remote calls like a db access) 
		if(!PatternsHolder.isValidEmail(params.getString("email")))
			return JSONResponse.alert(ServiceCodes.INVALID_EMAIL_FORMAT);

		//--DB VALIDATION
		if(THINGS.exists(JSONRefiner.slice(params,new String[]{"email"}),collection))
			return JSONResponse.alert(ServiceCodes.EMAIL_IS_TAKEN);

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
	public static JSONObject checkUsernameCore(
			JSONObject params
			) throws JSONException, ShouldNeverOccurException, DBException, AbsentKeyException{
	
		//--FORMAT VALIDATION (do all format validations bf remote calls like a db access) 
		if(!PatternsHolder.isValidUsername(params.getString("username")))
			return JSONResponse.alert(ServiceCodes.INVALID_USERNAME_FORMAT);
	
		//--DB VALIDATION
		if(THINGS.exists(JSONRefiner.slice(params,new String[]{"username"}),collection))
			return JSONResponse.alert(ServiceCodes.USERNAME_IS_TAKEN);		
	
		return null; //all right
	}

}
