package mood.user.io.services;

import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.jr.JR;

import mood.user.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

import com.aj.jeez.annotation.annotations.WebService;

/**
 * @author Joan */
public class SignoutService {
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
		THINGS.remove(JR.wrap(
				"skey",ServicesToolBox.scramble(params.getString("skey")))
				,UserIOCore.session);
	
		return Response.reply(null);
	}

}
