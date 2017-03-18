package mood.users.servlets.pp;

import tools.servletspolicy.OfflinePostServlet;
import mood.users.services.UserPlacesProfile;

import org.json.JSONObject;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet(urlPatterns={"/pp/get"})
public class GetPpServlet extends OfflinePostServlet {
	private static final long serialVersionUID = 1L;
	public GetPpServlet() {super();}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
		return UserPlacesProfile.getPp(params);
	}
}