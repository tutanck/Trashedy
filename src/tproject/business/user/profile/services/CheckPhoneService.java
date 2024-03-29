package tproject.business.user.profile.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.db.UserDB;
import tproject.business.user.profile.core.ProfileCore;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OfflineGetServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class CheckPhoneService extends ProfileCore {
	public final static String url="/phone/owned";


	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value=UserDB._email,rules={PatternsHolder.email}) }))
	public static JSONObject isPhoneOwned(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
			
		return Response.reply(
				THINGS.exists(JR.slice(params,UserDB._phone)
					.put("_id",JR.wrap("$ne",params.get(Common._userID))),userdb)
				);	
		}
				
}
