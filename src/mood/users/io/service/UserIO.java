package mood.users.io.service;

import java.util.Date;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.InvalidKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.ServiceCaller;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.json.JSONException;
import org.json.JSONObject;

import mood.users.io.db.UserIODB;
import mood.users.io.db.UserSessionDB;
import tools.general.InputType;
import tools.general.PatternsHolder;
import tools.db.DBException;
import tools.services.JSONResponse;
import tools.services.ServiceCodes;
import tools.services.ServicesToolBox;
import tools.mailing.SendEmail;
import tools.services.ShouldNeverOccurException;
import tools.lingua.Lingua;
import tools.lingua.StringNotFoundException;

/**
 * @author AJoan
 * Service classes are much more meaningful now , because DB access is automatic
 * This classes will take more significant decision on how their process and dispatch incoming data
 * to DB instead of just forwarding the DataBus as fast as possible without proper inspection.*/
public class UserIO{

	private static DBCollection collection = UserIODB.collection;
	private static DBCollection session = UserSessionDB.collection;
	

	/**
	 * @description 
	 * Users registration service : register a new user
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	public static JSONObject registration(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {

		//--FORMAT VALIDATION (do all format validations bf remote calls like a db access) 
		if(!PatternsHolder.isValidUsername(params.getString("username")))
			return JSONResponse.alert(ServiceCodes.INVALID_USERNAME_FORMAT);

		if(!PatternsHolder.isValidEmail(params.getString("email")))
			return JSONResponse.alert(ServiceCodes.INVALID_EMAIL_FORMAT);

		if(!PatternsHolder.isValidPass(params.getString("pass")))
			return JSONResponse.alert(ServiceCodes.INVALID_PASS_FORMAT);

		//--DB VALIDATION
		if(THINGS.exists(JSONRefiner.slice(params,new String[]{"username"}),collection))
			return JSONResponse.alert(ServiceCodes.USERNAME_IS_TAKEN);		

		if(THINGS.exists(JSONRefiner.slice(params,new String[]{"email"}),collection))
			return JSONResponse.alert(ServiceCodes.EMAIL_IS_TAKEN);

		//--DB WRITEACTION
		String ckey =ServicesToolBox.generateToken();
		THINGS.add(JSONRefiner.slice(params,
				new String[]{"username","pass","email"})
				.put("confirmed", ckey)
				.put("regdate", new Date()),
				collection);

		
		//TODO utiliser un .property pour gerer le nom de racine de l app
		String basedir = "http://localhost:8080/Essais0";
		//TODO recuperer dans @weservlet de la servlet associée le bout d'url "/account/confirm"
		String dir= "/account/confirm";
		try {
			SendEmail.sendMail(
					params.getString("email"),
					Lingua.get("welcomeMailSubject","fr-FR"),
					Lingua.get("welcomeMailMessage","fr-FR")
					+basedir+dir+"?ckey="+ckey);
			
		}catch (StringNotFoundException e) { 
			System.out.println("Dictionary Error : Mail not sent : ");
			e.printStackTrace();
		}catch (Exception e) {
			System.out.println("Mailing Error : Mail not sent : ");
			e.printStackTrace();
			//TODO ameliorer ste merde
			return JSONResponse.answer(
					JSONRefiner.wrap("url",basedir+dir+"?ckey="+ckey),			
					ServiceCaller.whichServletIsAsking().hashCode()
					);
		}

		return JSONResponse.answer(
				null,			
				ServiceCaller.whichServletIsAsking().hashCode()
				);
	}



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
	public static JSONObject confirmUser(
			JSONObject params
			) throws ShouldNeverOccurException, DBException, JSONException, AbsentKeyException, InvalidKeyException{ 

		WriteResult wr =THINGS.updateOne(
				JSONRefiner.renameJSONKeys(params, new String[]{"ckey->confirmed"}), 
				JSONRefiner.wrap("$set",JSONRefiner.wrap("confirmed", true)), 
				collection);
		
		if(wr.getN()<1)
			return JSONResponse.alert(ServiceCodes.UNKNOWN_RESOURCE);
		
		//Better to throw an except and broke the server 
		//so that an ISE is returned back to the client
		//and we avoid more inconsistency damage/issues on the database
		if(wr.getN()>1)
			throw new ShouldNeverOccurException("Inconsistent DBCollection : "+collection);

		return JSONResponse.answer(
				null,
				ServiceCaller.whichServletIsAsking().hashCode());
	}



	/**
	 * @description  Users login service : Connects user into online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException  
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException 
	 * @throws InvalidKeyException */
	public static JSONObject login(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {

		DBObject user;

		switch (PatternsHolder.determineFormat(params.getString("username"))) {

		case EMAIL:
			System.out.println("username input format : "+InputType.EMAIL);//Debug

			JSONObject byEmail= JSONRefiner.renameJSONKeys(params,new String[]{"username->email"});

			if (THINGS.exists(JSONRefiner.slice(
					byEmail,new String[]{"email","pass"}),collection))
				user = THINGS.getOne(JSONRefiner.slice(	
						byEmail,new String[]{"email"}),collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;

		case NUMS:
			System.out.println("username input format : "+InputType.NUMS);//Debug

			JSONObject byPhone= JSONRefiner.renameJSONKeys(params,new String[]{"username->phone"});

			if (THINGS.exists(JSONRefiner.slice(
					byPhone,new String[]{"phone","pass"}),collection))
				user= THINGS.getOne(JSONRefiner.slice(
						byPhone,new String[]{"phone"}),collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;	 

		case USERNAME:
			System.out.println("username input format : "+InputType.USERNAME);//Debug
			
			if(THINGS.exists(JSONRefiner.slice(
					params,new String[]{"username", "pass"}),collection))
				user = THINGS.getOne(JSONRefiner.slice(
						params,new String[]{"username"}),collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;

		default:
			System.out.println("username input format : "+InputType.UNKNOWN);//Debug
			return JSONResponse.alert(ServiceCodes.INVALID_USERNAME_FORMAT);
		}

		if(!THINGS.exists(
				JSONRefiner.wrap("_id", user.get("_id"))
				.put("confirmed", true)
				,collection))
			return JSONResponse.alert(ServiceCodes.USER_NOT_CONFIRMED);
		
		//2 different devices can't be connected at the same time
		THINGS.remove(
				JSONRefiner.wrap("uid", user.get("_id"))
				,session);
		
		String himitsu = ServicesToolBox.generateToken();

		THINGS.add(
				JSONRefiner.wrap("skey",ServicesToolBox.scramble(himitsu+params.getString("did")))
				.put("uid", user.get("_id"))
				,session);

		return JSONResponse.answer(
				JSONRefiner.wrap("himitsu", himitsu)
				.put("username",user.get("username")),
				ServiceCaller.whichServletIsAsking().hashCode());
	}

	
	/**
	 * @description  Users logout service : Disconnects user from online mode
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException */
	public static JSONObject logout(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException {
		THINGS.remove(new JSONObject()
				.put("skey",
						ServicesToolBox.scramble(
								params.getString("skey")
								)
						),session);
		return JSONResponse.answer(
				null,
				ServiceCaller.whichServletIsAsking().hashCode());
	}


	
	
	
	
	
	
	
	
	

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
		if(!THINGS.exists(JSONRefiner.slice(params, new String[]{"email"}),collection))
			return JSONResponse.alert(ServiceCodes.UNKNOWN_EMAIL_ADDRESS);

		//Generate temporary key (sequence of 32 hexadecimal digits) using MD5 hashes algorithm 
		//reset password temporarily until user redefine it! 
		String secret = ServicesToolBox.generateToken();
		THINGS.updateOne(
				JSONRefiner.wrap("pass", secret),
				JSONRefiner.slice(params, new String[]{"email"}),
				collection);

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


	public static void main(String[] args) throws DBException, JSONException {
		 
	}

}
