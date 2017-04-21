package com.aj.mood.users.search.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ShouldNeverOccurException;

public class SearchUser {
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
	
			return Response.reply(new JSONObject().put("users",""/*jar*/));
		}
	
}

