package mood.users.io.services.core;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBCollection;

import java.util.regex.Pattern;

import org.json.JSONException;
import org.json.JSONObject;

import mood.users.io.db.UserIODB;
import mood.users.io.db.UserSessionDB;
import tools.general.InputType;
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
			return JSONResponse.issue(ServiceCodes.INVALID_EMAIL_FORMAT);

		//--DB VALIDATION
		if(THINGS.exists(JR.slice(params,"email"),collection))
			return JSONResponse.issue(ServiceCodes.EMAIL_IS_TAKEN);

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
			return JSONResponse.issue(ServiceCodes.INVALID_USERNAME_FORMAT);
	
		//--DB VALIDATION
		if(THINGS.exists(JR.slice(params,"username"),collection))
			return JSONResponse.issue(ServiceCodes.USERNAME_IS_TAKEN);		
	
		return null; //all right
	}
	
	
	/**
	 * Determine the input format among the values of the InputType enumeration
	 * according to possible input formats for the UserIO service
	 * other formats are ignored 
	 * @param input
	 * @return */
	public static InputType determineFormat(String input){
		if(Pattern.compile(
				PatternsHolder.email
				).matcher(input).matches())
			return InputType.EMAIL;

		else if(Pattern.compile(
				PatternsHolder.nums
				).matcher(input).matches())
			return InputType.PHONE;

		return InputType.USERNAME; 
	}

}
