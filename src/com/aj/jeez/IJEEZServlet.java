package com.aj.jeez;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * * @author Anagbla Joan */
public interface IJEEZServlet {

	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response, 
			JSONObject params
			)throws Exception;

	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception;

}