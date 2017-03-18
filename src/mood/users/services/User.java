package mood.users.services;

import java.util.Date;
import java.util.List;

import com.aj.regina.THINGS;
import com.aj.utils.AbsentKeyException;
import com.aj.utils.InvalidKeyException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.ServiceCaller;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;

import org.apache.commons.codec.digest.DigestUtils;
import org.json.JSONException;
import org.json.JSONObject;

import mood.users.db.UserPlacesProfileDB;
import mood.users.db.UserSessionDB;
import mood.users.db.UserDB;
import mood.users.utils.InputType;
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
public class User{

	private static DBCollection collection = UserDB.collection;
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

		try {
			//TODO utiliser un .property pour gerer le nom de racine de l app
			String basedir = "http://localhost:8080/Essais0";
			//TODO recuperer dans @weservlet de la servlet associée le bout d'url "/account/confirm"
			String dir= "/account/confirm";
			SendEmail.sendMail(
					params.getString("email"),
					Lingua.get("welcomeMailSubject","fr-FR"),
					Lingua.get("welcomeMailMessage","fr-FR")
					+basedir+dir+"?ckey="+ckey);
			
		}catch (StringNotFoundException e) { 
			System.out.println("Dictionary Error : Mail not send");
			e.printStackTrace();
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
			System.out.println("input format : "+InputType.EMAIL);//Debug

			JSONObject byEmail= JSONRefiner.renameJSONKeys(params,new String[]{"username->email"});

			if (THINGS.exists(JSONRefiner.slice(
					byEmail,new String[]{"email","pass"}),collection))
				user = THINGS.getOne(JSONRefiner.slice(	
						byEmail,new String[]{"email"}),collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;

		case NUMS:
			System.out.println("input format : "+InputType.NUMS);//Debug

			JSONObject byPhone= JSONRefiner.renameJSONKeys(params,new String[]{"username->phone"});

			if (THINGS.exists(JSONRefiner.slice(
					byPhone,new String[]{"phone","pass"}),collection))
				user= THINGS.getOne(JSONRefiner.slice(
						byPhone,new String[]{"phone"}),collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;	 

		case AWORD:
			System.out.println("input format : "+InputType.AWORD);//Debug
			
			if(THINGS.exists(JSONRefiner.slice(
					params,new String[]{"username", "pass"}),collection))
				user = THINGS.getOne(JSONRefiner.slice(
						params,new String[]{"username"}),collection);
			else return JSONResponse.alert(ServiceCodes.WRONG_LOGIN_PASSWORD);
			break;

		default:
			System.out.println("input format : "+InputType.UNKNOWN);//Debug
			return JSONResponse.alert(ServiceCodes.INVALID_USERNAME_FORMAT);
		}

		if(!THINGS.exists(new JSONObject()
				.put("_id", (String) user.get("_id"))
				.put("confirmed", true)
				,collection))
			return JSONResponse.alert(ServiceCodes.USER_NOT_CONFIRMED);

		String himitsu = ServicesToolBox.generateToken();

		String kage = DigestUtils.shaHex(himitsu+params.getString("did"));

		THINGS.add(new JSONObject()
				.put("skey",kage)
				.put("uid", user.get("_id"))
				,session);

		return JSONResponse.answer(
				new JSONObject()
				.put("himitsu", himitsu)
				.put("username",user.get("username")),
				ServiceCaller.whichServletIsAsking().hashCode());
	}




	/**
	 * @description update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {
		String nexturl="/Momento/showprofile";

		JSONObject clean = JSONRefiner.clean(params, new String[]{"skey"});

		if(clean.has("email") && THINGS.exists(JSONRefiner.slice(clean,
				new String[]{"email"})
				.put("_id",
						new JSONObject()
						.put("$ne",params.get("uid")) )
				,collection))
			return JSONResponse.alert(ServiceCodes.EMAIL_IS_TAKEN);

		if(clean.has("phone") && THINGS.exists(JSONRefiner.slice(clean,
				new String[]{"phone"})
				.put("_id",
						new JSONObject()
						.put("$ne",params.get("uid")))
				,collection))
			return JSONResponse.alert(ServiceCodes.PHONE_IS_TAKEN);

		//Branch the json (dissociate) like separating the yolk from the egg white
		List<JSONObject> node = JSONRefiner.branch(clean, new String[]{"places"});	

		//Update of user profile in users collection
		THINGS.putOne(new JSONObject().put("_id",params.get("uid")),node.get(1),collection);

		//UserPlacesProfile.updatePp(_id, node.get(0));//TODO 

		return JSONResponse.answer(
				new JSONObject()
				.put("nexturl",nexturl),
				ServiceCaller.whichServletIsAsking().hashCode());
	}



	/**
	 * @description 
	 * return user's complete profile information 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	public static JSONObject getProfile(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException {	
		JSONObject clean = JSONRefiner.clean(params, new String[]{"skey"});
		//Trick : like fb, an user can see his profile as someone else
		//uther as a contraction of user-other (other user)
		if(clean.has("uther")) 
			clean.put("_id", params.get("uther"));
		else
			clean.put("_id",params.get("uid"));

		DBObject user=  THINGS.getOne(clean, collection);
		return JSONResponse.answer(
				new JSONObject()
				.put("username",user.get("username"))
				.put("email",user.get("email"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname"))
				.put("birthdate",user.get("birthdate"))
				.put("phone",user.get("phone"))
				.put("places",UserPlacesProfileDB.getPp(params.getString("uid"))),
				ServiceCaller.whichServletIsAsking().hashCode());
	}



	/**
	 * @description 
	 * return username , firstname and lastname, etc 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws JSONException 
	 * @throws ShouldNeverOccurException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, JSONException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 

		DBObject user=  THINGS.getOne(
				JSONRefiner.renameJSONKeys(
						JSONRefiner.slice(params, new String[]{"uther"}),
						new String[]{"uther->_id"}), 
				collection);

		return JSONResponse.answer(
				new JSONObject()
				.put("username",user.get("username"))
				.put("firstname",user.get("firstname"))
				.put("lastname",user.get("lastname")),
				ServiceCaller.whichServletIsAsking().hashCode());
	}




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

		return JSONResponse.answer(
				new JSONObject().put("users",""/*jar*/), 
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
		String nexturl="/Momento/signin.jsp";
		THINGS.remove(new JSONObject()
				.put("skey",
						ServicesToolBox.figureIDOut(
								params.getString("token"),
								params.getString("did")
								)
						),session);
		return JSONResponse.answer(
				new JSONObject()
				.put("nexturl",nexturl),
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
	public static JSONObject accessRecover(
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
		/*Map<String, String[]> test=new HashMap<>();
		test.put("username", new String[]{"louis","hd"});
		test.put("pass", new String[]{"fearness12","tove"});
		test.put("email", new String[]{"tutanck@gmail.com"});
		test.put("uid", new String[]{"hardtofindeuid"});
		test.put("skey", new String[]{"hardtobrokekey"});
		//		System.out.println("registration : "+registration(test)+"\n");
		//System.out.println("login : "+login(test)+"\n");
		//System.out.println("updateProfile : "+updateProfile(test)+"\n");
		System.out.println("accessRecover : "+accessRecover("tutanck@gmail.com")+"\n");
		//System.out.println("logout : "+logout(test)+"\n");*/
		//searchUser("h","j");
	}

}
