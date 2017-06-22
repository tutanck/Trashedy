package com.aj.hxh.business.user.profile.services;

import com.aj.hxh.business.user.profile.services.core.ProfileCore;
import com.aj.hxh.conf.servletspolicy.OnlineGetServlet;
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
import com.aj.jeez.regina.THINGS;
import com.mongodb.DBObject;

import org.bson.types.ObjectId;
import org.json.JSONObject;

/**
 * @author AJoan */
 public class GetUnameService extends ProfileCore{
	 public final static String url="/user/proile/get/short";
 
	/** 
	 * return uname , fname and lname 
	 * @param params
	 * @return
	 * @throws DBException
	 * @throws ShouldNeverOccurException 
	 * @throws InvalidKeyException 
	 * @throws AbsentKeyException */
	 @WebService(value=url,policy = OnlineGetServlet.class,
				requestParams=@Params({@Param("uther")}))
	public static JSONObject getShortInfos(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException, InvalidKeyException {		 
		
		DBObject user =  THINGS.getOne(
				JR.wrap("_id",new ObjectId(params.getString("uther"))), 
				collection);
		
		return (user==null) ?
				Response.issue(ServiceCodes.UNKNOWN_USER) 
				: Response.reply(
				JR.wrap("uname",user.get("uname")).put("type","user") );
	}

}