package mood.users.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.AbsentKeyException;
import com.aj.tools.Caller;
import com.aj.tools.InvalidKeyException;
import com.aj.tools.JSONRefiner;
import com.mongodb.DBObject;

import mood.users.io.core.UserIOCore;
import tools.db.DBException;
import tools.general.InputType;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;

/**
 * @author Joan */
public class LoginService {

	/**
	 * @description  Users login service : Connects user into online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException  
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	public static JSONObject login(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {

		DBObject user;
		InputType it = UserIOCore.determineFormat(params.getString("username"));
		System.out.println("username input format : "+it);//Debug

		JSONObject renamed = JSONRefiner.renameKeys(params,new String[]{"username->"+it.toString()});
		System.out.println("renamed: "+renamed);//Debug

		if (THINGS.exists(JSONRefiner.slice(
				renamed,new String[]{it.toString(),"pass"}),UserIOCore.collection))
			user = THINGS.getOne(JSONRefiner.slice(	
					renamed,new String[]{it.toString()}),UserIOCore.collection);
		else return JSONResponse.issue(ServiceCodes.WRONG_LOGIN_PASSWORD);

		if(!THINGS.exists(
				JSONRefiner.wrap("_id", user.get("_id"))
				.put("confirmed", true)
				,UserIOCore.collection))
			return JSONResponse.issue(ServiceCodes.USER_NOT_CONFIRMED);

		//2 different devices can't be connected at the same time
		THINGS.remove(
				JSONRefiner.wrap("uid", user.get("_id"))
				,UserIOCore.session);

		String himitsu = ServicesToolBox.generateToken();

		THINGS.add(
				JSONRefiner.wrap("skey",ServicesToolBox.scramble(himitsu+params.getString("did")))
				.put("uid", user.get("_id"))
				,UserIOCore.session);

		return JSONResponse.reply(
				JSONRefiner.wrap("himitsu", himitsu)
				.put("username",user.get("username")),
				null,Caller.signature());
	}

	public static void main(String[] args) throws JSONException, DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {
		login(new JSONObject().put("username", "044747").put("pass","value"));
	}

}
