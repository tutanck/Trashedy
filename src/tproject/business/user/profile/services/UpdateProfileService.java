package tproject.business.user.profile.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;
import com.aj.jeez.tools.__;

import tproject.business.user.io.db.UserDB;
import tproject.business.user.profile.core.ProfileCore;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.general.PatternsHolder;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

import org.json.JSONObject;

/**
 * @author AJoan */
public class UpdateProfileService extends ProfileCore{
	public final static String url="/user/profile/update";

	/**
	 * update user's profile
	 * @param params
	 * @return
	 * @throws DBException 
	 * @throws ShouldNeverOccurException 
	 * @throws AbsentKeyException */
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value=UserDB._email,rules={PatternsHolder.email}),
							@Param(value=UserDB._unqualilified,type=boolean.class) //TODO TEST //ready to do unqualified work (not in relation with skills)
							},
					optionals={
							@Param(value="phone",rules={PatternsHolder.nums}), //TODO change pattern to better +33...
							@Param("sname"),//TODO rules if needed
							@Param("lname"),//TODO rules if needed
							@Param("fname"),//TODO rules  if needed
							@Param("bdate"),//TODO rules if needed
							@Param("skils"),//tab [{skil:"java",dur:3}]//3years of java
							//@Param("intrst"),//tab ["mongo"]person interests //maybe latet TODO	
							@Param("desc")//resume/ description							
					}))
	public static JSONObject updateProfile(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {
		
		if(CheckEmailService.isEmailOwned(params).getBoolean(Response.result)!=true)
			return Response.issue(ServiceCodes.EMAIL_IS_TAKEN);

		if(params.has(UserDB._phone))
			if(CheckPhoneService.isPhoneOwned(params).getBoolean(Response.result)!=true)
				return Response.issue(ServiceCodes.PHONE_IS_TAKEN);
		
		__.outln("params : "+params);//debug

		THINGS.update(JR.wrap(
				"_id",params.get(Common._userID))
				,JR.evict(params,Common._userID)
				,true,userdb);

		return Response.reply();
	}

}
