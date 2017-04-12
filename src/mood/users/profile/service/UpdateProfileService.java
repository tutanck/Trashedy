package mood.users.profile.service;

import com.aj.jeez.annotation.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.AbsentKeyException;
import com.aj.tools.Caller;
import com.aj.tools.JSONRefiner;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import mood.users.io.db.UserSessionDB;
import mood.users.profile.service.core.UserProfileCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UpdateProfileService{

	

	/**
	 * @description update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={"/user/update"}),
			expectedIn={"username","email"},
			optionalIn={"phone","lastname","firstname","birthdate"},
			policy = OnlinePostServlet.class)
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject clear = UserSessionDB.clarifyParams(params);

		if(clear.has("email") && THINGS.exists(
				JSONRefiner.slice(clear,"email")
				.put("_id",JSONRefiner.wrap("$ne",params.get("uid")))
				,UserProfileCore.collection))
			return JSONResponse.issue(ServiceCodes.EMAIL_IS_TAKEN);

		if(clear.has("phone") && THINGS.exists(
				JSONRefiner.slice(clear,"phone")
				.put("_id",JSONRefiner.wrap("$ne",params.get("uid")))
				,UserProfileCore.collection))
			return JSONResponse.issue(ServiceCodes.PHONE_IS_TAKEN);	

		THINGS.putOne(JSONRefiner.wrap("_id",params.get("uid")),clear,UserProfileCore.collection);

		return JSONResponse.reply(null,null,Caller.signature());
	}

}
