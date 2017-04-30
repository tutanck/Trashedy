package md.user.profile.service;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import md.user.io.db.SessionDB;
import md.user.profile.service.core.ProfileCore;
import tools.db.DBException;
import tools.general.PatternsHolder;
import tools.services.Response;
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
	public final static String url="/user/profile/update";

	/**
	 * update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value="uname",rules={PatternsHolder.uname}),
							@Param(value="email",rules={PatternsHolder.email})},
					optionals={
							@Param(value="phone",rules={PatternsHolder.nums}), //TODO change pattern to better +33...
							@Param("lname"),//TODO rules if needed
							@Param("fname"),//TODO rules  if needed
							@Param("bdate")//TODO rules if needed
					}))
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject decrypted = SessionDB.decrypt(params,"uid");

		JSONObject usernameCheck = checkUsername(params);
		if(usernameCheck!=null) return usernameCheck;

		JSONObject emailCheck = checkEmail(params);
		if(emailCheck!=null) return emailCheck;

		if(decrypted.has("phone")){
			JSONObject phoneCheck = checkPhone(params);
			if(phoneCheck!=null) return emailCheck;
		}

		THINGS.update(JR.wrap("_id",decrypted.get("uid")),decrypted,true,collection);

		return Response.reply();
	}

}
