package mood.user.io.services;

import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.WriteResult;

import mood.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

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
	
		WriteResult wr =THINGS.replaceOne(
				JR.renameKeys(params,"ckey->confirmed"), 
				JR.wrap("$set",JR.wrap("confirmed", true))
				,collection);
	
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
