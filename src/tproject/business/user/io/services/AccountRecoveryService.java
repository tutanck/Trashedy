package tproject.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.aj.jeez.tools.__;

import tproject.business.user.io.core.IOCore;
import tproject.business.user.io.db.UserDB;
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
public class AccountRecoveryService extends IOCore {
	public final static String url="/account/recover";

	/**
	 * Send an email with MD5 generated temporary access key for access recover to the user
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({
				@Param(value=UserDB._email,rules={PatternsHolder.email}) }))
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
	
		JSONObject things = JR.slice(params,UserDB._email);
		
		if(!THINGS.exists(things,userdb))
			return Response.issue(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
		//Generate temporary key (sequence of 32 hexadecimal digits)  
		//reset password temporarily until user redefine it! 
		String himitsu = ToolBox.generateAlphaNumToken(64);
		THINGS.update(things,
				JR.wrap("$set",JR.wrap(UserDB._pass, himitsu))
				,userdb);
	
		try {
			Email.send(params.getString(UserDB._email),
					Lingua.get("NewAccessKeySentSubject","fr-FR"),
					Lingua.get("NewAccessKeySentMessage","fr-FR")+ himitsu);
		}catch (Exception e) {__.explode(e);}
		
		return Response.reply();
	}

}
