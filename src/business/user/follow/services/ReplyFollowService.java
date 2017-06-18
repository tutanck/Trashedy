package business.user.follow.services;

import java.util.Date;

import org.json.JSONObject;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import business.user.follow.services.core.FollowCore;
import business.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

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
