package mood.users.profile.service;

import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.InvalidKeyException;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.DBObject;

import javax.servlet.annotation.WebServlet;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import mood.users.profile.service.core.UserProfileCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlineGetServlet;

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
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	 @WebService(
				webServlet = @WebServlet(urlPatterns={url}),
				expectedIn={"uther"},
				policy = OnlineGetServlet.class)
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user=  THINGS.getOne(
				JR.wrap("_id",new ObjectId(params.getString("uther"))), 
				UserProfileCore.collection);

		return Response.reply(
				JR.wrap("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")),null,
				Caller.signature());
	}

}