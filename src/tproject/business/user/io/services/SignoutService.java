package tproject.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.core.IOCore;
import tproject.business.user.io.db.SessionDB;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class SignoutService extends IOCore{
	public final static String url="/signout";

	
	@WebService(value=url,policy=OnlinePostServlet.class)
	public static JSONObject logout(
			JSONObject params
			) throws DBException, ShouldNeverOccurException {
		
		THINGS.remove(
				JR.wrap(SessionDB._deviceID,
						params.getString(SessionDB._deviceID))
				,sessiondb);
	
		return Response.reply();
	}

}
