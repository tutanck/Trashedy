package tproject.business.need.services;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.need.db.NeedDB;
import tproject.business.need.services.core.NeedCore;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan
 * Post are need search representation */
public class AddNeedService extends NeedCore{
	public final static String url="/need/add";

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
							@Param(value=NeedDB._query),//search key words 
							@Param(value=NeedDB._description),
							@Param(value=NeedDB._type,rules={"(PRODUCTION|TRAINING|ADVICE)"}),
							@Param(value=NeedDB._unqualified,type=boolean.class),//TODO TEST
							@Param(value=NeedDB._beginDate)//date of the beginning of the need
					},
					optionals={
							@Param(value=NeedDB._endDate),//date of the end of the need
							@Param(value=NeedDB._place),//place where the activity is {lat,lon}
							@Param(value=NeedDB._beginDate), //TODO should shout err 
							@Param(value=NeedDB._pay),
							@Param(value=NeedDB._activityDuration),//estimated activity duration 
					}))
	public static JSONObject add(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	

		THINGS.add(params,needdb);

		return Response.reply();
	}
}
