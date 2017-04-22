package mood.user.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.mongodb.DBObject;

import mood.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.general.InputType;
import tools.general.PatternsHolder;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

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
				@Param(value="uname",rules={PatternsHolder.username}),
				@Param(value="pass",rules={PatternsHolder.pass}),
				@Param("did")}))
	public static JSONObject login(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {

		DBObject user;
		InputType it = determineFormat(params.getString("username"));
		System.out.println("username input format : "+it);//Debug

		JSONObject renamed = JR.renameKeys(params,"username->"+it.toString());
		System.out.println("renamed: "+renamed);//Debug

		if (!THINGS.exists(JR.slice(renamed,it.toString(),"pass"),collection))
			return Response.issue(ServiceCodes.WRONG_LOGIN_PASSWORD);

		user = THINGS.getOne(JR.slice(renamed,it.toString()),collection);

		if(!THINGS.exists(JR.wrap("_id",
				user.get("_id")).put("confirmed", true)
				,collection))
			return Response.issue(ServiceCodes.USER_NOT_CONFIRMED);

		//2 different devices can't be connected at the same time
		THINGS.remove(JR.wrap("uid", user.get("_id")),session);

		String himitsu = ServicesToolBox.generateToken();

		THINGS.add(
				JR.wrap("skey",ServicesToolBox.scramble(himitsu+params.getString("did")))
				.put("uid",user.get("_id"))
				,session);

		return Response.reply(
				JR.wrap("himitsu", himitsu)
				.put("username",user.get("username")));
	}

	public static void main(String[] args) throws JSONException, DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {
		login(new JSONObject().put("username", "044747").put("pass","value"));
	}

}
