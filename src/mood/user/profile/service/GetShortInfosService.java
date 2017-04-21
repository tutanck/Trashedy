package mood.user.profile.service;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import mood.user.profile.service.core.UserProfileCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;

import org.bson.types.ObjectId;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

/**
 * @author AJoan */
 public class GetShortInfosService{
	 public final static String url="/user/infos";
 
	/**
	 * @description 
	 * return username , firstname and lastname, etc 
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
		
		DBObject user=  THINGS.getOne(
				JR.wrap("_id",new ObjectId(params.getString("uther"))), 
				UserProfileCore.collection);

		return Response.reply(
				JR.wrap("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")) );
	}

}