package com.aj.hxh.business.user.io.services;

import org.json.JSONObject;

import com.aj.hxh.business.user.io.services.core.IOCore;
import com.aj.hxh.conf.servletspolicy.OnlinePostServlet;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.hxh.tools.services.ToolBox;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.regina.THINGS;

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
