package com.aj.moodtools.servletspolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.policy.GetServlet;
import com.aj.jeez.templating.Param;
import com.aj.mood.users.io.db.UserSessionDB;

/**
 * * @author Anagbla Joan */
public class OnlineGetServlet extends GetServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.auth=true;
		super.requestParams.addExpected(new Param("skey"));
	}

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		return UserSessionDB.sessionExists(params);
	}
}