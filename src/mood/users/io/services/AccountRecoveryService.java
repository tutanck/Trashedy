package mood.users.io.services;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.ServiceCaller;

import mood.users.io.services.core.UserIOCore;
import tools.db.DBException;
import tools.lingua.Lingua;
import tools.lingua.StringNotFoundException;
import tools.mailing.SendEmail;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.services.ShouldNeverOccurException;

public class AccountRecoveryService {

	/**
	 * @description send an email with MD5 generated temporary access key for access recover to the user
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	public static JSONObject accessRecovery(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {
	
		//Verify if user email exists
		if(!THINGS.exists(JSONRefiner.slice(params, new String[]{"email"}),UserIOCore.collection))
			return JSONResponse.alert(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);
	
		//Generate temporary key (sequence of 32 hexadecimal digits)  
		//reset password temporarily until user redefine it! 
		String secret = ServicesToolBox.generateToken();
		THINGS.replaceOne(
				JSONRefiner.slice(params, new String[]{"email"}),
				JSONRefiner.wrap("pass", secret),
				UserIOCore.collection);
	
		//Send an email to the applicant
		try {
			SendEmail.sendMail(params.getString("email"),Lingua.get("NewAccessKeySentSubject","fr-FR"),
					Lingua.get("NewAccessKeySentMessage","fr-FR")+ secret);
		}
		catch (StringNotFoundException e) { 
			System.out.println("Dictionary Error : Mail not send");
			e.printStackTrace();
		}
		return JSONResponse.answer(
				null,			
				ServiceCaller.whichServletIsAsking().hashCode());
	}

}
