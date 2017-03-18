package mood.users.profile.service;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.InvalidKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.ServiceCaller;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import org.json.JSONException;
import org.json.JSONObject;

import mood.users.io.db.UserIODB;
import mood.users.io.db.UserSessionDB;
import mood.users.places.db.UserPlacesDB;

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
			return JSONResponse.alert(ServiceCodes.EMAIL_IS_TAKEN);

		if(clear.has("phone") && THINGS.exists(
				JSONRefiner.slice(clear,new String[]{"phone"})
				.put("_id",JSONRefiner.wrap("$ne",params.get("uid")))
				,collection))
			return JSONResponse.alert(ServiceCodes.PHONE_IS_TAKEN);	

		THINGS.putOne(JSONRefiner.wrap("_id",params.get("uid")),clear,collection);

		return JSONResponse.answer(
				null,
				ServiceCaller.whichServletIsAsking().hashCode());
	}



	/**
	 * @description 
	 * return user's complete profile information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	public static JSONObject getProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {	
		JSONObject clean = JSONRefiner.clean(params, new String[]{"skey"});
		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(clean.has("uther")) 
			clean.put("_id", params.get("uther"));
		else
			clean.put("_id",params.get("uid"));

		DBObject user=  THINGS.getOne(clean, collection);
		return JSONResponse.answer(
				new JSONObject()
				.put("username",user.get("username"))
				.put("email",user.get("email"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname"))
				.put("birthdate",user.get("birthdate"))
				.put("phone",user.get("phone"))
				.put("places",UserPlacesDB.getPp(params.getString("uid"))),
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
				JSONRefiner.renameJSONKeys(
						JSONRefiner.slice(params, new String[]{"uther"}),
						new String[]{"uther->_id"}), 
				collection);

		return JSONResponse.answer(
				new JSONObject()
				.put("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")),
				ServiceCaller.whichServletIsAsking().hashCode());
	}




	/**
	 * TODO
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException
	 * @throws ShouldNeverOccurException
	 */
	public static JSONObject searchUser(JSONObject params) 
			throws DBException, JSONException, ShouldNeverOccurException {

		/*JSONArray jar=new JSONArray();
		UserDB.searchUser("",params.getString("query"));

			while(rs.next()){
				String type=FriendDB.status(
						UserSession.sessionOwner(skey),rs.getString("uid"));	
				jar.put(new JSONObject()
						.put("uid",rs.getString("uid"))
						.put("type",(type==null)?"user":type)
						.put("username",rs.getString("username"))
						.put("firstname",rs.getString("firstname"))
						.put("lastname",rs.getString("lastname")));
						}*/

		return JSONResponse.answer(
				new JSONObject().put("users",""/*jar*/), 
				ServiceCaller.whichServletIsAsking().hashCode());
	}


	public static void main(String[] args) throws DBException, JSONException {

	}

}
