package com.aj.mood.users.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.RequestParams;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.general.InputType;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ServiceCodes;
import com.aj.moodtools.services.ServicesToolBox;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflinePostServlet;

/**
 * @author Joan */
public class SigninService {
	public final static String url="/signin";
	public final static String id="signin";

	/**
	 * @description  Users login service : Connects user into online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(
			id=id,urlPattern=url,policy=OfflinePostServlet.class,
			requestParams=@RequestParams({
					@Param("username"),
					@Param("pass"),
					@Param("did")}))
	public static JSONObject login(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {

		DBObject user;
		InputType it = UserIOCore.determineFormat(params.getString("username"));
		System.out.println("username input format : "+it);//Debug

		JSONObject renamed = JR.renameKeys(params,"username->"+it.toString());
		System.out.println("renamed: "+renamed);//Debug

		if (THINGS.exists(
				JR.slice(renamed,it.toString(),"pass")
				,UserIOCore.collection))
			user = THINGS.getOne(JR.slice(renamed,it.toString())
					,UserIOCore.collection);
		else return Response.issue(ServiceCodes.WRONG_LOGIN_PASSWORD);

		if(!THINGS.exists(
				JR.wrap("_id", user.get("_id"))
				.put("confirmed", true)
				,UserIOCore.collection))
			return Response.issue(ServiceCodes.USER_NOT_CONFIRMED);

		//2 different devices can't be connected at the same time
		THINGS.remove(
				JR.wrap("uid", user.get("_id"))
				,UserIOCore.session);

		String himitsu = ServicesToolBox.generateToken();

		THINGS.add(
				JR.wrap("skey",ServicesToolBox.scramble(himitsu+params.getString("did")))
				.put("uid", user.get("_id"))
				,UserIOCore.session);

		return Response.reply(
				JR.wrap("himitsu", himitsu)
				.put("username",user.get("username")),
				null,Caller.signature());
	}

	public static void main(String[] args) throws JSONException, DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {
		login(new JSONObject().put("username", "044747").put("pass","value"));
	}

}
