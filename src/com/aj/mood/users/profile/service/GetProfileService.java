package com.aj.mood.users.profile.service;

import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.mood.users.io.db.UserSessionDB;
import com.aj.mood.users.profile.service.core.UserProfileCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OnlineGetServlet;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class GetProfileService{
	public final static String url="/user/profile";


	/** 
	 * return user's complete profile information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={url}),
			optionalIn={"uther"},
			policy = OnlineGetServlet.class)
	public static JSONObject getProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		DBObject user=null;
		JSONObject profile=new JSONObject();
		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(params.has("uther")) 
			THINGS.getOne(JR.renameKeys(
					JR.slice(params,"uther"),"uther->_id"), 
					UserProfileCore.collection);
		else{
			user = THINGS.getOne(JR.renameKeys(
					JR.slice(UserSessionDB.clarifyParams(params), "uid"),"uid->_id"),
					UserProfileCore.collection);
			profile.put("self",true);
		}

		return Response.reply(
				profile
				.put("username",user.get("username"))
				.put("email",user.get("email"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname"))
				.put("birthdate",user.get("birthdate"))
				.put("phone",user.get("phone")),null,
				Caller.signature());
	}
}