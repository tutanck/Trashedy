package mood.users.profile.servlets;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OnlineGetServlet;

import org.json.JSONObject;

import mood.users.profile.service.UserProfile;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/user/profile"})
public class GetProfileServlet extends OnlineGetServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.optionalIn.addAll(Arrays.asList(new String[]{"uther"}));
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return UserProfile.getProfile(params);
	}
}
