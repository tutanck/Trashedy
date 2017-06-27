package tproject.business.user.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;

import tproject.business.user.io.db.SessionDB;
import tproject.business.user.io.db.UserDB;
import tproject.business.user.io.services.core.IOCore;
import tproject.conf.servletspolicy.OfflinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;
import tproject.tools.services.ToolBox;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

/**
 * @author AJoan */
public class SigninService extends IOCore {
	
	public final static String url="/signin";
	
	/*Out*/
	public final static String _sessionKey="skey";

	/**
	 * User login service : Sign user in online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException 
	 * @throws UnknownFormatException 
	 * @throws JSONException */
	@WebService(
			value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({
				@Param(value=UserDB._email,rules={PatternsHolder.email}),
				@Param(value=UserDB._pass,rules={PatternsHolder.pass})}))
	public static JSONObject login(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException, JSONException {

		DBObject foundUser;
		JSONObject things = JR.slice(params,UserDB._email,UserDB._pass);

		if (!THINGS.exists(things,userdb))
			return Response.issue(ServiceCodes.WRONG_LOGIN_PASSWORD);

		foundUser = THINGS.getOne(things,userdb);

		if(!THINGS.exists(
				JR.wrap("_id",foundUser.get("_id")).put(UserDB._verified, true)
				,userdb))
			return Response.issue(ServiceCodes.USER_NOT_CONFIRMED);

		//2 different devices can't be connected at the same time
		THINGS.remove(JR.wrap(SessionDB._userID, foundUser.get("_id")),sessiondb);

		String sessionKey = ToolBox.generateAlphaNumToken(32);

		THINGS.add(
				JR.wrap(SessionDB._sessionKey,
						ToolBox.scramble(sessionKey+params.getString(SessionDB._deviceID)))
				.put(SessionDB._userID,foundUser.get("_id"))
				.put(SessionDB._deviceID,params.getString(SessionDB._deviceID))
				,sessiondb);

		return Response.reply(JR.wrap(_sessionKey, sessionKey));
	}

}
