package hxh.business.user.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;

import hxh.business.user.io.services.core.IOCore;
import hxh.conf.servletspolicy.OfflinePostServlet;
import hxh.tools.db.DBException;
import hxh.tools.general.PatternsHolder;
import hxh.tools.services.Response;
import hxh.tools.services.ServiceCodes;
import hxh.tools.services.ShouldNeverOccurException;
import hxh.tools.services.ToolBox;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.THINGS;

/**
 * @author Joan */
public class SigninService extends IOCore {
	public final static String url="/signin";

	/**
	 * Users login service : Connects user into online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	@WebService(
			value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({
				@Param(value="uname"),
				@Param(value="pass",rules={PatternsHolder.pass}),
				@Param("did")}))
	public static JSONObject login(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {

		DBObject user;
		String format = determineFormat(params.getString("uname")).toString();
		System.out.println("username input format : "+format);//Debug

		JSONObject renamed = JR.renameKeys(params,"uname->"+format);
		System.out.println("renamed: "+renamed);//Debug

		if (!THINGS.exists(JR.slice(renamed,format,"pass"),collection))
			return Response.issue(ServiceCodes.WRONG_LOGIN_PASSWORD);

		user = THINGS.getOne(JR.slice(renamed,format),collection);

		if(!THINGS.exists(
				JR.wrap("_id",user.get("_id")).put("verifd", true)
				,collection))
			return Response.issue(ServiceCodes.USER_NOT_CONFIRMED);

		//2 different devices can't be connected at the same time
		THINGS.remove(JR.wrap("uid", user.get("_id")),session);

		String himitsu = ToolBox.generateToken();

		THINGS.add(
				JR.wrap("skey",ToolBox.scramble(himitsu+params.getString("did")))
				.put("uid",user.get("_id"))
				,session);

		return Response.reply(
				JR.wrap("himitsu", himitsu)
				.put("username",user.get("username")));
	}

	public static void main(String[] args) throws JSONException, DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {
		login(new JSONObject().put("uname", "044747").put("pass","value"));
	}

}
