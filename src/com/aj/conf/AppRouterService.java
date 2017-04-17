package com.aj.conf;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.WebService;
import com.aj.jeez.annotation.core.StartupListener;
import com.aj.jeez.policy.GetServlet;

/**
 * @author Joan */
public class AppRouterService {
	public final static String url="/jz/app/routes"; 
	public final static String id="jz_app_routes";

	
	@WebService(ID=id,urlPattern=url,policy=GetServlet.class)
	public static JSONObject getRouter(
			JSONObject params
			) throws JSONException  {
		return StartupListener.router;
	}

}
