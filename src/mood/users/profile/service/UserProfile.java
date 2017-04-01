package mood.users.profile.service;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.InvalidKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.ServiceCaller;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;
import org.json.JSONException;
import org.json.JSONObject;

import mood.users.io.db.UserIODB;
import mood.users.io.db.UserSessionDB;

import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UserProfile{

	private static DBCollection collection = UserIODB.collection;


	/**
	 * @description update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {
		JSONObject clear = UserSessionDB.clarifyParams(params);

		if(clear.has("email") && THINGS.exists(
				JSONRefiner.slice(clear,new String[]{"email"})
				.put("_id",JSONRefiner.wrap("$ne",params.get("uid")))
				,collection))
			return JSONResponse.issue(ServiceCodes.EMAIL_IS_TAKEN);

		if(clear.has("phone") && THINGS.exists(
				JSONRefiner.slice(clear,new String[]{"phone"})
				.put("_id",JSONRefiner.wrap("$ne",params.get("uid")))
				,collection))
			return JSONResponse.issue(ServiceCodes.PHONE_IS_TAKEN);	

		THINGS.putOne(JSONRefiner.wrap("_id",params.get("uid")),clear,collection);

		return JSONResponse.reply(null,null,ServiceCaller.whichServletIsAsking().hashCode());
	}



	/**
	 * @description 
	 * return user's complete profile information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	public static JSONObject getProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {	
		DBObject user=null;
		JSONObject profile=new JSONObject();
		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(params.has("uther")) 
			THINGS.getOne(
					JSONRefiner.renameJSONKeys(
							JSONRefiner.slice(params, new String[]{"uther"}),
							new String[]{"uther->_id"}), 
					collection);
		else{
			user = THINGS.getOne(JSONRefiner.renameJSONKeys(
					JSONRefiner.slice(UserSessionDB.clarifyParams(params), new String[]{"uid"}),
					new String[]{"uid->_id"}),collection);
			profile.put("self",true);
		}

		return JSONResponse.reply(
				profile
				.put("username",user.get("username"))
				.put("email",user.get("email"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname"))
				.put("birthdate",user.get("birthdate"))
				.put("phone",user.get("phone")),null,
				ServiceCaller.whichServletIsAsking().hashCode());
	}



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
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user=  THINGS.getOne(
				JSONRefiner.wrap("_id",new ObjectId(params.getString("uther"))), 
				collection);

		return JSONResponse.reply(
				JSONRefiner.wrap("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")),null,
				ServiceCaller.whichServletIsAsking().hashCode());
	}

	

	
	public static void main(String[] args) throws DBException, JSONException {

	}

}
