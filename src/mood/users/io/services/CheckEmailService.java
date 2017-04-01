package mood.users.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.utils.AbsentKeyException;
import com.aj.utils.ServiceCaller;

import mood.users.io.core.UserIOCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ShouldNeverOccurException;

public class CheckEmailService {

	/**
	 * Check if email's input is valid 
	 * @param params
	 * @return
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 * @throws DBException
	 * @throws AbsentKeyException */
	public static JSONObject checkEmail(
			JSONObject params
			) throws JSONException, ShouldNeverOccurException, DBException, AbsentKeyException{
	
		JSONObject emailCheck = UserIOCore.checkEmailCore(params);
		if(emailCheck!=null) return emailCheck;
		
		return JSONResponse.reply(
				null,null,	
				ServiceCaller.whichServletIsAsking().hashCode()
				);
	}

}
