package hxh.business.admin;

import org.json.JSONObject;

import com.aj.jeez.gate.representation.annotations.Param;
import com.aj.jeez.gate.representation.annotations.Params;
import com.aj.jeez.gate.representation.annotations.WebService;

import hxh.conf.servletspolicy.OfflinePostServlet;
import hxh.tools.mailing.Email;
import hxh.tools.services.Response;
import hxh.tools.services.ServiceCodes;
import hxh.tools.services.ShouldNeverOccurException;

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