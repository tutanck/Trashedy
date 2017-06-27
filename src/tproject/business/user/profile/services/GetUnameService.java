package tproject.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import tproject.business.user.profile.services.core.ProfileCore;
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
 
	/** 
	 * return uname , fname and lname 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	 @WebService(value=url,policy = OnlineGetServlet.class,
				requestParams=@Params({@Param("uther")}))
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user =  THINGS.getOne(
				JR.wrap("_id",new ObjectId(params.getString("uther"))), 
				userdb);
		
		return (user==null) ?
				Response.issue(ServiceCodes.UNKNOWN_USER) 
				: Response.reply(
				JR.wrap("uname",user.get("uname")).put("type","user") );
	}

}