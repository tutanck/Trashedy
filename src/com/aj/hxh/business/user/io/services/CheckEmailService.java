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
public class CheckEmailService extends IOCore {
	public final static String url="/check/email";
	
	/**
	 * Check if email's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value="email",rules={PatternsHolder.email}) }))
	public static JSONObject checkEmail(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		
		JSONObject emailCheck = IOCore.checkEmail(params);
		
		if(emailCheck!=null) 
			return emailCheck;
		
		return Response.reply();
	}

}
