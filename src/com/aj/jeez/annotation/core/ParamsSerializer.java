package com.aj.jeez.annotation.core;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Param;
import com.aj.jeez.annotation.exceptions.ParamTypingException;
import com.aj.jeez.exceptions.JEEZError;

public class ParamsSerializer {

	static JSONArray serialize(
			Param[]params,
			boolean serializable
			){
		JSONArray jar = new JSONArray();
	
		for(Param param : params){
			JSONObject jo = new JSONObject();
			if(serializable)
				try {
					jo.put("type", FormalParamTypeControler.typeToInt(param.type()));
				}catch (ParamTypingException e) {
					throw new JEEZError("#SNO : internal typing error");
				}
			else
				jo.put("type", param.type().getCanonicalName());
			jar.put(jo
					.put("name", param.value())
					.put("rules", param.rules()));
		}
		return jar;
	}

}
