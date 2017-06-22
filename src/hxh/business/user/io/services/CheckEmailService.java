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
public class CheckEmailService extends IOCore {
	public final static String url="/check/email";
	
	/**
	 * Check if email's input is valid 
	 * @param params
	 * @return
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflineGetServlet.class,
			requestParams=@Params({ @Param(value="email",rules={PatternsHolder.email}) }))
	public static JSONObject checkEmail(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		
		JSONObject emailCheck = IOCore.checkEmail(params);
		
		if(emailCheck!=null) 
			return emailCheck;
		
		return Response.reply();
	}

}
