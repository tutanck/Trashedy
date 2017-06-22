package hxh.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.exceptions.AbsentKeyException;

import hxh.business.user.io.services.core.IOCore;
import hxh.conf.servletspolicy.OfflineGetServlet;
import hxh.tools.db.DBException;
import hxh.tools.general.PatternsHolder;
import hxh.tools.services.Response;
import hxh.tools.services.ShouldNeverOccurException;

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
