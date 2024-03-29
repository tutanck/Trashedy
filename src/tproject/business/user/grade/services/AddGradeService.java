package tproject.business.user.grade.services;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;
import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.aj.jeez.regina.THINGS;

import tproject.business.user.grade.core.GradeCore;
import tproject.business.user.io.db.UserDB;
import tproject.conf.servletspolicy.Common;
import tproject.conf.servletspolicy.OnlinePostServlet;
import tproject.tools.db.DBException;
import tproject.tools.services.Response;
import tproject.tools.services.ShouldNeverOccurException;

import java.util.Date;

import org.json.JSONObject;

/**
 * @author AJoan */
public class AddGradeService extends GradeCore{
	public final static String url="/user/grade/update";

	
	@WebService(value=url,policy = OnlinePostServlet.class,
			requestParams=@Params(
					value={
							@Param(value=UserDB._grade,type=int.class,rules={"0|1|2|3|4|5"}),
							@Param(value=UserDB._nid)
					}))
	public static JSONObject addGrade(
			JSONObject params
			) throws DBException, ShouldNeverOccurException, AbsentKeyException {

		THINGS.update(
				JR.slice(params,Common._userID),
				JR.wrap(
						"$push"
						,JR.slice(params,UserDB._grade,UserDB._nid)
						.put(UserDB._gradeDate, new Date())
						)
				,userdb);

		return Response.reply();
	}
}
