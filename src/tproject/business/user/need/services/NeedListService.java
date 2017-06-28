package tproject.business.user.need.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCursor;

import tproject.business.user.need.core.NeedCore;
import tproject.business.user.need.db.NeedDB;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan */
public class NeedListService extends NeedCore{
	public final static String url="/need/up";

	public final static String _nid="nid";

	/**
	 * update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class)
	public static JSONObject list(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	

		DBCursor dbc = THINGS.get(
				JR.renameKeys(JR.slice(params,_nid),_nid+"->_id")
				,needdb);

		JSONArray jar = new JSONArray();
		while(dbc.hasNext())
			jar.put(
					JR.slice(JR.jsonify(dbc.next()),
							_nid,NeedDB._title)
					);

		return Response.reply(jar);
	}
}
