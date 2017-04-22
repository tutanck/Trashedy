package mood.user.profile.service;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import mood.user.io.db.SessionDB;
import mood.user.profile.service.core.ProfileCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UpdateProfileService extends ProfileCore{
	 public final static String url="/user/update";

	/**
	 * @description update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={@Param("username"),@Param("email")},
			optionals={@Param("phone"),@Param("lastname"),@Param("firstname"),@Param("birthdate")}))
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject decrypted = SessionDB.decrypt(params,"uid");

		if(decrypted.has("email") && THINGS.exists(
				JR.slice(decrypted,"email")
				.put("_id",JR.wrap("$ne",params.get("uid")))
				,collection))
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);

		if(decrypted.has("phone") && THINGS.exists(
				JR.slice(decrypted,"phone")
				.put("_id",JR.wrap("$ne",params.get("uid")))
				,collection))
			return Response.issue(ServiceCodes.PHONE_IS_TAKEN);	

		THINGS.upsertOne(JR.wrap("_id",params.get("uid")),decrypted,collection);

		return Response.reply();
	}

}
