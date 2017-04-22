package mood.user.follow.services;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import mood.user.follow.services.core.FollowCore;
import mood.user.io.db.SessionDB;
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
		ArrayList<String>fidList= new ArrayList<>(); 	

		THINGS.get(JR.slice(SessionDB.decrypt(params,"fid"), "fid"), collection);

		JSONArray followers=new JSONArray();
		for(String fid : fidList)
			followers.put(new JSONObject()
					.put("uid",fid)
					.put("type","follower")
					.put("username",
							THINGS.getOne(
									JR.slice(SessionDB.decrypt(params,"uid"), "uid"), collection)
							.get("username")));
		
		return Response.reply(JR.wrap("followers",followers));
	}


}