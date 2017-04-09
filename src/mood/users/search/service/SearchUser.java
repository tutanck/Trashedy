package mood.users.search.service;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.tools.Caller;

import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ShouldNeverOccurException;

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
	
			return JSONResponse.reply(
					new JSONObject().put("users",""/*jar*/),
					null,Caller.whoIsAsking().hashCode());
		}
	
}

