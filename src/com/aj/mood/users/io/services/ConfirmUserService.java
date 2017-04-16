package com.aj.mood.users.io.services;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.InvalidKeyException;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.WriteResult;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ServiceCodes;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflinePostServlet;

/**
 * @author Joan */
public class ConfirmUserService {
	public final static String url="/account/confirm";

	/**
	 * @description 
	 * confirm a user account (email is verified)
	 * @param params
	 * @return 
	 * @throws ShouldNeverOccurException
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	@WebService(
			webServlet = @WebServlet(urlPatterns={url}),
			expectedIn={"ckey"},
			policy=OfflinePostServlet.class)
	public static JSONObject confirmUser(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, JSONException, AbsentKeyException, InvalidKeyException{ 
	
		WriteResult wr =THINGS.replaceOne(
				JR.renameKeys(params,"ckey->confirmed"), 
				JR.wrap("$set",JR.wrap("confirmed", true)), 
				UserIOCore.collection);
	
		if(wr.getN()<1)
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);
	
		//Better to throw an except and broke the server 
		//so that an ISE is returned back to the client
		//and we avoid more inconsistency damage/issues on the database
		if(wr.getN()>1)
			throw new ShouldNeverOccurException("Inconsistent DBCollection : "+UserIOCore.collection);
	
		return Response.reply(null,null,Caller.signature());
	}

}
