package mood.users.io.services;

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

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

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
			return JSONResponse.issue(ServiceCodes.UNKNOWN_RESOURCE);
	
		//Better to throw an except and broke the server 
		//so that an ISE is returned back to the client
		//and we avoid more inconsistency damage/issues on the database
		if(wr.getN()>1)
			throw new ShouldNeverOccurException("Inconsistent DBCollection : "+UserIOCore.collection);
	
		return JSONResponse.reply(null,null,Caller.signature());
	}

}
