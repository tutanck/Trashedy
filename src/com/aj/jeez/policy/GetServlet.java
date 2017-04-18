package com.aj.jeez.policy;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.aj.jeez.templating.JEEZServlet;

/**
 * * @author Anagbla Joan */
public class GetServlet extends JEEZServlet{
	private static final long serialVersionUID = 1L;

	@Override
	protected final void doGet(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		doDefault(request, response);
	}

	@Override
	protected final void doPost(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	protected final void doPut(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		doGet(request, response);
	}
	
	@Override
	protected final void doDelete(
			HttpServletRequest request,
			HttpServletResponse response
			) throws ServletException, IOException {
		doGet(request, response);
	}

}