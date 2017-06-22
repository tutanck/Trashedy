package hxh.business.user.follow.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import hxh.business.user.follow.services.core.FollowCore;
import hxh.business.user.io.db.SessionDB;
import hxh.conf.servletspolicy.OnlineGetServlet;
import hxh.tools.db.DBException;
import hxh.tools.services.Response;
import hxh.tools.services.ShouldNeverOccurException;

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
