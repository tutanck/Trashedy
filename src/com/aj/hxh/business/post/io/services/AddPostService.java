package com.aj.hxh.business.post.io.services;

import org.json.JSONObject;

import com.aj.hxh.business.post.io.services.core.PostCore;
import com.aj.hxh.business.user.io.db.SessionDB;
import com.aj.hxh.conf.servletspolicy.OnlinePostServlet;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

/**
 * 
 * @author AJoan
 * Post are need search representation */
public class AddPostService extends PostCore{
	public final static String url="/post/add";
	
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
							@Param(value="txt"),//job search key words 
							@Param(value="desc"),
							@Param(value="unqlf",type=boolean.class) //TODO TEST //ready to do unqualified work (not in relation with skills)
							},
					optionals={
							@Param(value="now",type=boolean.class),//immediate work
							@Param(value="whr"),//place where the job is {lat,lon}
							@Param(value="dur"),//estimated duration
							@Param(value="sal"),//salary (string)
					}))
	public static JSONObject addPost(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		JSONObject decrypted = SessionDB.decrypt(params,"uid");
		THINGS.update(JR.wrap("_id",decrypted.get("uid")),decrypted,true,collection);
		return Response.reply();
	}
}
