package tools.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	
	/**
	 * STATUS CODES */
	private static int _ISSUE=-1;
	private static int _KANPEKI=0;

	/** 
	 * Return a predefined JSONObject containing
	 * the reply code {rpcode},
	 * a the status code [_KANPEKI] 
	 * eventually 
	 * 	- a service's {result},
	 * 	- a warning {message}
	 * @param result
	 * @param message
	 * @param replycode
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject reply(
			Object result
			)throws JSONException, ShouldNeverOccurException{
		return new JSONObject()
				.put("status",_KANPEKI)
				.put("result",result);
	}

	
	public static JSONObject reply(
			)throws JSONException, ShouldNeverOccurException{
		return new JSONObject()
				.put("status",_KANPEKI);
	}



	/**
	 * Return a predefined JSONObject containing 
	 * the issue's {issuecode},
	 * and the {status} code [_ISSUE]
	 * 
	 * @param status
	 * @param result
	 * @param message
	 * @param issueCode
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject issue(
			int issueCode
			)throws JSONException, ShouldNeverOccurException{
		return new JSONObject()
				.put("status",_ISSUE)
				.put("issue",issueCode);
	}

}
