package com.aj.mood.users.profile.service;

import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.RequestParams;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.profile.service.core.UserProfileCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OnlineGetServlet;

/**
 * @author AJoan */
 public class GetShortInfosService{
	 public final static String url="/user/infos";
	 public final static String servletName="user_infos";
 
	/**
	 * @description 
	 * return username , firstname and lastname, etc 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	 @WebService(id=servletName,urlPattern=url,policy = OnlineGetServlet.class,
				requestParams=@RequestParams({@Param("uther")}))
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user=  THINGS.getOne(
				JR.wrap("_id",new ObjectId(params.getString("uther"))), 
				UserProfileCore.collection);

		return Response.reply(
				JR.wrap("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")),null,
				Caller.signature());
	}

}