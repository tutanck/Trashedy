package mood.user.group.services;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;

import mood.user.group.services.core.GroupCore;
import mood.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

public class DeleteService extends GroupCore{
	public final static String url="/user/group/delete";
	
	
	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("gid")}))
	public static JSONObject delete(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException{
		
		JSONObject group = JR.renameKeys(JR.slice(SessionDB.decrypt(params,"owner"),"owner","gid")
				,"gid->_id").put("open",true);
			 
		if(!THINGS.exists(group, collection))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);			
			
			THINGS.update(group,JR.wrap("$set",JR.wrap("open",false)),collection);

		return Response.reply();
	}
	
}