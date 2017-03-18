package mood.users.places.servlets;

import tools.servletspolicy.OfflinePostServlet;

import org.json.JSONObject;

import mood.users.places.service.UserPlaces;

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
		return UserPlaces.getPp(params);
	}
}