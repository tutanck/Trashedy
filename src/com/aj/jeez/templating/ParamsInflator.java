package com.aj.jeez.templating;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class ParamsInflator {

	public static void inflateParams(
			TemplateParams params,
			JSONArray jsonParams,
			boolean expected
			) throws ClassNotFoundException, JSONException{	
		
		for(int i=0;i<jsonParams.length();i++){
			TemplateParam param = ParamsInflator.inflateParam(jsonParams.getJSONObject(i));
			if(expected){
				if(!params.getExpecteds().contains(param))
					params.addExpected(param);
			}else 
				if(!params.getOptionals().contains(param))
					params.addOptional(param);
		}
	}

	public static TemplateParam inflateParam(
			JSONObject jsonParam
			) throws ClassNotFoundException, JSONException{	
	
		Set<String> rules = new HashSet<>();
		JSONArray jarRules = (JSONArray)jsonParam.get("rules");
	
		for(int i=0; i<jarRules.length();i++)
			rules.add(jarRules.getString(i));
	
		return new TemplateParam(jsonParam.getString("name"),
				Class.forName(jsonParam.getString("type")),rules);
	}

}
