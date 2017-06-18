package md.user.state.services;

import com.aj.jeez.annotations.Param;
import com.aj.jeez.annotations.Params;
import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import md.user.io.db.SessionDB;
import md.user.profile.services.core.ProfileCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

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
