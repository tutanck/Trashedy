package com.aj.hxh.business.user.io.services;

import org.json.JSONObject;

import com.aj.hxh.business.user.io.services.core.IOCore;
import com.aj.hxh.conf.servletspolicy.OfflinePostServlet;
import com.aj.hxh.tools.db.DBException;
import com.aj.hxh.tools.services.Response;
import com.aj.hxh.tools.services.ServiceCodes;
import com.aj.hxh.tools.services.ShouldNeverOccurException;
import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.jr.exceptions.InvalidKeyException;
import com.aj.jeez.regina.DBCommit;
import com.aj.jeez.regina.THINGS;
import com.mongodb.WriteResult;

/**
 * @author Joan */
public class UserAccountConfirmationService extends IOCore {
	public final static String url="/account/confirm";
	
	/** 
	 * confirm a user account (email is verified)
	 * @param params
	 * @return 
	 * @throws ShouldNeverOccurException
	 * @throws DBException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy=OfflinePostServlet.class,
			requestParams=@Params({@Param("ckey")}))
	public static JSONObject confirmUser(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, AbsentKeyException, InvalidKeyException{ 
	
		DBCommit commit = THINGS.update(
				JR.renameKeys(params,"ckey->verifd"), 
				JR.wrap("$set",JR.wrap("verifd", true))
				,collection);
	
		WriteResult wr = commit.getWriteResult();
		
		if(wr.getN()<1)
			return Response.issue(ServiceCodes.UNKNOWN_RESOURCE);
	
		//Better to throw an except and broke the server 
		//so that an ISE is returned back to the client
		//and we avoid more inconsistency damage/issues on the database
		if(wr.getN()>1)
			throw new ShouldNeverOccurException("Inconsistent DBCollection : "+collection);
		
		return Response.reply();
	}

}
