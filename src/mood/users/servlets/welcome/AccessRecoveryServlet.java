package mood.users.servlets.welcome;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import tools.servletspolicy.OfflineGetServlet;

import mood.users.services.User;

import org.json.JSONObject;

/**
 * * @author Anagbla Joan */

@WebServlet(urlPatterns={"/account/recover"})
public class AccessRecoveryServlet extends OfflineGetServlet {
	private static final long serialVersionUID = 1L;
	public AccessRecoveryServlet() {super();}

	@Override
	public void init() throws ServletException {
		super.init();
		super.epnIn=new HashSet<>(Arrays.asList(new String[]{"email"}));}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception{
		return  User.accessRecover(params);
	}
}
