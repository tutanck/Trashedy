package tproject.business.user.status.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.status.core.StateCore;
import tproject.business.user.status.db.StateDB;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan */
public class UpdateStatusService extends StateCore{
	public final static String url="/user/state/update";


	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params({
				@Param(value=StateDB._status,rules={"(READY|AVAILABLE|BUSY|OFFLINE)"}),
				@Param(value=StateDB._position, type=int.class) }))
	public static JSONObject updateState(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {

		THINGS.update(JR.wrap("_id",params.get(Common._userID))
				,params
				,true,statedb);

		return Response.reply();
	}

}
