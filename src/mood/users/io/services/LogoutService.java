package mood.users.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.utils.ServiceCaller;

import mood.users.io.core.UserIOCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;

public class LogoutService {

	/**
	 * @description  Users logout service : Disconnects user from online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException */
	public static JSONObject logout(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException {
		THINGS.remove(new JSONObject()
				.put("skey",
						ServicesToolBox.scramble(
								params.getString("skey")
								)
						),UserIOCore.session);
		return JSONResponse.reply(
				null,null,
				ServiceCaller.whichServletIsAsking().hashCode());
	}

}
