package md.user.follow.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.WebService;
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
public class FolloweesListService extends FollowCore{
	public final static String url="/user/followees";

	@WebService(value=url,policy=OnlineGetServlet.class)
	public static JSONObject followees(
			JSONObject params
			) throws ShouldNeverOccurException, AbsentKeyException, DBException{
		JSONArray followees=new JSONArray();

		DBCursor dbc = THINGS.get(JR.slice(SessionDB.decrypt(params,"uid"),"uid"),collection);

		while(dbc.hasNext()){
			DBObject dbo = dbc.next();
			followees.put(
				JR.wrap("fid",dbo.get("fid"))
				.put("type","user")
				.put("uname",
						THINGS.getOne(JR.wrap("fid",dbo.get("fid")),collection)
						.get("uname"))
				);
		}
		return Response.reply(JR.wrap("followees",followees));
	}


}
