package com.aj.hxh.conf.servletspolicy;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONObject;

import com.aj.hxh.business.user.io.db.SessionDB;
import com.aj.jeez.gate.core.exceptions.InconsistentParametersException;
import com.aj.jeez.gate.core.exceptions.ParamNamingException;
import com.aj.jeez.gate.core.exceptions.ParamRulingException;
import com.aj.jeez.gate.core.exceptions.ParamTypingException;
import com.aj.jeez.gate.defaults.policy.PostServlet;
import com.aj.jeez.gate.representation.templates.TemplateParam;
import com.aj.jeez.gate.representation.templates.TemplateParams;
import com.aj.jeez.tools.__;

/**
 * * @author Anagbla Joan */
public class OnlinePostServlet extends PostServlet{
	private static final long serialVersionUID = 1L;
		
	@Override
	public Boolean requireAuth(){return true;}

	@Override
	public TemplateParams requestParams(){
		try {
			TemplateParams tp =	new TemplateParams();
			tp.addExpected(new TemplateParam("skey"));
			return tp;
		} catch (ParamTypingException | ParamNamingException | ParamRulingException | InconsistentParametersException e) {
			__.explode(e);
			return null;
		}
	}

	
	
	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			)throws Exception{	
		return SessionDB.exists(params);
	}
}