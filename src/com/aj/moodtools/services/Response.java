package com.aj.moodtools.services;

import org.json.JSONException;
import org.json.JSONObject;

public class Response {
	
	/**
	 * STATUS CODES */
	private static int _ISSUE=-1;
	private static int _KANPEKI=0;

	/**
	 * @description 
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
			JSONObject result
			)throws JSONException, ShouldNeverOccurException{
		return new JSONObject()
				.put("status",_KANPEKI)
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
				.put("status",_ISSUE)
				.put("issue",issue);
	}

}
