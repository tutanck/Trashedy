package com.aj.jeez;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import com.aj.jeez.annotation.annotations.Checkout;
import com.aj.jeez.annotation.core.CheckoutsRadar;
import com.aj.jeez.annotation.core.FormalParamTypeControler;
import com.aj.jeez.annotation.core.ServletsManager;
import com.aj.jeez.annotation.exceptions.CheckoutAnnotationMisuseException;
import com.aj.jeez.annotation.exceptions.ParamNamingException;
import com.aj.jeez.annotation.exceptions.ParamRulingException;
import com.aj.jeez.annotation.exceptions.ParamTypingException;
import com.aj.jeez.checks.CheckExpectedOut;
import com.aj.jeez.exceptions.JEEZError;
import com.aj.jeez.templating.EffectiveParamTyper;
import com.aj.jeez.templating.ParamsInflator;
import com.aj.jeez.templating.TemplateParam;
import com.aj.jeez.templating.TemplateParams;
import com.aj.tools.MapRefiner;
import com.aj.tools.Stretch;
import com.aj.tools.__;
import com.aj.tools.jr.JR;


/**
 * @author Anagbla Joan */
public abstract class JEEZServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;

	/*PRIVATE ATTRIBUTES*/

	/**
	 * The url pattern of the servlet */
	private String url;

	/**
	 * HTTP method
	 */
	private String HTTPMethod=null;

	/**
	 * The qualified name of the class where to find 
	 * the serice's method to call */
	private String sC; 

	/**
	 * The qualified name of the serice's method 
	 * to be called */
	private String sM;

	/**
	 * The servlet checkouts by checkClass */
	private Map<Class<?>,Set<Method>> checkouts;



	/*PROTECTED ATTRIBUTES*/

	/**
	 * Specify if the underlying service 
	 * need the user to be authenticated or not */
	protected Boolean auth = null;

	/**
	 * The set of incoming parameters required and optional
	 * known by the underlying service  */
	protected final TemplateParams requestParams = new TemplateParams() ; 

	/**
	 * The set of outgoing parameters required and optional
	 * known by the underlying service */
	protected TemplateParams jsonOutParams = new TemplateParams();

	/**
	 * The set of test classes where to find 
	 * the test methods to execute after business */
	protected final Set<Class<?>> checkClasses = new HashSet<Class<?>>();




	public void init() throws ServletException {
		super.init();

		__.outln("JEEZServlet/init:: "+getClass().getCanonicalName()+" : new instance requested...");

		Enumeration<String> servletInitParamsNames = getInitParameterNames();
		while(servletInitParamsNames.hasMoreElements()){ 
			String paramName = servletInitParamsNames.nextElement();

			if(ServletsManager.JZID.equals(paramName)){
				String paramValue = getInitParameter(paramName);

				JSONObject jzsDriver = new JSONObject(paramValue);

				__.outln("JEEZServlet/init::jzsDriver="+jzsDriver);

				this.url=jzsDriver.getString("url");
				this.HTTPMethod=jzsDriver.getString("httpm");
				this.sC=jzsDriver.getString("sc");
				this.sM=jzsDriver.getString("sm");

				if(this.auth==null)
					this.auth = jzsDriver.getBoolean("auth");

				try { //Order matters : expected first optional then to favor expected parameter in case of collision exp-opt
					ParamsInflator.inflateParams(requestParams, (JSONArray)jzsDriver.get("expin"), true);
					ParamsInflator.inflateParams(requestParams, (JSONArray)jzsDriver.get("optin"), false);
					ParamsInflator.inflateParams(jsonOutParams, (JSONArray)jzsDriver.get("expout"), true);
					ParamsInflator.inflateParams(jsonOutParams, (JSONArray)jzsDriver.get("optout"), false);

					if(!jsonOutParams.expectedsEmpty() || !jsonOutParams.optionalsEmpty())
						checkClasses.add(CheckExpectedOut.class);

					Stretch.addClassesToSet(this.checkClasses,jzsDriver.getString("ckc"));
					checkouts = CheckoutsRadar.findAnnotatedServices(checkClasses);

				} catch (ClassNotFoundException | CheckoutAnnotationMisuseException  | ParamTypingException | ParamNamingException | ParamRulingException e) 
				{throw new ServletException(e);}

				__.outln("JEEZServlet/init::THIS : '"+toString()+"'");
			} 
		}
		__.outln("JEEZServlet/init::"+sC+"."+sM+" initialised and now waiting for call...");
	}



	/**
	 * The default method that check the connectivity of the related service
	 * default implementation returns always false
	 * @param request 
	 * @throws Exception */
	protected boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			) throws Exception{	return false; }



	/**
	 * -Set the response's content type to 'text/plain'
	 * -Performs some inspection on incoming parameters 
	 * and make sure they fit with the underlying service requirements/preconditions.
	 * -Send an HTTP error in case of some service's constraint violation.
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception */
	protected final JSONObject beforeBusiness(
			HttpServletRequest request,
			HttpServletResponse response
			)throws Exception {

		__.outln("JEEZServlet/beforeBusiness::"+sC+"."+sM+" --> before business...");

		response.setContentType("text/plain");

		JSONObject validParams = new JSONObject();

		Map<String,String>effectiveRequestParams=MapRefiner.refine(request.getParameterMap());

		__.outln("JEEZServlet/beforeBusiness::effectiveRequestParams : "+effectiveRequestParams+" - formalRequestParams : "+requestParams);

		for(TemplateParam expected : requestParams.getExpecteds()) {
			JSONObject res = paramIsValid(effectiveRequestParams,expected,validParams,true);
			if (!res.getBoolean("valid")){
				__.outln(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			__.outln(" -> Valid");
			validParams = JR.merge(validParams,(JSONObject) res.get("validParams"));
		}

		for(TemplateParam optional : requestParams.getOptionals()){
			JSONObject res = paramIsValid(effectiveRequestParams,optional,validParams,false);
			if (!res.getBoolean("valid")){
				__.outln(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			__.outln(" -> Valid");
			validParams = JR.merge(validParams,(JSONObject) res.get("validParams"));
		}

		__.outln("JEEZServlet/beforeBusiness::Finally ValidParams:"+validParams);

		if(auth && !isAuth(request,validParams)){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "USER UNAUTHENTICATED");
			return null;
		}else if(!auth && isAuth(request,validParams)){
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "USER ALREADY AUTHENTICATED");
			return null;
		}

		return validParams;
	}


	/**
	 * Check if an incoming parameter is filled (exists and is not empty in the request)
	 * and properly typed according to epnIn and opnIn definitions
	 * @param effectiveParams
	 * @param formalParam
	 * @param validParams
	 * @param strict
	 * @return */
	private final JSONObject paramIsValid(
			Map<String,String>effectiveParams,
			TemplateParam formalParam,
			JSONObject validParams,
			boolean strict
			){
		//Must be at the beginning to be unchanged
		JSONObject invalid = new JSONObject().put("valid", false).put("validParams", validParams); 
		JSONObject unchanged = new JSONObject().put("valid", true).put("validParams", validParams);

		String paramName = formalParam.getName();
		Class<?> paramType = formalParam.getType();
		Set<String> paramRules = formalParam.getRules();

		//Debug
		__.out("    --->paramName:"+paramName);
		__.out(" && paramType:"+paramType);
		__.out(" = "+effectiveParams.get(paramName)+" ");

		//availability test
		if( effectiveParams.containsKey(paramName) && 
				effectiveParams.get(paramName)!=null &&
				!effectiveParams.get(paramName).equals("") && 
				!effectiveParams.get(paramName).equals("null") )
			if(__.civilized(effectiveParams.get(paramName), paramRules)) //civilization test
				try {//typing test
					if( EffectiveParamTyper.valid(
							paramName, 
							FormalParamTypeControler.typeToInt(paramType),
							effectiveParams.get(paramName), validParams) )	
						//parameter added to validParams

						return new JSONObject() //updated with the valid parameter in addition
								.put("valid", true)
								.put("validParams", validParams); 

				}catch (ParamTypingException e) {
					throw new JEEZError("#SNO : internal typing error");
				}
		return strict? invalid:unchanged; 
	}

	/**
	 * Check if the result in the server response is sufficient and well formed (
	 * 	contains all needed keys each of the right type (in the JSON result) 
	 *  to considerate that the result match the service's postconditions.
	 *  )  
	 * @param request
	 * @param response
	 * @param result
	 * @param debug
	 * @throws Exception */
	protected final void afterBusiness(
			HttpServletRequest request, //just a precaution (useless for now)
			HttpServletResponse response,
			Object result
			)throws Exception {

		__.outln("JEEZServlet/afterBusiness::"+sC+"."+sM+" --> after business...");

		if(!resultIsOK(result)) {
			__.outln("result failed to satisfy at least one clientsafe checkouts");
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SERVICE TEMPORARILY UNAVAILABLE");
			return;
		}
		__.outln("result satisfied all clientsafe checkouts");
		response.getWriter().print(result);
	}	



	/** 
	 * Check if the result contains all epnOut's key 
	 * and the corresponding values are properly typed
	 * @param result
	 * @return 
	 * @throws InstantiationException 
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException */
	protected boolean resultIsOK(
			Object result
			) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{

		for(Map.Entry<Class<?>,Set<Method>> entry: this.checkouts.entrySet()){
			Class<?> checkClass=entry.getKey();	

			for(Method checkout : entry.getValue()){
				String chkName=checkClass+"."+checkout;
				__.outln("JEEZServlet/resultIsOK:: static call of : "+chkName+"("+result+")");
				Checkout chk = checkout.getAnnotation(Checkout.class);

				try{
					if(!(boolean)checkout.invoke(checkClass.newInstance(), new Object[]{result,this.jsonOutParams})){
						__.outln("result failed to satisfy checkout '"+chkName+"'");
						return !chk.value();							
					}
				}catch (Throwable t) {t.printStackTrace();return !chk.value();}
			}
		}
		return true;
	}



	/**
	 * Default doBusiness : invoke the {{serviceMethodName}} from the {{serviceClassName}}
	 * @param request
	 * @param response
	 * @param params
	 * @return
	 * @throws Exception */
	protected Object doBusiness(
			HttpServletRequest request,
			HttpServletResponse response,
			JSONObject params
			)throws Exception {			
		Class<?> serviceClass=Class.forName(this.sC);	
		Method m = serviceClass.getMethod(this.sM, new Class[]{JSONObject.class});
		__.outln("JEEZServlet/doBusiness:: static call of : "+this.sC+"."+m+"("+params+")");
		return m.invoke(serviceClass.newInstance(), new Object[]{params});
	}	 


	/**
	 * Default common operations for all doMethods as basis : 
	 * beginning by beforeBusiness operation,
	 * if all is right :
	 *  then executes the doBusiness operation
	 *  and finally executes tests through the afterBusiness operation
	 * @param request
	 * @param response
	 * @throws ServletException
	 * @throws IOException */
	protected final void doDefault(
			HttpServletRequest request,
			HttpServletResponse response
			) throws IOException  {
		try{
			JSONObject params = beforeBusiness(request,response);
			if(params!=null)
				afterBusiness(
						request,response,
						doBusiness(request,response,params)
						);
		}catch (Throwable t){
			t.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AN INTERNAL SERVER ERROR OCCURRED");
		}
	}



	public String toString(){
		String jz ="";
		jz+="service:"+this.sC+"."+this.sM;
		jz+=" - url:"+this.url;
		jz+=" - method:"+this.HTTPMethod;
		jz+=" - auth:"+this.auth;	
		List<String> ckList= new ArrayList<>();
		for(Map.Entry<Class<?>,Set<Method>> entry : checkouts.entrySet())
			for(Method m : entry.getValue())
				ckList.add(entry.getKey().getCanonicalName()+"."+m.getName());		
		jz+=" - checkouts:"+ckList;
		jz+="\n- reqestParams:{"+requestParams+"}";
		jz+="\n- jsonOutParams:{"+jsonOutParams+"}";
		return jz;
	}

	/** 
	 * @return The url of the servlet exposed to public */
	public String getUrl() {return url;}
}