package tproject.business.talk.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCursor;

import tproject.business.talk.core.TalkCore;
import tproject.business.talk.db.TalkDB;
import tproject.business.user.profile.services.GetUnameService;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan
 * Post are need search representation */
public class GetTalkService extends TalkCore{
	public final static String url="/talk/get";

	public final static String _entity="entity";
	public final static String _msg="msg";
	public final static String _fromName="fromname";
	public final static String _toName="toname";

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
							@Param(value=TalkDB._to)
					}))
	public static JSONObject get(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	

		JSONArray jar = new JSONArray();
		DBCursor dbc = THINGS.get(
				A_B(
						params.getString(Common._userID)
						,params.getString(TalkDB._to)
						)
				,talkdb);

		while(dbc.hasNext()){
			JSONObject msg = JR.jsonify(dbc.next());
			jar.put(
					msg
					.put(_entity, _msg)
					.put(
							_fromName, 
							JR.renameKeys(
									GetUnameService.getUname(JR.slice(msg,TalkDB._from))
									,TalkDB._from+"->"+GetUnameService._uther
									)
							)
					.put(
							_toName, 
							JR.renameKeys(
									GetUnameService.getUname(JR.slice(msg,TalkDB._to))
									,TalkDB._to+"->"+GetUnameService._uther
									)
							)
					);
		}
		return Response.reply(jar);
	}
}
