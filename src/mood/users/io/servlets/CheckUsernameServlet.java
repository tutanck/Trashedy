package mood.users.io.servlets;

import java.util.Arrays;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflineGetServlet;

import org.json.JSONObject;

import mood.users.io.services.CheckUsernameService;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/check/username"})
public class CheckUsernameServlet extends OfflineGetServlet {
	private static final long serialVersionUID = 1L;
	public CheckUsernameServlet() {super();}

	@Override
	public void init() throws ServletException {
		super.init();
		super.expectedIn.addAll(Arrays.asList(new String[]{"username"}));
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response, 
			JSONObject params
			)throws Exception {
		return CheckUsernameService.checkUsername(params);
	}
}