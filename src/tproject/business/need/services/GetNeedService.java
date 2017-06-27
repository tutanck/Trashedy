package tproject.business.need.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import tproject.business.need.services.core.NeedCore;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan
 * Post are need search representation */
public class GetNeedService extends NeedCore{
	public final static String url="/need/up";

	public final static String _nid="nid";

	/**
	 * update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params( 
					value={
							@Param(value=_nid)
					}))
	public static JSONObject up(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	

		DBObject need = THINGS.getOne(
				JR.renameKeys(JR.slice(params,_nid),_nid+"->_id")
				,needdb);

		return need == null ? 
				Response.issue(ServiceCodes.UNKNOWN_RESOURCE)
				:
					Response.reply(need);
	}
}
