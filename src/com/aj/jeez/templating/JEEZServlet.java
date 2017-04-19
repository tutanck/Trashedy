package com.aj.jeez.templating;

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
import com.aj.jeez.annotation.core.ServletsManager;
import com.aj.jeez.annotation.exceptions.CheckoutAnnotationMisuseException;
import com.aj.jeez.exceptions.JEEZException;
import com.aj.tools.Mr;
import com.aj.tools.Stretch;
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
	protected final Params requestParams = new Params() ; 

	/**
	 * The set of outgoing parameters required and optional
	 * known by the underlying service */
	protected Params jsonOutParams = new Params();

	/**
	 * The set of test classes where to find 
	 * the test methods to execute after business */
	protected final Set<Class<?>> checkClasses = new HashSet<Class<?>>();




	public void init() throws ServletException {
		super.init();

		System.out.println("JEEZServlet/init:: "+getClass().getCanonicalName()+" : new instance requested...");

		Enumeration<String> servletInitParamsNames = getInitParameterNames();
		while(servletInitParamsNames.hasMoreElements()){ 
			String paramName = servletInitParamsNames.nextElement();

			if(ServletsManager.JZID.equals(paramName)){
				String paramValue = getInitParameter(paramName);

				JSONObject jzDriver = new JSONObject(paramValue);

				System.out.println("JEEZServlet/init::jzDriver="+jzDriver);

				this.url=jzDriver.getString("url");
				this.sC=jzDriver.getString("sC");
				this.sM=jzDriver.getString("sM");

				if(this.auth==null)
					this.auth = jzDriver.getBoolean("auth");

				try { 
					Stretch.addClassesToSet(this.checkClasses,jzDriver.getString("ckC"));
					checkouts = CheckoutsRadar.findAnnotatedServices(checkClasses);

					Stretch.inflateParams(requestParams, (JSONArray)jzDriver.get("expIN"), true);
					Stretch.inflateParams(requestParams, (JSONArray)jzDriver.get("optIN"), false);
					Stretch.inflateParams(jsonOutParams, (JSONArray)jzDriver.get("expOut"), true);
					Stretch.inflateParams(jsonOutParams, (JSONArray)jzDriver.get("optOut"), false);

				} catch (ClassNotFoundException | CheckoutAnnotationMisuseException e) 
				{throw new ServletException(e);}

				System.out.println("JEEZServlet/init::THIS : '"+toString()+"'");
			} 
		}
		System.out.println("JEEZServlet/init::"+sC+"."+sM+" initialised and now waiting for call...");
	}

	

	/**
	 * The default method that check the connectivity of the related service
	 * @param request 
	 * @throws Exception */
	protected boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			) throws Exception{
		return request.getSession(false)!=null;
	}



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

		System.out.println("JEEZServlet/beforeBusiness::"+sC+"."+sM+" --> before business...");

		response.setContentType("text/plain");

		JSONObject params = new JSONObject();
		
		Map<String,String>effectiveRequestParams=Mr.refine(request.getParameterMap());

		System.out.println("JEEZServlet/beforeBusiness::effectiveRequestParams : "+effectiveRequestParams+" formalRequestParams : "+requestParams);

		for(Param expected : requestParams.getExpecteds()) {
			JSONObject res = paramIsValid(effectiveRequestParams,expected,params,true);
			if (!res.getBoolean("valid")){
				System.out.println(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			System.out.println(" -> Valid");
			params = JR.merge(
					params,(JSONObject) res.get("supportedParams"));
		}

		for(Param optional : requestParams.getOptionals()){
			JSONObject res = paramIsValid(effectiveRequestParams,optional,params,false);
			if (!res.getBoolean("valid")){
				System.out.println(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			System.out.println(" -> Valid");
			params = JR.merge(
					params,(JSONObject) res.get("supportedParams"));
		}

		if(auth && !isAuth(request,params)){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "USER UNAUTHENTICATED");
			return null;
		}else if(!auth && isAuth(request,params)){
			response.sendError(HttpServletResponse.SC_FORBIDDEN, "USER ALREADY AUTHENTICATED");
			return null;
		}
		 
		return params;
	}


	/**
	 * Check if an incoming parameter is filled (exists and is not empty in the request)
	 * and properly typed according to epnIn and opnIn definitions
	 * @param requestParams
	 * @param typedParameterName
	 * @param supportedParams
	 * @param strict
	 * @return 
	 * @throws JEEZException */
	private final JSONObject paramIsValid(
			Map<String,String>requestParams,
			String typedParameterName,
			JSONObject supportedParams,
			boolean strict
			) throws JEEZException {
		JSONObject notValid = new JSONObject()
				.put("valid", false)
				.put("supportedParams", supportedParams); //parameter not added to supportedParams

		JSONObject noChanges= new JSONObject()
				.put("valid", true)
				.put("supportedParams", supportedParams); //parameter not added to supportedParams

		//name:string --> {[0]:name(paramName) , [1]:string(paramType)}
		String[] typedParameterTab = typedParameterName.split("\\:");
		String paramName = typedParameterTab[0].trim();
		System.out.print("    --->paramName : "+paramName);

		//availability test
		if(!requestParams.containsKey(paramName)
				|| requestParams.get(paramName).equals("")
				|| requestParams.get(paramName).equals("null")
				|| requestParams.get(paramName)==null)
			if (strict)
				return notValid;
			else
				return noChanges;

		//typing test
		if (typedParameterTab.length >= 2) {//typedef is provided in the template
			String paramType = typedParameterTab[1].trim().toLowerCase();

			if(paramType.length()==0) //in case of empty string --> consider type as not defined
				//Copy the supported parameter now typed into a restricted JSON(supportedParams) 
				//(that contains only valid and typed epn and opn)
				supportedParams.put(paramName, requestParams.get(paramName));
			else
				try {
					System.out.print(" - paramType : "+paramType);
					//Copy the supported parameter now typed into a restricted JSON(supportedParams) 
					//(that contains only valid and typed epn and opn)
					switch (paramType) {
					case "int":
						supportedParams.put(paramName, Integer.parseInt(requestParams.get(paramName)));
						break;
					case "long":
						supportedParams.put(paramName, Long.parseLong(requestParams.get(paramName)));
						break;
					case "float":
						supportedParams.put(paramName, Float.parseFloat(requestParams.get(paramName)));
						break;
					case "double":
						supportedParams.put(paramName, Double.parseDouble(requestParams.get(paramName)));
						break;
					case "boolean":
						String boolStr=requestParams.get(paramName);
						if(!boolStr.equals("true") || !boolStr.equals("false"))
							throw new IllegalArgumentException(paramName+"'s value: "+boolStr+" is not a boolean value");
						supportedParams.put(paramName, Boolean.parseBoolean(requestParams.get(paramName)));
						break;
					case "string" :
						supportedParams.put(paramName, requestParams.get(paramName));
						break;
						//Should Never occur ("parameters types have to be checked at deployment time")
					default: throw new JEEZException(
							"Unsupported type for expected parameter "+paramName+": "+paramType+". "
									+ "Supported types are : int, long, float, double, boolean and string.");
					}
				} catch (IllegalArgumentException iae) {
					iae.printStackTrace();
					return notValid;
				}
		}else  //Copy the supported parameter now typed into a restricted JSON(supportedParams) 
			//(that contains only valid and typed epn and opn)
			supportedParams.put(paramName, requestParams.get(paramName));

		return new JSONObject()
				.put("valid", true)
				.put("supportedParams", supportedParams); //updated with the valid parameter added
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

		System.out.println("JEEZServlet/afterBusiness::"+sC+"."+sM+" --> after business...");

		if(!resultIsOK(result)) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SERVICE TEMPORARILY UNAVAILABLE");
			System.out.println("result failed to satisfy some postconditions");
			return;
		}
		System.out.println("result satisfy all postconditions");
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
	protected boolean resultIsOK(//TODO tester
			Object result
			) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, InstantiationException{
		for(Map.Entry<Class<?>,Set<Method>> entry: this.checkouts.entrySet()){
			Class<?> checkClass=entry.getKey();	
			for(Method checkout : entry.getValue()){
				System.out.println("JEEZServlet/resultIsOK:: static call of : "+checkClass+"."+checkout+"("+result+")");
				Checkout chk = checkout.getAnnotation(Checkout.class);
				String chkName=chk.value().length()==0?checkClass.getCanonicalName()+"."+checkout.getName():chk.value();
				try{
						//boolean approved=(boolean)checkout.invoke(checkClass.newInstance(), new Object[]{result,this.expectedOut.toArray(new String[]{}),this.optionalOut.toArray(new String[]{})});
					//TODO if(!approved)
					if(chk.clientsafe())
						return false;
					else
						System.out.println("JEEZServlet/resultIsOK:: Checkout '"+chkName+"' failed with no consequences on the service '"+sC+"."+sM+"' 's result.");
				}catch (Exception e) {return chk.clientsafe();}
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
		System.out.println("JEEZServlet/doBusiness:: static call of : "+this.sC+"."+this.sM+"("+params+")");
		Class<?> serviceClass=Class.forName(this.sC);	
		Method m = serviceClass.getMethod(this.sM, new Class[]{JSONObject.class});
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
		}catch (Exception e){
			e.printStackTrace();
			response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "AN INTERNAL SERVER ERROR OCCURRED");
		}
	}

	
	
	public String toString(){
		String jz ="";
		jz+="service:"+this.sC+"."+this.sM;
		jz+=" - url:"+this.url;
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