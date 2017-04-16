package com.aj.mood.users.io.services;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.JR;
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
	public final static String servletName="signout";

	/**
	 * @description  Users logout service : Disconnects user from online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException */
	
	@WebService(
			webServlet = @WebServlet(name=servletName, urlPatterns={url}),
			policy=OnlinePostServlet.class)
	public static JSONObject logout(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException {
		THINGS.remove(JR.wrap(
				"skey",ServicesToolBox.scramble(params.getString("skey")))
				,UserIOCore.session);
		
		return Response.reply(null,null,Caller.signature());
	}

}
