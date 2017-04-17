package com.aj.mood.users.io.services;

import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.Caller;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.WriteResult;
import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.RequestParams;
import com.aj.jeez.annotation.annotations.WebService;
import com.aj.mood.users.io.services.core.UserIOCore;
import com.aj.moodtools.db.DBException;
import com.aj.moodtools.services.Response;
import com.aj.moodtools.services.ServiceCodes;
import com.aj.moodtools.services.ShouldNeverOccurException;
import com.aj.moodtools.servletspolicy.OfflinePostServlet;

/**
 * @author Joan */
public class UserAccountConfirmationService {
	public final static String url="/account/confirm";
	public final static String id="confirm_account";

	/**
	 * @description 
	 * confirm a user account (email is verified)
	 * @param params
	 * @return 
	 * @throws ShouldNeverOccurException
	 * @throws DBException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	@WebService(id=id,urlPattern=url,policy=OfflinePostServlet.class,
			requestParams=@RequestParams({@Param("ckey")}))
	public static JSONObject confirmUser(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException, InvalidKeyException{ 
	
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
