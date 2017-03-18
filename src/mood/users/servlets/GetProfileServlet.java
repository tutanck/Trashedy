package mood.users.servlets;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OnlineGetServlet;

import mood.users.services.User;

import org.json.JSONObject;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/user/profile"})
public class GetProfileServlet extends OnlineGetServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return User.getProfile(params);
	}
}
