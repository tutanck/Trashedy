package com.aj.mood.users.search.servlets;

import tools.servletspolicy.OnlineGetServlet;

import org.json.JSONObject;

import com.aj.mood.users.search.service.SearchUser;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/user/search"})
public class SearchUserServlet extends OnlineGetServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.expectedIn.addAll(Arrays.asList(new String[]{"query"}));
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return SearchUser.searchUser(params);
	}
}