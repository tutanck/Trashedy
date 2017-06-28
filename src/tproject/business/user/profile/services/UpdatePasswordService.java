package tproject.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.db.UserDB;
import tproject.business.user.profile.core.ProfileCore;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan */
public class UpdatePasswordService extends ProfileCore{
	public final static String url="/user/profile/update";

	/**
	 * update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value=UserDB._pass,rules={PatternsHolder.pass})
					}))
	public static JSONObject updatePassword(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {

		THINGS.update(JR.slice(params,Common._userID),
				JR.wrap("$set",JR.slice(params,UserDB._pass))
				,userdb);

		return Response.reply();
	}
}
