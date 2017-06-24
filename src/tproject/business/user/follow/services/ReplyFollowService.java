package tproject.business.user.follow.services;

import java.util.Date;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.follow.services.core.FollowCore;
import tproject.business.user.io.db.SessionDB;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class ReplyFollowService extends FollowCore{
	public final static String url="/user/follow/reply";

	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("uid")}))		
	public static JSONObject replyFollow(
			JSONObject params
			) throws  DBException, ShouldNeverOccurException, AbsentKeyException{

		JSONObject rel = JR.slice(SessionDB.decrypt(params,"fid"),"fid","uid");//fid=followedID

		if(!THINGS.exists(rel, collection))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);			

		THINGS.update(rel,JR.wrap("$set",JR.wrap("following",true)).put("fdate", new Date()), collection);

		return Response.reply();
	}

}
