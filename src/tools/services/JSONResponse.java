package tools.services;

import org.json.JSONException;
import org.json.JSONObject;

public class JSONResponse {
	
	
	/**
	 * @description 
	 * Return a predefined JSONObject containing
	 * the service's {result}, 
	 * the servlet's {rpcode},
	 * and the {status} code [_WARNING],
	 * a warning {message}
	 * 
	 * @param status
	 * @param result
	 * @param message
	 * @param replycode
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject warn(
			Object result,
			String message,
			int replycode
			)throws JSONException, ShouldNeverOccurException{

		if(message==null) 
			throw new 
			ShouldNeverOccurException("In Warning responses, message part should never be null");

		return new JSONObject()
				.put("status",ServiceCodes._WARNING)
				.put("rpcode",replycode)
				.put("message",message)
				.put("result",result); //if result is null is should disappear from the org.json's response
	}


	/**
	 * @description 
	 * Return a predefined JSONObject containing
	 * the service's {result}, 
	 * the servlet {rpcode},
	 * and the status code [_KANPEKI] 
	 * 
	 * @param status
	 * @param result
	 * @param message
	 * @param replycode
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject answer(
			Object result,
			int replycode
			)throws JSONException, ShouldNeverOccurException{

		return new JSONObject()
				.put("status",ServiceCodes._KANPEKI)
				.put("rpcode",replycode)
				.put("result",result);
	}



	/**
	 * @description 
	 * Return a predefined JSONObject containing 
	 * the servlet's {rpcode},
	 * and the {status} code [_ISSUE]
	 * 
	 * @param status
	 * @param result
	 * @param message
	 * @param replycode
	 * @return 
	 * @throws ShouldNeverOccurException */
	public static JSONObject alert(
			int replycode
			)throws JSONException, ShouldNeverOccurException{

		return new JSONObject()
				.put("status",ServiceCodes._ISSUE)
				.put("rpcode",replycode);
	}

}
