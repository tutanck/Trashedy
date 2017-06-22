package hxh.business.user.state.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import hxh.business.user.io.db.SessionDB;
import hxh.business.user.profile.services.core.ProfileCore;
import hxh.conf.servletspolicy.OnlinePostServlet;
import hxh.tools.db.DBException;
import hxh.tools.services.Response;
import hxh.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UpdateStateService extends ProfileCore{
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
 
		THINGS.update(JR.wrap("_id",decrypted.get("uid")),decrypted,true,collection);

		return Response.reply();
	}

}
