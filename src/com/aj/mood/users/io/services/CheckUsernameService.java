package com.aj.mood.users.io.services;

import org.json.JSONObject;

import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.general.PatternsHolder;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflineGetServlet;

/**
 * @author Joan */
public class CheckUsernameService {
	public final static String url="/check/username";

	/**
	 * Check if username's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value="username",rules={PatternsHolder.username}) }))
	public static JSONObject checkUsername(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject usernameCheck = UserIOCore.checkUsernameCore(params);
		if(usernameCheck!=null) 
			return usernameCheck;
		return Response.reply(null,null,Caller.signature());
	}

}
