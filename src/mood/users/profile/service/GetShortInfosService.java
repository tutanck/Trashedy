package mood.users.profile.service;

import com.aj.jeez.annotation.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.AbsentKeyException;
import com.aj.tools.Caller;
import com.aj.tools.InvalidKeyException;
import com.aj.tools.JSONRefiner;
import com.mongodb.DBObject;

import javax.servlet.annotation.WebServlet;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import mood.users.profile.service.core.UserProfileCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;

/**
 * @author AJoan */
 public class GetShortInfosService{
 
	/**
	 * @description 
	 * return username , firstname and lastname, etc 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	 @WebService(
				webServlet = @WebServlet(urlPatterns={"/user/infos"}),
				expectedIn={"uther"},
				policy = OnlineGetServlet.class)
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user=  THINGS.getOne(
				JSONRefiner.wrap("_id",new ObjectId(params.getString("uther"))), 
				UserProfileCore.collection);

		return JSONResponse.reply(
				JSONRefiner.wrap("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")),null,
				Caller.signature());
	}

}