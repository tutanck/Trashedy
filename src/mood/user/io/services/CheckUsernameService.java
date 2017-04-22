package mood.user.io.services;

import org.json.JSONObject;

import com.aj.tools.jr.AbsentKeyException;

import mood.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflineGetServlet;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

/**
 * @author Joan */
public class CheckUsernameService extends IOCore {
	public final static String url="/check/username";

	/**
	 * Check if username's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value="username",rules={PatternsHolder.uname}) }))
	public static JSONObject checkUsername(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		JSONObject usernameCheck = checkUsernameCore(params);
		if(usernameCheck!=null) 
			return usernameCheck;
		
		return Response.reply();
	}

}
