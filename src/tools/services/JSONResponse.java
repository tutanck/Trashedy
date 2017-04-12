package tools.services;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONResponse {

	/**
	 * @description 
	 * Return a predefined JSONObject containing
	 * the reply code {rpcode},
	 * a the status code [_KANPEKI] 
	 * eventually 
	 * 	the service's {result},
	 * 	a warning {message}
	 * @param result
	 * @param message
	 * @param replycode
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject reply(
			JSONObject result,
			String message,
			int replycode
			)throws JSONException, ShouldNeverOccurException{

		return new JSONObject()
				.put("status",ServiceCodes._KANPEKI)
				.put("rpcode",replycode)
				.put("message",message)//warning or whatever
				.put("result",result);
	}



	/**
	 * @description 
	 * Return a predefined JSONObject containing 
	 * the issue's {issuecode},
	 * and the {status} code [_ISSUE]
	 * 
	 * @param status
	 * @param result
	 * @param message
	 * @param issue
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject issue(
			int issue
			)throws JSONException, ShouldNeverOccurException{
		return new JSONObject()
				.put("status",ServiceCodes._ISSUE)
				.put("issue",issue);
	}

}
