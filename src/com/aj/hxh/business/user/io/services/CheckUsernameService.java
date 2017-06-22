package com.aj.hxh.business.user.io.services;

import org.json.JSONObject;

import com.aj.hxh.business.user.io.services.core.IOCore;
import com.aj.hxh.conf.servletspolicy.OfflineGetServlet;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.general.PatternsHolder;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.exceptions.AbsentKeyException;

/**
 * @author Joan */
public class CheckUsernameService extends IOCore {
	public final static String url="/check/uname";

	/**
	 * Check if username's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value="uname",rules={PatternsHolder.uname}) }))
	public static JSONObject checkUsername(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		
		JSONObject usernameCheck = IOCore.checkUsername(params);
		
		if(usernameCheck!=null) 
			return usernameCheck;
		
		return Response.reply();
	}

}
