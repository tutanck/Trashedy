package com.aj.mood.users.io.services;

import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.jr.JR;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ServicesToolBox;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OnlinePostServlet;

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
