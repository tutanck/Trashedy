package md.admin;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.annotations.Params;
import com.aj.jeez.annotation.annotations.WebService;

import tools.mailing.Email;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

/**
 * @author AJoan */
public class AlertMeService {
	public final static String url="/alertme";

	@WebService(value=url,policy = OfflinePostServlet.class,
			requestParams=@Params({ @Param(value="msg") }))
	public static JSONObject alertme(
			JSONObject params
			) throws ShouldNeverOccurException{
	
		try {
			Email.send(Email.username,"Alert me!",params.getString("msg"));
		}catch (Exception e) {return Response.issue(ServiceCodes.ALERT_NOT_SENT);}; 
		
		return Response.reply();
	}
}