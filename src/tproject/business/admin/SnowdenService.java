package tproject.business.admin;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;

import tproject.conf.servletspolicy.OfflinePostServlet;
import tproject.tools.mailing.Email;
import tproject.tools.services.Response;
import tproject.tools.services.ServiceCodes;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan */
public class SnowdenService {
	public final static String url="/snowden";
	
	public final static String msg="msg";

	@WebService(value=url,policy = OfflinePostServlet.class,
			requestParams=@Params({ @Param(value=msg) }))
	public static JSONObject alertme(
			JSONObject params
			) throws ShouldNeverOccurException{
	
		try {
			Email.send(Email.username,"Snowden wants to talk!",params.getString(msg));
		}catch (Exception e) {return Response.issue(ServiceCodes.ALERT_NOT_SENT);} 
		
		return Response.reply();
	}
}