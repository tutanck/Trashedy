package md.user.io.services;

import java.util.Date;
import org.json.JSONObject;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.DBCommit;
import com.aj.regina.THINGS;
import com.aj.tools.__;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import md.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.lingua.Lingua;
import tools.mailing.Email;
import tools.services.Response;
import tools.services.ToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

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
				@Param(value="uname",rules={PatternsHolder.uname}),
				@Param(value="pass",rules={PatternsHolder.pass}),
				@Param(value="mail",rules={PatternsHolder.email}) }))
	public static JSONObject registration(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {

		JSONObject emailCheck = checkEmail(params);
		if(emailCheck!=null) return emailCheck;

		JSONObject usernameCheck = checkUsername(params);
		if(usernameCheck!=null) return usernameCheck;

		//--DB WRITE_ACTION
		String ckey =ToolBox.generateToken();
		DBCommit commit = THINGS.add(
				JR.slice(params,"uname","pass","mail")
				.put("verifd", ckey)
				.put("regdate", new Date())
				,collection);

		//TODO utiliser un .property pour gerer le nom de racine de l app
		String basedir = "http://localhost:8080/Essais0";

		try {
			Email.send(
					params.getString("email"),
					Lingua.get("welcomeMailSubject","fr-FR"),
					Lingua.get("welcomeMailMessage","fr-FR")
					+basedir+UserAccountConfirmationService.url+"?ckey="+ckey);
		}catch (Exception e) {commit.rollback(); /*TODO a tester bcp*/	__.explode(e);}
		
		return Response.reply();
	}

}
