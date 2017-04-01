package mood.users.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.InvalidKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.ServiceCaller;
import com.mongodb.DBObject;

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.general.InputType;
import tools.general.PatternsHolder;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;

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
	
		switch (PatternsHolder.determineFormat(params.getString("username"))) {
	
		case EMAIL:
			System.out.println("username input format : "+InputType.EMAIL);//Debug
	
			JSONObject byEmail= JSONRefiner.renameJSONKeys(params,new String[]{"username->email"});
	
			if (THINGS.exists(JSONRefiner.slice(
					byEmail,new String[]{"email","pass"}),UserIOCore.collection))
				user = THINGS.getOne(JSONRefiner.slice(	
						byEmail,new String[]{"email"}),UserIOCore.collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;
	
		case NUMS:
			System.out.println("username input format : "+InputType.NUMS);//Debug
	
			JSONObject byPhone= JSONRefiner.renameJSONKeys(params,new String[]{"username->phone"});
	
			if (THINGS.exists(JSONRefiner.slice(
					byPhone,new String[]{"phone","pass"}),UserIOCore.collection))
				user= THINGS.getOne(JSONRefiner.slice(
						byPhone,new String[]{"phone"}),UserIOCore.collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;	 
	
		case USERNAME:
			System.out.println("username input format : "+InputType.USERNAME);//Debug
	
			if(THINGS.exists(JSONRefiner.slice(
					params,new String[]{"username", "pass"}),UserIOCore.collection))
				user = THINGS.getOne(JSONRefiner.slice(
						params,new String[]{"username"}),UserIOCore.collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;
	
		default:
			System.out.println("username input format : "+InputType.UNKNOWN);//Debug
			return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
		}
	
		if(!THINGS.exists(
				JSONRefiner.wrap("_id", user.get("_id"))
				.put("confirmed", true)
				,UserIOCore.collection))
			return JSONResponse.alert(ServiceCodes.USER_NOT_CONFIRMED);
	
		//2 different devices can't be connected at the same time
		THINGS.remove(
				JSONRefiner.wrap("uid", user.get("_id"))
				,UserIOCore.session);
	
		String himitsu = ServicesToolBox.generateToken();
	
		THINGS.add(
				JSONRefiner.wrap("skey",ServicesToolBox.scramble(himitsu+params.getString("did")))
				.put("uid", user.get("_id"))
				,UserIOCore.session);
	
		return JSONResponse.answer(
				JSONRefiner.wrap("himitsu", himitsu)
				.put("username",user.get("username")),
				ServiceCaller.whichServletIsAsking().hashCode());
	}

}
