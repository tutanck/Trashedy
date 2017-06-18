package conf;

import org.json.JSONObject;

import com.aj.jeez.core.StartupListener;
import com.aj.jeez.representation.annotations.WebService;

/**
 * @author Joan */
public class AppRouterService {
	public final static String url="/jz/app/routes"; 
	
	@WebService(url)
	public static JSONObject getRouter(
			JSONObject params
			)  {
		return StartupListener.getRouter();
	}
}
