package tproject.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import tproject.business.user.profile.core.ProfileCore;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlineGetServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan */
public class GetProfileService extends ProfileCore{
	public final static String url="/user/profile/get";

	/*In*/
	public final static String _uther="uther";
	
	/*Out*/
	public final static String _entity="entity";
	public final static String _self="self";
	public final static String _user="user";
	


	/** 
	 * return user's complete profile information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(value=url,policy=OnlineGetServlet.class,
			requestParams=@Params(optionals={@Param(_uther)}))
	public static JSONObject getProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		DBObject user=null;
		JSONObject profile=JR.wrap(_entity,_user);

		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(params.has(_uther)) 
			user = THINGS.getOne(JR.renameKeys(
					JR.slice(params,_uther),_uther+"->_id"), 
					userdb);
		else {
			user = THINGS.getOne(JR.slice(params,Common._userID),userdb);
			profile.put(_self,true);
		}

		return (user==null) ?
				Response.issue(ServiceCodes.UNKNOWN_USER) 
				: 
					Response.reply(JR.merge(profile,JR.jsonify(user)));
	}
}