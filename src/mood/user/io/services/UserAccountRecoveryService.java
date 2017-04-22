package mood.user.io.services;

import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.__;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import mood.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.lingua.Lingua;
import tools.mailing.Email;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;


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
			requestParams=@Params({	@Param(value="email",rules={PatternsHolder.email}) }))
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
	
		if(!THINGS.exists(JR.slice(params,"email"),collection))
			return Response.issue(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
		//Generate temporary key (sequence of 32 hexadecimal digits)  
		//reset password temporarily until user redefine it! 
		String secret = ServicesToolBox.generateToken();
		THINGS.update(
				JR.slice(params,"email"),
				JR.wrap("$set",JR.wrap("pass", secret))
				,collection);
	
		try {
			Email.send(params.getString("email"),
					Lingua.get("NewAccessKeySentSubject","fr-FR"),
					Lingua.get("NewAccessKeySentMessage","fr-FR")+ secret);
		}catch (Exception e) {__.explode(e);}
		
		return Response.reply();
	}

}
