package mood.users.io.services;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotation.WebService;
import com.aj.tools.AbsentKeyException;
import com.aj.tools.Caller;

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflineGetServlet;

/**
 * @author Joan */
public class CheckEmailService {

	/**
	 * Check if email's input is valid 
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={"/check/email"}),
			expectedIn={"email"},
			policy=OfflineGetServlet.class)
	public static JSONObject checkEmail(
			JSONObject params
			) throws JSONException, ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject emailCheck = UserIOCore.checkEmailCore(params);
		if(emailCheck!=null) 
			return emailCheck;
		return JSONResponse.reply(null,null,Caller.signature());
	}

}
