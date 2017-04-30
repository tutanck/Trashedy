package tools.servletspolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.annotation.exceptions.ParamNamingException;
import com.aj.jeez.annotation.exceptions.ParamRulingException;
import com.aj.jeez.annotation.exceptions.ParamTypingException;
import com.aj.jeez.policy.GetServlet;
import com.aj.jeez.templating.TemplateParam;
import com.aj.tools.__;

import md.user.io.db.SessionDB;

/**
 * * @author Anagbla Joan */
public class OnlineGetServlet extends GetServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.auth=true;
		try {
			super.requestParams.addExpected(new TemplateParam("skey"));
		} catch (ParamTypingException | ParamNamingException | ParamRulingException e) {
			__.explode(e);
		}
		super.init();//template mode (annotations does not override templating if init is called last)
	}

	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{
		return SessionDB.exists(params);
	}
}