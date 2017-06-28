package tproject.business.user.contact.services;

import java.util.Date;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.contact.core.ContactCore;
import tproject.business.user.io.db.UserDB;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan */
public class AddContactService extends ContactCore{

	public final static String url="/contact/add";


	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value=UserDB._cid)
					}))
	public static JSONObject add(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	

		JSONObject user = 
				JR.renameKeys(
						JR.slice(params,Common._userID)
						,Common._userID+"->"+"_id"
						);

		if(!THINGS.exists(user, userdb))
			return Response.issue(ServiceCodes.UNKNOWN_USER);

		THINGS.update(
				user
				,JR.wrap(
						"$addToSet"
						,JR.slice(params,UserDB._cid)
						.put(UserDB._contactDate,new Date())
						)
				,userdb);

		return Response.reply();
	}
}
