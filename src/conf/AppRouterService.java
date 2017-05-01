package conf;

import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.WebService;
import com.aj.jeez.annotation.core.StartupListener;

/**
 * @author Joan */
public class AppRouterService {
	public final static String url="/jz/app/routes"; 
	
	@WebService(url)
	public static JSONObject getRouter(
			JSONObject params
			)  {
		return StartupListener.router;
	}
}
