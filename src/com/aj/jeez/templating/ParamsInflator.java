package com.aj.jeez.templating;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.exceptions.ParamNamingException;
import com.aj.jeez.annotation.exceptions.ParamRulingException;
import com.aj.jeez.annotation.exceptions.ParamTypingException;

public class ParamsInflator {

	public static void inflateParams(
			TemplateParams params,
			JSONArray jsonParams,
			boolean expected
			) throws ClassNotFoundException, ParamTypingException, ParamNamingException, ParamRulingException{	

		for(int i=0;i<jsonParams.length();i++){
			TemplateParam param = ParamsInflator.inflateParam(jsonParams.getJSONObject(i));
			if(!params.getExpecteds().contains(param) && !params.getOptionals().contains(param))
				if(expected)
					params.addExpected(param);
				else 
					params.addOptional(param);
		}
	}

	public static TemplateParam inflateParam(
			JSONObject jsonParam
			) throws ClassNotFoundException, ParamTypingException, ParamNamingException, ParamRulingException{	

		Set<String> rules = new HashSet<>();
		JSONArray jarRules = (JSONArray)jsonParam.get("rules");

		for(int i=0; i<jarRules.length();i++)
			rules.add(jarRules.getString(i));

		return new TemplateParam(jsonParam.getString("name"),
				Class.forName(jsonParam.getString("type")),rules);
	}

}
