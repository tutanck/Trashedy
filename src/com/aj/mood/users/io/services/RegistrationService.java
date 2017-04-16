package com.aj.mood.users.io.services;

import java.util.Date;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.jeez.checks.CheckExpectedOut;
import com.aj.regina.DBCommit;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.general.PatternsHolder;
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
public class RegistrationService {
	public final static String url="/signup";

	/**
	 * @description 
	 * Users registration service : register a new user
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={url}),
			expectedIn={"username","pass","email"},
			checkClasses={CheckExpectedOut.class},
			policy = OfflinePostServlet.class)
	public static JSONObject registration(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {

		//--FORMAT VALIDATION (do all format validations bf remote calls like a db access) 
		if(!PatternsHolder.isValidPass(params.getString("pass")))
			return Response.issue(ServiceCodes.INVALID_PASS_FORMAT);

		JSONObject emailCheck = UserIOCore.checkEmailCore(params);
		if(emailCheck!=null) return emailCheck;

		JSONObject usernameCheck = UserIOCore.checkUsernameCore(params);
		if(usernameCheck!=null) return usernameCheck;

		//--DB WRITEACTION
		String ckey =ServicesToolBox.generateToken();
		DBCommit commit = THINGS.add(
				JR.slice(params,"username","pass","email")
				.put("confirmed", ckey)
				.put("regdate", new Date()),
				UserIOCore.collection);

		//TODO utiliser un .property pour gerer le nom de racine de l app
		String basedir = "http://localhost:8080/Essais0";
	
		try {
			Email.send(
					params.getString("email"),
					Lingua.get("welcomeMailSubject","fr-FR"),
					Lingua.get("welcomeMailMessage","fr-FR")
					+basedir+AccountRecoveryService.url+"?ckey="+ckey);
		}catch (Exception e) {
			commit.rollback(); //TODO a tester bcp
			Safety.explode(e);
		}

		return Response.reply(null,null,Caller.signature());
	}

}
