package md.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.annotations.Param;
import com.aj.jeez.annotations.Params;
import com.aj.jeez.annotations.WebService;
import com.aj.tools.jr.AbsentKeyException;

import md.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflineGetServlet;

/**
 * @author Joan */
public class CheckUsernameService extends IOCore {
	public final static String url="/check/uname";

	/**
	 * Check if username's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value="uname",rules={PatternsHolder.uname}) }))
	public static JSONObject checkUsername(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		
		JSONObject usernameCheck = IOCore.checkUsername(params);
		
		if(usernameCheck!=null) 
			return usernameCheck;
		
		return Response.reply();
	}

}
