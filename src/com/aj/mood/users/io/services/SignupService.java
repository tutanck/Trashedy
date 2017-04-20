package com.aj.mood.users.io.services;

import java.util.Date;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.DBCommit;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.__;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.general.PatternsHolder;
import com.aj.moodtools.lingua.Lingua;
import com.aj.moodtools.mailing.Email;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ServicesToolBox;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflinePostServlet;

/**
 * @author Joan */
public class SignupService {
	public final static String url="/signup";


	/** 
	 * Users registration service : register a new user
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(
			value=url,policy = OfflinePostServlet.class,
			requestParams=@Params({
				@Param(value="username",rules={PatternsHolder.username}),
				@Param(value="pass",rules={PatternsHolder.pass}),
				@Param(value="email",rules={PatternsHolder.email}) }))
	public static JSONObject registration(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {

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
					+basedir+UserAccountConfirmationService.url+"?ckey="+ckey);
		}catch (Exception e) {commit.rollback(); /*TODO a tester bcp*/	__.explode(e);}
		return Response.reply(null,null,Caller.signature());
	}

}
