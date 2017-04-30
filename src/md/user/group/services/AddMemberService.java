package md.user.group.services;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.aj.tools.jr.Node;

import md.user.group.services.core.GroupCore;
import md.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

/**
 * @author Joan */
public class AddMemberService extends GroupCore{
	public final static String url="/user/group/member/add";


	@WebService(value=url,policy=OnlinePostServlet.class,
			requestParams=@Params(value={@Param("gid"),@Param("mid")}))
	public static JSONObject add(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException{

		Node<JSONObject> node = JR.branch(params, "mid");	

		JSONObject group = JR.renameKeys(
				JR.slice(SessionDB.decrypt(node.white(),"owner"),"owner","gid")
				,"gid->_id").put("open",true);

		if(!THINGS.exists(group,collection))
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);	

		THINGS.update(group,JR.wrap("$addToSet",JR.wrap("members",node.yellow().getString("mid"))),collection);

		return Response.reply();
	}

}