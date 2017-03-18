package mood.users.profile.servlets;

import tools.servletspolicy.OnlineGetServlet;

import org.json.JSONObject;

import mood.users.profile.service.UserProfile;

import java.util.Arrays;
import java.util.HashSet;

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
		super.epnIn=new HashSet<>(Arrays.asList(new String[]{"query"}));}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return UserProfile.searchUser(params);
	}
}