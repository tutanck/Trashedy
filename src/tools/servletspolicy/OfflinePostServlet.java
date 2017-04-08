package tools.servletspolicy;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.policy.PostServlet;

/**
 * * @author Anagbla Joan */
public class OfflinePostServlet extends PostServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		return false;
	}
}