package business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.tools.jr.AbsentKeyException;

import business.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflineGetServlet;

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
