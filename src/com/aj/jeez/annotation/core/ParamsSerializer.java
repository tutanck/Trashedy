package com.aj.jeez.annotation.core;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.exceptions.ParamTypingException;
import com.aj.jeez.exceptions.JEEZError;

public class ParamsSerializer {

	static JSONArray serialize(
			Param[]params
			){
		JSONArray jar = new JSONArray();
	
		for(Param param : params){
			JSONObject jo = new JSONObject();
				try {
					jo.put("type", FormalParamTypeControler.typeToInt(param.type()));
				}catch (ParamTypingException e) {
					throw new JEEZError("#SNO : internal typing error");
				}
			jar.put(jo
					.put("name", param.value())
					.put("rules", param.rules()));
		}
		return jar;
	}

}
