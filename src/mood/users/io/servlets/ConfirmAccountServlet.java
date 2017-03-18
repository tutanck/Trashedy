package mood.users.io.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflinePostServlet;

import org.json.JSONObject;

import mood.users.io.service.UserIO;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/account/confirm"})
public class ConfirmAccountServlet extends OfflinePostServlet {
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.expectedIn.add("ckey");
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return UserIO.confirmUser(params);
	}
}