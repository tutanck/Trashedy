package mood.users.servlets.pp;

import java.util.Arrays;
import java.util.HashSet;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import mood.users.services.UserPlacesProfile;
import tools.servletspolicy.OfflinePostServlet;

import org.json.JSONObject;

@WebServlet(urlPatterns={"/pp/update"})
public class UpdatePpServlet extends OfflinePostServlet {
	private static final long serialVersionUID = 1L;
	public UpdatePpServlet() {super();}

	@Override
	public void init() throws ServletException {
		super.init();
 		super.epnIn=new HashSet<>(Arrays.asList(new String[]{"places"}));}

	@Override
	public JSONObject doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {
return UserPlacesProfile.updatePp(params);
	}
}