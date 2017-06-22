package com.aj.hxh.business.admin;

import org.json.JSONObject;

import com.aj.hxh.conf.servletspolicy.OfflinePostServlet;
import com.aj.hxh.tools.mailing.Email;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ServiceCodes;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;

/**
 * @author AJoan */
public class SnowdenService {
	public final static String url="/snowden";

	@WebService(value=url,policy = OfflinePostServlet.class,
			requestParams=@Params({ @Param(value="msg") }))
	public static JSONObject alertme(
			JSONObject params
			) throws ShouldNeverOccurException{
	
		try {
			Email.send(Email.username,"Snowden wants to talk!",params.getString("msg"));
		}catch (Exception e) {return Response.issue(ServiceCodes.ALERT_NOT_SENT);}; 
		
		return Response.reply();
	}
}