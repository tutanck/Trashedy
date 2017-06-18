package business.user.io.services;

import org.json.JSONObject;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;
import com.aj.regina.DBCommit;
import com.aj.regina.THINGS;
import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;
import com.mongodb.WriteResult;

import business.user.io.services.core.IOCore;
import tools.db.DBException;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

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
