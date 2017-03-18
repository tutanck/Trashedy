package mood.users.servlets;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mood.users.services.User;
import tools.servletspolicy.OfflinePostServlet;

import org.json.JSONObject;

/**
 * @author Anagbla Joan */

@WebServlet(urlPatterns={"/user/update"})
public class UpdateProfileServlet extends OfflinePostServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.epnIn=new HashSet<>(Arrays.asList(new String[]{"username"}));}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request, 
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return User.updateProfile(params);
	}
}