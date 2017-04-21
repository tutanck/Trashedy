package mood.user.profile.service;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import mood.user.io.db.UserSessionDB;
import mood.user.profile.service.core.UserProfileCore;
import tools.db.DBException;
import tools.services.Response;
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
public class GetProfileService{
	public final static String url="/user/profile";

	/** 
	 * return user's complete profile information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(value=url,policy=OnlineGetServlet.class,
			requestParams=@Params(optionals={@Param("uther")}))
	public static JSONObject getProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		DBObject user=null;
		JSONObject profile=new JSONObject();
		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(params.has("uther")) 
			THINGS.getOne(JR.renameKeys(
					JR.slice(params,"uther"),"uther->_id"), 
					UserProfileCore.collection);
		else{
			user = THINGS.getOne(JR.renameKeys(
					JR.slice(UserSessionDB.clarifyParams(params), "uid"),"uid->_id"),
					UserProfileCore.collection);
			profile.put("self",true);
		}

		return Response.reply(
				profile
				.put("username",user.get("username"))
				.put("email",user.get("email"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname"))
				.put("birthdate",user.get("birthdate"))
				.put("phone",user.get("phone")) );
	}
}