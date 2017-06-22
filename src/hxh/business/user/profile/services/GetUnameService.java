package hxh.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import hxh.business.user.profile.services.core.ProfileCore;
import hxh.conf.servletspolicy.OnlineGetServlet;
import hxh.tools.db.DBException;
import hxh.tools.services.Response;
import hxh.tools.services.ServiceCodes;
import hxh.tools.services.ShouldNeverOccurException;

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
				collection);
		
		return (user==null) ?
				Response.issue(ServiceCodes.UNKNOWN_USER) 
				: Response.reply(
				JR.wrap("uname",user.get("uname")).put("type","user") );
	}

}