package tproject.business.user.contact.services;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.talk.db.TalkDB;
import tproject.business.user.contact.core.ContactCore;
import tproject.business.user.io.db.UserDB;
import tproject.business.user.profile.services.GetUnameService;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * 
 * @author AJoan */
public class ContactListService extends ContactCore{
	public final static String url="/contact/list";

	public final static String _entity="entity";
	public final static String _speaker="talk";


	@WebService(value=url,policy = OnlinePostServlet.class)
	public static JSONObject list(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	

		JSONArray contactList = (JSONArray) THINGS.getOne(
				JR.renameKeys(
						JR.slice(params,Common._userID)
						,Common._userID+"->"+"_id"
						)
				,userdb).get(UserDB._contacts);

		for(Object o : contactList)
			((JSONObject)o)
			.put(_entity, _speaker)
			.put(
					GetUnameService._uname, 
					JR.renameKeys(
							GetUnameService.getUname(
									JR.slice(
											((JSONObject)o),UserDB._cid
											)
									).getJSONObject(Response.result)
							,TalkDB._to+"->"+GetUnameService._uther
							)
					);

		return Response.reply(contactList);
	}
}
