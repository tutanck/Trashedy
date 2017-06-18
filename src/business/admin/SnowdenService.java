package business.admin;

import org.json.JSONObject;

import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.annotations.WebService;

import tools.mailing.Email;
import tools.services.Response;
import tools.services.ServiceCodes;
import tools.services.ShouldNeverOccurException;
import tools.servletspolicy.OfflinePostServlet;

/**
 * @author AJoan */
public class SnowdenService {
	public final static String url="/snowden";

	@WebService(value=url,policy = OfflinePostServlet.class,
			requestParams=@Params({ @Param(value="msg") }))
	public static JSONObject alertme(
			JSONObject params
			) throws ShouldNeverOccurException{
	
		try {
			Email.send(Email.username,"Snowden wants to talk!",params.getString("msg"));
		}catch (Exception e) {return Response.issue(ServiceCodes.ALERT_NOT_SENT);}; 
		
		return Response.reply();
	}
}