package tproject.business.talk.services;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

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
 * @author AJoan */
public class TalkListService extends TalkCore{
	public final static String url="/talk/list";

	public final static String _entity="entity";
	public final static String _speaker="talk";

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

		JSONArray jar = new JSONArray();
		DBCursor dbc = THINGS.get(
				_A_(params.getString(Common._userID))
				,talkdb);

		dbc.sort(THINGS.wrap(TalkDB._at,-1));
		
		Set<String>unicqids=new HashSet<>();
		
		while(dbc.hasNext()){
			JSONObject msg = JR.jsonify(dbc.next());
			
			unicqids.add(msg.getString(TalkDB._from).equals(params.getString(TalkDB._to))==true?
					(String)dbo.get("recipient")
					:
						(String)dbo.get("sender"));
			
			jar.put(
					JR.slice(
							JR.jsonify(msg)
							,TalkDB._to
							)
					.put(_entity, _speaker)
					.put(
							GetUnameService._uname, 
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
