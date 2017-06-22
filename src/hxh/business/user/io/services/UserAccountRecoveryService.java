package hxh.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.aj.jeez.tools.__;

import hxh.business.user.io.services.core.IOCore;
import hxh.conf.servletspolicy.OfflinePostServlet;
import hxh.tools.db.DBException;
import hxh.tools.general.PatternsHolder;
import hxh.tools.lingua.Lingua;
import hxh.tools.mailing.Email;
import hxh.tools.services.Response;
import hxh.tools.services.ServiceCodes;
import hxh.tools.services.ShouldNeverOccurException;
import hxh.tools.services.ToolBox;


/**
 * @author Joan */
public class UserAccountRecoveryService extends IOCore {
	public final static String url="/account/recover";

	/**
	 * Send an email with MD5 generated temporary access key for access recover to the user
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({	@Param(value="mail",rules={PatternsHolder.email}) }))
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
	
		if(!THINGS.exists(JR.slice(params,"mail"),collection))
			return Response.issue(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
		//Generate temporary key (sequence of 32 hexadecimal digits)  
		//reset password temporarily until user redefine it! 
		String secret = ToolBox.generateToken();
		THINGS.update(
				JR.slice(params,"mail"),
				JR.wrap("$set",JR.wrap("pass", secret))
				,collection);
	
		try {
			Email.send(params.getString("mail"),
					Lingua.get("NewAccessKeySentSubject","fr-FR"),
					Lingua.get("NewAccessKeySentMessage","fr-FR")+ secret);
		}catch (Exception e) {__.explode(e);}
		
		return Response.reply();
	}

}
