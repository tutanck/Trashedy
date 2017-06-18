package md.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.JR;

import md.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

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
