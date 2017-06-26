package tproject.business.user.io.services;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.DBCommit;
import com.aj.jeez.regina.THINGS;
import com.aj.jeez.tools.__;

import tproject.business.user.io.db.UserDB;
import tproject.business.user.io.services.core.IOCore;
import tproject.conf.servletspolicy.OfflinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.lingua.Lingua;
import tproject.tools.mailing.Email;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;
import tproject.tools.services.ToolBox;

/**
 * @author AJoan */
public class SignupService extends IOCore {
	
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
				@Param(value=UserDB._email,rules={PatternsHolder.email}),
				@Param(value=UserDB._pass,rules={PatternsHolder.pass}) }))
	public static JSONObject registration(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException,JSONException {
		
		if(CheckEmailService.isEmailTaken(params).getBoolean(Response.result)==true)
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);

		int vkey = ToolBox.generateIntToken(4);
		DBCommit commit = THINGS.add(
				JR.slice(params,UserDB._pass,UserDB._email)
				.put(UserDB._verified, vkey)
				.put(UserDB._registrationDate, new Date())
				,userdb);

		try {
			Email.send(
					params.getString(UserDB._email),
					//TODO REPLACE "welcomeMailSubject" by a public key in Lingua
					Lingua.get("welcomeMailSubject","fr-FR"),
					Lingua.get("welcomeMailMessage","fr-FR")+vkey);
		}catch (Exception e) {
			commit.rollback(); /*TODO a tester bcp*/
			__.explode(e);
		}

		return Response.reply();
	}

}
