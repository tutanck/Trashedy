package mood.users.servlets.welcome;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflinePostServlet;
import mood.users.services.User;

import org.json.JSONObject;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/signup"})
public class RegistrationServlet extends OfflinePostServlet {
	private static final long serialVersionUID = 1L;
	public RegistrationServlet() {super();}

	@Override
	public void init() throws ServletException {
		super.init();
		super.epnIn= new HashSet<>(Arrays.asList(new String[]{"username","pass","email"}));}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response, 
			JSONObject params
			)throws Exception {
		return User.registration(params);
	}
}