package tproject.business.user.status.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.db.SessionDB;
import tproject.business.user.profile.services.core.ProfileCore;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UpdateStatusService extends ProfileCore{
	public final static String url="/user/state/update";

	/**
	 * update user's state
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params({
					 		@Param(value="statu",type=boolean.class),
							@Param(value="pos") }))
	public static JSONObject updateState(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject decrypted = SessionDB.decrypt(params,"uid");
 
		THINGS.update(JR.wrap("_id",decrypted.get("uid")),decrypted,true,userdb);

		return Response.reply();
	}

}
