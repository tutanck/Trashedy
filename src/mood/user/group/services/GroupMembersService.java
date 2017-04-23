package mood.user.group.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.__;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.*;

import mood.user.group.services.core.GroupCore;
import mood.user.io.db.SessionDB;
import mood.user.io.db.UserDB;
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

		DBObject dbo = THINGS.getOne( 
				JR.renameKeys(JR.slice(SessionDB.decrypt(params,"owner"),"owner","gid")
						,"gid->_id").put("open",true),collection);

		if(dbo == null)
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);	

		JSONArray jar = new JSONArray();
		for(Object memberID : THINGS.undressArray((BasicDBList) dbo.get("members"))){
			String mid = (String)memberID;
			DBObject member = THINGS.getOne(JR.wrap("_id",mid),UserDB.collection);
			if(member==null) __.explode(new ShouldNeverOccurException("Inconsistent database transaction : abort!"));
			jar.put( JR.wrap("mid",mid)
					.put("type","groupmember")
					.put("uname",member.get("uname"))
					);
		}

		return Response.reply(jar);
	}

}