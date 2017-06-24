package tproject.conf.servletspolicy;

import org.json.JSONObject;

import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.db.SessionDB;
import tproject.tools.db.DBException;
import tproject.tools.services.ToolBox;

public class Common {

	/**
	 * Check if a session exists for a given sessionKey in params
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static boolean exists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JR.wrap(SessionDB._sessionKey,
						ToolBox.scramble
						(params.getString(SessionDB._deviceID)+params.getString(SessionDB._sessionKey)))
				,SessionDB.collection);
	}
	
	/**
	 * Check if a device exists in session
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static boolean deviceExists(
			JSONObject params
			) throws DBException, AbsentKeyException{
		return THINGS.exists(
				JR.wrap(SessionDB._deviceID,params.getString(SessionDB._deviceID))
				,SessionDB.collection);
	}
	
}
