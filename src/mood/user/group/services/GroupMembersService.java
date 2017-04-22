package mood.user.group.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.*;

import mood.user.group.services.core.GroupCore;
import mood.user.io.db.SessionDB;
import mood.user.io.db.UserIODB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;


public class GroupMembersService extends GroupCore{
	public final static String url="/user/group/members";


	@WebService(value=url,policy=OnlineGetServlet.class,
			requestParams=@Params(value={@Param("gid")}))
	public static JSONObject members(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException{

		JSONObject group = JR.renameKeys(JR.slice(SessionDB.decrypt(params,"owner"),"owner","gid"),"gid->_id");

		DBObject dbo = THINGS.getOne(group,collection);
		
		if(dbo == null)
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);	

		JSONArray jar = new JSONArray();
		for(Object member : THINGS.undressJSON((BasicDBList) dbo.get("members"))){
			String mid = (String)member;	
			jar.put(JR.wrap("mid",mid));
			jar.put(JR.wrap("uname",THINGS.getOne(JR.wrap("_id",mid), UserIODB.collection)));
		}
		return Response.reply(jar);
	}

}