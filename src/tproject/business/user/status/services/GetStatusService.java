package tproject.business.user.status.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import tproject.business.user.status.services.core.StateCore;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlineGetServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan */
public class GetStatusService extends StateCore{

	public final static String url="/user/state/get";

	/*In*/
	public final static String _uther="uther";

	/*Out*/
	public final static String _status ="status"; 
	public final static String _state ="state"; 
	public final static String _position ="pos";
	public final static String _entity="entity";
	public final static String _self="self";
	public final static String _user="user";



	/** 
	 * return user's complete state information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(value=url,policy=OnlineGetServlet.class,
			requestParams=@Params(optionals={@Param(_uther)}))
	public static JSONObject getState(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		DBObject userState = null;
		JSONObject profile = new JSONObject();

		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(params.has(_uther)) 
			userState = THINGS.getOne(
					JR.renameKeys(
							JR.slice(params,_uther),_uther+"->_id"), 
					statedb);
		else {
			userState = THINGS.getOne(
					JR.renameKeys(
							JR.slice(params, Common._userID),Common._userID+"->_id")
					,statedb);
			profile.put("self",true);
		}

		return (userState == null) ?
				Response.issue(ServiceCodes.UNKNOWN_USER) 
				: Response.reply (
						JR.merge(profile,JR.jsonify(userState)
								).put(_entity,_state)
						);
	}
}