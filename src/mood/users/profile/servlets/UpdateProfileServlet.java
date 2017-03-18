package mood.users.profile.servlets;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OnlinePostServlet;

import org.json.JSONObject;

import mood.users.profile.service.UserProfile;

/**
 * @author Anagbla Joan */

@WebServlet(urlPatterns={"/user/update"})
public class UpdateProfileServlet extends OnlinePostServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.epnIn.addAll(new HashSet<>(Arrays.asList(new String[]{"username"})));
		super.opnIn=new HashSet<>(Arrays.asList(new String[]{"phone","email","lastName","firstName","birthdate"}));	
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request, 
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return UserProfile.updateProfile(params);
	}
}