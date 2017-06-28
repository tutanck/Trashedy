package tproject.business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.core.IOCore;
import tproject.business.user.io.db.UserDB;
import tproject.conf.servletspolicy.OfflinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class AccountConfirmationService extends IOCore {
	public final static String url="/account/confirm";

	public final static String _confirmationKey="ckey";

	
	@WebService(value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({
				@Param(_confirmationKey),
				@Param(value=UserDB._email,rules={PatternsHolder.email}) }))
	public static JSONObject confirmUser(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException, InvalidKeyException{ 

		JSONObject things = JR.renameKeys(
				JR.slice(params,_confirmationKey,UserDB._email),
				_confirmationKey+"->"+UserDB._verified);
		
		if(!THINGS.exists(things, userdb))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);	
		
		THINGS.update(things, 
				JR.wrap("$set",JR.wrap(UserDB._verified, true))
				,userdb);		

		return Response.reply();
	}

}
