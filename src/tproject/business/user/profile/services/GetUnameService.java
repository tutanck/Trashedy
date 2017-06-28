package tproject.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import tproject.business.user.io.db.UserDB;
import tproject.business.user.profile.core.ProfileCore;
import tproject.conf.servletspolicy.OnlineGetServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

import org.bson.types.ObjectId;
import org.json.JSONObject;

/**
 * @author AJoan */
 public class GetUnameService extends ProfileCore{
	 public final static String url="/user/proile/get/short";
 
	 /*In*/
	 public final static String _uther="uther";
	 
	 /*Out*/
	 public final static String _uname="uname";
	 public final static String _entity="entity";
	 public final static String _user="user";

	 
	 
	 @WebService(value=url,policy = OnlineGetServlet.class,
				requestParams=@Params({@Param(_uther)}))
	public static JSONObject getUname(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user =  THINGS.getOne(
				JR.wrap("_id",new ObjectId(params.getString(_uther))), 
				userdb);
		
		if(user==null)
			return Response.issue(ServiceCodes.UNKNOWN_USER);
		
		JSONObject res = JR.wrap(_entity,_user);
		
		return user.get(UserDB._type).equals("society") ?
				Response.reply(
						res.put(_uname, user.get("sname"))
						)
				:
				Response.reply(
						res.put(_uname, user.get("lname")+" "+user.get("fname"))
						);
	}

}