package hxh.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.regina.THINGS;

import hxh.business.user.io.services.core.IOCore;
import hxh.conf.servletspolicy.OnlinePostServlet;
import hxh.tools.db.DBException;
import hxh.tools.services.Response;
import hxh.tools.services.ShouldNeverOccurException;
import hxh.tools.services.ToolBox;

/**
 * @author Joan */
public class SignoutService extends IOCore{
	public final static String url="/signout";

	/**
	 * Users logout service : Disconnects user from online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException */
	
	@WebService(value=url,policy=OnlinePostServlet.class)
	public static JSONObject logout(
			JSONObject params
			) throws DBException, ShouldNeverOccurException {
		
		THINGS.remove(JR.wrap("skey",ToolBox.scramble(params.getString("skey"))),session);
	
		return Response.reply();
	}

}
