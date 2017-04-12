package mood.users.io.services;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflineGetServlet;

/**
 * @author Joan */
public class CheckUsernameService {
	public final static String url="/check/username";

	/**
	 * Check if username's input is valid 
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={url}),
			expectedIn={"username"},
			policy=OfflineGetServlet.class)
	public static JSONObject checkUsername(
			JSONObject params
			) throws JSONException, ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject usernameCheck = UserIOCore.checkUsernameCore(params);
		if(usernameCheck!=null) 
			return usernameCheck;
		return JSONResponse.reply(null,null,Caller.signature());
	}

}
