package md.user.state.services;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import md.user.io.db.SessionDB;
import md.user.state.services.core.StateCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class GetStateService extends StateCore{
	public final static String url="/user/state/get";

	/** 
	 * return user's complete state information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(value=url,policy=OnlineGetServlet.class,
			requestParams=@Params(optionals={@Param("uther")}))
	public static JSONObject getState(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		DBObject user=null;
		JSONObject profile=new JSONObject();

		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(params.has("uther")) 
			user = THINGS.getOne(JR.renameKeys(
					JR.slice(params,"uther"),"uther->_id"), 
					collection);
		else {
			user = THINGS.getOne(JR.slice(SessionDB.decrypt(params, "uid"),"uid"),collection);
			profile.put("self",true);
		}

		return (user==null) ?
				Response.issue(ServiceCodes.UNKNOWN_USER) 
				: Response.reply(
						profile
						.put("type","user")
						.put("statu",user.get("statu"))
						.put("pos",user.get("pos"))
						);
	}
}