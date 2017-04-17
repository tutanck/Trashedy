package com.aj.mood.users.io.services;

import org.json.JSONObject;

import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.RequestParams;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflineGetServlet;

/**
 * @author Joan */
public class CheckEmailService {
	public final static String url="/check/email";
	public final static String id="check_email";

	/**
	 * Check if email's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(id=id,urlPattern=url,policy=OfflineGetServlet.class,
			requestParams=@RequestParams({@Param("email")}))
	public static JSONObject checkEmail(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject emailCheck = UserIOCore.checkEmailCore(params);
		if(emailCheck!=null) 
			return emailCheck;
		return Response.reply(null,null,Caller.signature());
	}

}
