package hxh.business.user.follow.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import hxh.business.user.follow.services.core.FollowCore;
import hxh.business.user.io.db.SessionDB;
import hxh.conf.servletspolicy.OnlinePostServlet;
import hxh.tools.db.DBException;
import hxh.tools.services.Response;
import hxh.tools.services.ServiceCodes;
import hxh.tools.services.ShouldNeverOccurException;

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
