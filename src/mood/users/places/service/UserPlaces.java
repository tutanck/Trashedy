package mood.users.places.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.utils.ServiceCaller;

import mood.users.places.db.UserPlacesDB;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ShouldNeverOccurException;


/**
 * @author AJoan
 ***@goodToKnow ! FLUENT STYLE CODE */
public class UserPlaces {

	/**
	 * Update places profile of a user
	 * can be call by both servlets (external call : rpcode!=0)
	 * or services(internal call rpcode=0 (not a nearby servlet call)) 
	 * @param skey
	 * @param places
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException */
	public static JSONObject updatePp(
			JSONObject params
			) 
			throws DBException, JSONException, ShouldNeverOccurException{		
		UserPlacesDB.updatePp(
				params.getString("uid"),
				params.getString("places"));
		return JSONResponse.answer(
				null,
				ServiceCaller.whichServletIsAsking().hashCode());
		} 
	

	/**
	 * @param params
	 * @param remoteuser
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException */
	public static JSONObject getPp(
			JSONObject params
			)throws DBException, JSONException, ShouldNeverOccurException{			
		return JSONResponse.answer(
				new JSONObject()
				.put("places",UserPlacesDB.getPp(
						params.getString("uid"))),
				ServiceCaller.whichServletIsAsking().hashCode());}	

	
	
	public static void main(String[] args) throws  DBException, JSONException {
//		updatePp("nico", "paris mexico japan");
//		updatePp("jo92", "paris");
//		updatePp("jo92", "ctn haie vive");
//		System.out.println(getPp(""));
	}	
}