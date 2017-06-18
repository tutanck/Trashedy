package md.user.follow.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import md.user.follow.services.core.FollowCore;
import md.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;

/**
 * @author AJoan */
public class FollowersListService extends FollowCore{
	public final static String url="/user/followers";

	@WebService(value=url,policy=OnlineGetServlet.class)
	public static JSONObject followers(
			JSONObject params
			) throws ShouldNeverOccurException, AbsentKeyException, DBException{
		JSONArray followers=new JSONArray();

		DBCursor dbc = THINGS.get(JR.slice(SessionDB.decrypt(params,"fid"),"fid"),collection);

		while(dbc.hasNext()){
			DBObject dbo = dbc.next();
			followers.put(
				JR.wrap("uid",dbo.get("uid"))
				.put("type","user")
				.put("uname",
						THINGS.getOne(JR.wrap("uid",dbo.get("uid")),collection)
						.get("uname"))
				);
		}
		return Response.reply(JR.wrap("followers",followers));
	}


}
