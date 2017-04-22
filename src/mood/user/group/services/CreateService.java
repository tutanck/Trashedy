package mood.user.group.services;

import java.util.Date;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;

import mood.user.group.services.core.GroupCore;
import mood.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

public class CreateService extends GroupCore{
	public final static String url="/user/group/create";


	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("name")}))
	public static JSONObject create(
			JSONObject params
			) throws  DBException, ShouldNeverOccurException, AbsentKeyException{

		JSONObject group = JR.slice(SessionDB.decrypt(params,"owner"),"owner","name");

		if(THINGS.exists(group.put("open",true), collection))
			return Response.issue(ServiceCodes.EXISTING_USER_GROUP_NAME);			

		THINGS.add(	group.put("open",true).put("cdate", new Date())	, collection);

		return Response.reply();
	}

}