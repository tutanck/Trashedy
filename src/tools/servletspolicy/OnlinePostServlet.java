package tools.servletspolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.policy.PostServlet;

import mood.users.io.db.UserSessionDB;

/**
 * * @author Anagbla Joan */
public class OnlinePostServlet extends PostServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.requireAuth=true;
		super.expectedIn.add("skey");
	}

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		return UserSessionDB.sessionExists(params);
	}
}