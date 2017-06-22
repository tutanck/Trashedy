package com.aj.hxh.business.user.follow.services;

import org.json.JSONObject;

import com.aj.hxh.business.user.follow.services.core.FollowCore;
import com.aj.hxh.business.user.io.db.SessionDB;
import com.aj.hxh.conf.servletspolicy.OnlinePostServlet;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ServiceCodes;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

/**
 * @author AJoan */
public class UnfollowService extends FollowCore{
	public final static String url="/user/unfollow";


	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("fid")}))//fid=followedID
	public static JSONObject unfollow(
			JSONObject params
			) throws DBException, AbsentKeyException, ShouldNeverOccurException{

		JSONObject rel = JR.slice(SessionDB.decrypt(params,"uid"),"uid","fid");

		if(!THINGS.exists(rel, collection))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);			

		THINGS.update(rel,JR.wrap("$set",JR.wrap("following",false)), collection);

		return Response.reply();
	}
}
