package com.aj.jeez;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

/**
 * * @author Anagbla Joan */
public class GetServlet extends JEEZServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		try{
			JSONObject params = beforeBusiness(request,response);
			if(params!=null)
				afterBusiness(
						request,response,
						doBusiness(request,response,params)
						);
		}catch (Exception e){
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AN INTERNAL SERVER ERROR OCCURRED");
		}
	}

	@Override
	protected void doPost(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		doGet(request, response);
	}

}