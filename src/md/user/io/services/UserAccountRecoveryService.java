package md.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.annotations.Param;
import com.aj.jeez.annotations.Params;
import com.aj.jeez.annotations.WebService;
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
import tools.services.ServiceCodes;
import tools.services.ToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;


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
