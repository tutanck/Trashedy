package tools.servletspolicy;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.jeez.core.exceptions.InconsistentParametersException;
import com.aj.jeez.core.exceptions.ParamNamingException;
import com.aj.jeez.core.exceptions.ParamRulingException;
import com.aj.jeez.core.exceptions.ParamTypingException;
import com.aj.jeez.defaults.policy.PostServlet;
import com.aj.jeez.representation.templates.TemplateParam;
import com.aj.tools.__;

import business.user.io.db.SessionDB;

/**
 * * @author Anagbla Joan */
public class OnlinePostServlet extends PostServlet{
	private static final long serialVersionUID = 1L;

	@Override
	public void init() throws ServletException {
		super.requireAuth=true;
		try {
			super.requestParams.addExpected(new TemplateParam("skey"));
		} catch (ParamTypingException | ParamNamingException | ParamRulingException | InconsistentParametersException e) {
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