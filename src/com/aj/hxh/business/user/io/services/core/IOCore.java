package com.aj.hxh.business.user.io.services.core;

import com.aj.hxh.business.user.io.db.SessionDB;
import com.aj.hxh.business.user.io.db.UserDB;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.general.InputType;
import com.aj.hxh.tools.general.PatternsHolder;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ServiceCodes;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCollection;

import java.util.regex.Pattern;

import org.json.JSONObject;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class IOCore{

	public static DBCollection collection = UserDB.collection;
	public static DBCollection session = SessionDB.collection;


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

		if(THINGS.exists(JR.slice(params,"email"),collection))
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);
		return null; //all right
	}


	/**
	 * Check if username's input is valid (internal service's core)
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static JSONObject checkUsername(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{

		if(THINGS.exists(JR.slice(params,"uname"),collection))
			return Response.issue(ServiceCodes.USERNAME_IS_TAKEN);		
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

		if(Pattern.compile(
				PatternsHolder.nums
				).matcher(input).matches())
			return InputType.PHONE;

		return InputType.USERNAME; 
	}

}
