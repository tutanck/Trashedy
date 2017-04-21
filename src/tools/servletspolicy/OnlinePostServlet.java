package tools.servletspolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.annotation.exceptions.ParamNamingException;
import com.aj.jeez.annotation.exceptions.ParamRulingException;
import com.aj.jeez.annotation.exceptions.ParamTypingException;
import com.aj.jeez.policy.PostServlet;
import com.aj.jeez.templating.TemplateParam;
import com.aj.tools.__;

import mood.user.io.db.UserSessionDB;

/**
 * * @author Anagbla Joan */
public class OnlinePostServlet extends PostServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.init();
		super.auth=true;
		try {
			super.requestParams.addExpected(new TemplateParam("skey"));
		} catch (ParamTypingException | ParamNamingException | ParamRulingException e) {
			__.explode(e);
		}
	}

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		return UserSessionDB.sessionExists(params);
	}
}