package mood.users.io.servlets;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflineGetServlet;

import org.json.JSONObject;

import mood.users.io.service.UserIO;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/check/email"})
public class CheckEmailServlet extends OfflineGetServlet {
	private static final long serialVersionUID = 1L;
	public CheckEmailServlet() {super();}

	@Override
	public void init() throws ServletException {
		super.init();
		super.expectedIn.addAll(Arrays.asList(new String[]{"email"}));
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response, 
			JSONObject params
			)throws Exception {
		return UserIO.checkEmail(params);
	}
}