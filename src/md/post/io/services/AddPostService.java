package md.post.io.services;

import org.json.JSONObject;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;

import md.post.io.services.core.PostCore;
import md.user.io.db.SessionDB;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OnlinePostServlet;

/**
 * 
 * @author AJoan
 * Post are need search representation */
public class AddPostService extends PostCore{
	public final static String url="/post/add";
	
	/**
	 * update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value="txt"),//job search key words 
							@Param(value="desc"),
							@Param(value="unqlf",type=boolean.class) //TODO TEST //ready to do unqualified work (not in relation with skills)
							},
					optionals={
							@Param(value="now",type=boolean.class),//immediate work
							@Param(value="whr"),//place where the job is {lat,lon}
							@Param(value="dur"),//estimated duration
							@Param(value="sal"),//salary (string)
					}))
	public static JSONObject addPost(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		JSONObject decrypted = SessionDB.decrypt(params,"uid");
		THINGS.update(JR.wrap("_id",decrypted.get("uid")),decrypted,true,collection);
		return Response.reply();
	}
}
