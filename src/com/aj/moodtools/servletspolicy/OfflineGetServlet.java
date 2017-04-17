package com.aj.moodtools.servletspolicy;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.policy.GetServlet;

import com.aj.mood.users.io.db.UserSessionDB;

/**
 * * @author Anagbla Joan */
public class OfflineGetServlet extends GetServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		//return UserSessionDB.sessionExists(params);
		return false; //TODO FIND BETTER
	}
}