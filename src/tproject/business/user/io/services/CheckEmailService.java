package tproject.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.db.UserDB;
import tproject.business.user.io.services.core.IOCore;
import tproject.conf.servletspolicy.OfflineGetServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
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
			requestParams=@Params({ @Param(value=UserDB._email,rules={PatternsHolder.email}) }))
	public static JSONObject isEmailTaken(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		return Response.reply(THINGS.exists(JR.slice(params,UserDB._email),userdb));
	}

}
