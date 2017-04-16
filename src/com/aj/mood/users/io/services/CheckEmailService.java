package com.aj.mood.users.io.services;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflineGetServlet;

/**
 * @author Joan */
public class CheckEmailService {
	public final static String url="/check/email";
	public final static String servletName="check_email";

	/**
	 * Check if email's input is valid 
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(name = servletName,urlPatterns={url}),
			expectedIn={"email"},
			policy=OfflineGetServlet.class)
	public static JSONObject checkEmail(
			JSONObject params
			) throws JSONException, ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject emailCheck = UserIOCore.checkEmailCore(params);
		if(emailCheck!=null) 
			return emailCheck;
		return Response.reply(null,null,Caller.signature());
	}

}
