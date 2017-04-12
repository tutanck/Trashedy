package mood.users.io.services;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.lingua.Lingua;
import tools.mailing.Email;
import tools.services.JSONResponse;
import tools.services.Safety;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;


/**
 * @author Joan */
public class AccountRecoveryService {
	public final static String url="/account/recover";
	

	/**
	 * @description send an email with MD5 generated temporary access key for access recover to the user
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={url}),
			expectedIn={"email"},
			policy=OfflinePostServlet.class)
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {
	
		if(!THINGS.exists(JR.slice(params,"email"),UserIOCore.collection))
			return JSONResponse.issue(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
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
		
		return JSONResponse.reply(null,null,Caller.signature());
	}

}
