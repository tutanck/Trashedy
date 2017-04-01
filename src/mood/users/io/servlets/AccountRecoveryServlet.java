package mood.users.io.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflineGetServlet;

import org.json.JSONObject;

import mood.users.io.services.AccountRecoveryService;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/account/recover"})
public class AccountRecoveryServlet extends OfflineGetServlet {
	private static final long serialVersionUID = 1L;
	public AccountRecoveryServlet() {super();}

	@Override
	public void init() throws ServletException {
		super.init();
		super.expectedIn.add("email");
	}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception{
		return  AccountRecoveryService.accessRecovery(params);
	}
}
