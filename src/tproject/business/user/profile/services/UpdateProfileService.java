package tproject.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.io.db.SessionDB;
import tproject.business.user.profile.services.core.ProfileCore;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UpdateProfileService extends ProfileCore{
	public final static String url="/user/profile/update";

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
							@Param(value="uname",rules={PatternsHolder.uname}),
							@Param(value="mail",rules={PatternsHolder.email}),
							@Param(value="unqlf",type=boolean.class) //TODO TEST //ready to do unqualified work (not in relation with skills)
							},
					optionals={
							@Param(value="phone",rules={PatternsHolder.nums}), //TODO change pattern to better +33...
							@Param("lname"),//TODO rules if needed
							@Param("fname"),//TODO rules  if needed
							@Param("bdate"),//TODO rules if needed
							@Param("skils"),//tab [{skil:"java",dur:3}]//3years of java
							//@Param("intrst"),//tab ["mongo"]person interests //maybe latet TODO	
							@Param("desc")//resume/ description							
					}))
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject decrypted = SessionDB.decrypt(params,"uid");

		JSONObject usernameCheck = checkUsername(params);
		if(usernameCheck!=null) return usernameCheck;

		JSONObject emailCheck = checkEmail(params);
		if(emailCheck!=null) return emailCheck;

		if(decrypted.has("phone")){
			JSONObject phoneCheck = checkPhone(params);
			if(phoneCheck!=null) return emailCheck;
		}

		THINGS.update(JR.wrap("_id",decrypted.get("uid")),decrypted,true,collection);

		return Response.reply();
	}

}
