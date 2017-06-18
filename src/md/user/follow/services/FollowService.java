package md.user.follow.services;

import java.util.Date;
import org.json.JSONObject;

import com.aj.jeez.annotations.Param;
import com.aj.jeez.annotations.Params;
import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import md.user.follow.services.core.FollowCore;
import md.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

/**
 * @author AJoan */
public class FollowService extends FollowCore {
	public final static String url="/user/follow";
	
	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("fid")}))//fid=followedID
	public static JSONObject follow(
			JSONObject params
			) throws  DBException, ShouldNeverOccurException, AbsentKeyException{
		
		JSONObject rel = JR.slice(SessionDB.decrypt(params,"uid"),"uid","fid");
		 
		if(THINGS.exists(rel, collection))
			return Response.issue(ServiceCodes.EXISTING_FOLLOW_REL);			
			
			THINGS.add(rel.put("following",false).put("ask_date", new Date()), collection);

		return Response.reply();
	}
}
