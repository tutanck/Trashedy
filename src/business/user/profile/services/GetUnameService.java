package business.user.profile.services;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import business.user.profile.services.core.ProfileCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;

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