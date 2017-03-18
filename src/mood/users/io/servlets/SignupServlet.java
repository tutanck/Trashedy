package mood.users.io.servlets;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflinePostServlet;

import org.json.JSONObject;

import mood.users.io.service.UserIO;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/signup"})
public class SignupServlet extends OfflinePostServlet {
	private static final long serialVersionUID = 1L;
	public SignupServlet() {super();}

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
		return UserIO.registration(params);
	}
}