package mood.users.io.services;

import java.util.Date;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.codegen.WebService;
import com.aj.regina.DBCommit;
import com.aj.regina.THINGS;
import com.aj.tools.AbsentKeyException;
import com.aj.tools.Caller;
import com.aj.tools.JSONRefiner;

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.lingua.Lingua;
import tools.mailing.SendEmail;
import tools.services.JSONResponse;
import tools.services.Safety;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

/**
 * @author Joan */
public class RegistrationService {

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
			webServlet = @WebServlet(name="RegistrationService",urlPatterns={"/signup"}),
			expectedIn={"username","pass","email"},
			policy = OfflinePostServlet.class)
	public static JSONObject registration(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {

		//--FORMAT VALIDATION (do all format validations bf remote calls like a db access) 
		if(!PatternsHolder.isValidPass(params.getString("pass")))
			return JSONResponse.issue(ServiceCodes.INVALID_PASS_FORMAT);

		JSONObject emailCheck = UserIOCore.checkEmailCore(params);
		if(emailCheck!=null) return emailCheck;

		JSONObject usernameCheck = UserIOCore.checkUsernameCore(params);
		if(usernameCheck!=null) return usernameCheck;

		//--DB WRITEACTION
		String ckey =ServicesToolBox.generateToken();
		DBCommit commit = THINGS.add(
				JSONRefiner.slice(params,new String[]{"username","pass","email"})
				.put("confirmed", ckey)
				.put("regdate", new Date()),
				UserIOCore.collection);

		//TODO utiliser un .property pour gerer le nom de racine de l app
		String basedir = "http://localhost:8080/Essais0";
		//TODO recuperer dans @weservlet de la servlet associée le bout d'url "/account/confirm"
		String dir= "/account/confirm";
		try {
			SendEmail.sendMail(
					params.getString("email"),
					Lingua.get("welcomeMailSubject","fr-FR"),
					Lingua.get("welcomeMailMessage","fr-FR")
					+basedir+dir+"?ckey="+ckey);
		}catch (Exception e) {
			commit.rollback(); //TODO a tester bcp
			Safety.explode(e);
		}

		return JSONResponse.reply(null,null,Caller.signature());
	}

}
