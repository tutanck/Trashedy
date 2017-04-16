package com.aj.mood.users.profile.service;

import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

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
	 public final static String servletName="user_update";

	/**
	 * @description update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(name=servletName,urlPatterns={url}),
			expectedIn={"username","email"},
			optionalIn={"phone","lastname","firstname","birthdate"},
			policy = OnlinePostServlet.class)
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {
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
