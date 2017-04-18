package com.aj.mood.users.io.services;

import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.lingua.Lingua;
import com.aj.moodtools.mailing.Email;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.Safety;
import com.aj.moodtools.services.ServiceCodes;
import com.aj.moodtools.services.ServicesToolBox;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflinePostServlet;


/**
 * @author Joan */
public class UserAccountRecoveryService {
	public final static String url="/account/recover";

	/**
	 * @description send an email with MD5 generated temporary access key for access recover to the user
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({@Param("email")}))
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
	
		if(!THINGS.exists(JR.slice(params,"email"),UserIOCore.collection))
			return Response.issue(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
		//Generate temporary key (sequence of 32 hexadecimal digits)  
		//reset password temporarily until user redefine it! 
		String secret = ServicesToolBox.generateToken();
		THINGS.replaceOne(
				JR.slice(params,"email"),
				JR.wrap("pass", secret),
				UserIOCore.collection);
	
		try {
			Email.send(params.getString("email"),
					Lingua.get("NewAccessKeySentSubject","fr-FR"),
					Lingua.get("NewAccessKeySentMessage","fr-FR")+ secret);
		}catch (Exception e) {Safety.explode(e);}
		
		return Response.reply(null,null,Caller.signature());
	}

}
