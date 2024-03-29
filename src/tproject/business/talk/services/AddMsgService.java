package tproject.business.talk.services;

import java.util.Date;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.talk.core.TalkCore;
import tproject.business.talk.db.TalkDB;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan */
public class AddMsgService extends TalkCore{
	public final static String url="/msg/add";


	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value=TalkDB._msg),
							@Param(value=TalkDB._to),
					}))
	public static JSONObject add(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {							

		if(!THINGS.exists(
				JR.renameKeys(
						JR.slice(params,TalkDB._to)
						,Common._userID+"->"+"_id")
				, userdb)) return Response.issue(ServiceCodes.UNKNOWN_INTERLOCUTOR);

		THINGS.add(
				JR.renameKeys(
						JR.slice(params,TalkDB._msg,TalkDB._to)
						,Common._userID+"->"+TalkDB._from)
				.put(TalkDB._at,new Date())
				,talkdb);

		return Response.reply();
	}
}
