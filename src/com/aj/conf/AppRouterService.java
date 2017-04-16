package com.aj.conf;

import javax.servlet.annotation.WebServlet;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.jeez.annotations.WebService;
import com.aj.jeez.annotations.core.StartupListener;
import com.aj.jeez.policy.GetServlet;

/**
 * @author Joan */
public class AppRouterService {
	public final static String url="/jz/app/routes"; 
	 public final static String servletName="jz_app_routes";

	
	@WebService(webServlet = @WebServlet(name=servletName,urlPatterns={url}),policy=GetServlet.class)
	public static JSONObject getRouter(
			JSONObject params
			) throws JSONException  {
		return StartupListener.router;
	}

}
