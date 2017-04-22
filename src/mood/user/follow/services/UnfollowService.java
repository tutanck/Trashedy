package mood.user.follow.services;

import java.util.Date;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import mood.user.follow.services.core.FollowCore;
import mood.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

/**
 * @author AJoan */
public class UnfollowService extends FollowCore{
	public final static String url="/user/unfollow";


	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("fid")}))
	public static JSONObject unfollow(
			JSONObject params
			) throws DBException, AbsentKeyException, ShouldNeverOccurException{

		JSONObject rel = JR.slice(SessionDB.decrypt(params,"uid"), "uid","fid");

		if(!THINGS.exists(rel, collection))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);			

		THINGS.upsertOne(rel,rel.put("following",false).put("fdate", new Date()), collection);

		return Response.reply();
	}
}
