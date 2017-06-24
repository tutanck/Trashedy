package tproject.business.user.follow.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import tproject.business.user.follow.services.core.FollowCore;
import tproject.business.user.io.db.SessionDB;
import tproject.conf.servletspolicy.OnlineGetServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

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
