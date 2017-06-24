package tproject.business.user.io.services.core;

import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCollection;

import tproject.business.user.io.db.SessionDB;
import tproject.business.user.io.db.UserDB;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class IOCore{

	public static DBCollection userdb = UserDB.collection;
	public static DBCollection sessiondb = SessionDB.collection;
	

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

		if(THINGS.exists(JR.slice(params,UserDB._email),userdb))
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);
		return null; //all right
	}

}
