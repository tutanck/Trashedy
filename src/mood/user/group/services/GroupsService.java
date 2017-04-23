package mood.user.group.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.*;

import mood.user.group.services.core.GroupCore;
import mood.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

public class GroupsService extends GroupCore{
	public final static String url="/user/groups/";


	@WebService(value=url,policy=OnlinePostServlet.class)
	public static JSONObject members(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException{

		DBCursor dbc = THINGS.get(
				JR.slice(SessionDB.decrypt(params,"owner"),"owner"),collection);

		JSONArray jar = new JSONArray();
		while (dbc.hasNext()){			 
			DBObject dbo=dbc.next();			
			jar.put(JR.wrap("gid",dbo.get("_id")) 
					.put("name",dbo.get("name"))
					.put("owner",dbo.get("owner"))
					.put("gdate",dbo.get("gdate")));
		}
		
		return Response.reply(jar);
	}

}