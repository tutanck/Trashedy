package com.aj.jeez.core;

import com.aj.jeez.core.exceptions.InconsistentParametersException;
import com.aj.jeez.core.exceptions.ParamNamingException;
import com.aj.jeez.core.exceptions.ParamRulingException;
import com.aj.jeez.core.exceptions.ParamTypingException;
import com.aj.jeez.representation.annotations.Param;
import com.aj.jeez.representation.annotations.Params;
import com.aj.jeez.representation.templates.TemplateParam;
import com.aj.jeez.representation.templates.TemplateParams;

public class ParamsTranslator {

	public static TemplateParams translate(
			Params params			
			) throws ParamTypingException, ParamNamingException, ParamRulingException, InconsistentParametersException{	
		return translate(new TemplateParams(), params);
	}

	public static TemplateParams translate(
			TemplateParams base,
			Params params
			) throws ParamTypingException, ParamNamingException, ParamRulingException, InconsistentParametersException{

		Param [] expected=params.value();
		Param [] optional=params.optionals();

		for(Param p : expected)
			base.addExpected(translate(p));
				
		for(Param p : optional)
			base.addOptional(translate(p));

		return base;
	}

	public static TemplateParam translate(
			Param param
			) throws ParamTypingException, ParamNamingException, ParamRulingException{	
		return new TemplateParam(param.value(),param.type(),param.rules());
	}

}