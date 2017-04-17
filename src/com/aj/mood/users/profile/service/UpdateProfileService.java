package com.aj.mood.users.profile.service;

import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.RequestParams;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.db.UserSessionDB;
import com.aj.mood.users.profile.service.core.UserProfileCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ServiceCodes;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OnlinePostServlet;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UpdateProfileService{
	 public final static String url="/user/update";
	 public final static String id="user_update";

	/**
	 * @description update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(ID=id,urlPattern=url,policy = OnlinePostServlet.class,
			requestParams=@RequestParams(
					value={@Param("username"),@Param("email")},
			optionals={@Param("phone"),@Param("lastname"),@Param("firstname"),@Param("birthdate")}))
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject clear = UserSessionDB.clarifyParams(params);

		if(clear.has("email") && THINGS.exists(
				JR.slice(clear,"email")
				.put("_id",JR.wrap("$ne",params.get("uid")))
				,UserProfileCore.collection))
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);

		if(clear.has("phone") && THINGS.exists(
				JR.slice(clear,"phone")
				.put("_id",JR.wrap("$ne",params.get("uid")))
				,UserProfileCore.collection))
			return Response.issue(ServiceCodes.PHONE_IS_TAKEN);	

		THINGS.putOne(JR.wrap("_id",params.get("uid")),clear,UserProfileCore.collection);

		return Response.reply(null,null,Caller.signature());
	}

}
