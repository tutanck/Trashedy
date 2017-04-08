package com.aj.jeez;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.aj.jeez.exceptions.JEEZException;
import com.aj.utils.JSONRefiner;
import com.aj.utils.MapRefiner;
import com.aj.utils.Utils;


/**
 * * @author Anagbla Joan */
public abstract class JEEZServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;


	/**
	 * The set that contains all the names of parameters
	 * JEEZ needs to initialize jeezServlets */
	protected final Set<String> jeezParamNames = new HashSet<String>();
	{
		jeezParamNames.add("serviceClassName");
		jeezParamNames.add("serviceMethodName");
		jeezParamNames.add("requireAuth");
		jeezParamNames.add("expectedIn");
		jeezParamNames.add("expectedOut");
		jeezParamNames.add("optionalIn");
		jeezParamNames.add("optionalOut");
		jeezParamNames.add("testClasses");
	}


	/**
	 * The qualified name of the class where to find 
	 * the serice's method to call */
	protected String serviceClassName=null; 

	/**
	 * The qualified name of the serice's method 
	 * to be called */
	protected String serviceMethodName=null;

	/**
	 * Specify if the underlying service 
	 * need to the user to be authenticated or not */
	protected Boolean requireAuth = null;

	/**
	 * The set of incoming parameters names required 
	 * for the underlying service to work properly */
	protected final Set<String> expectedIn=new HashSet<String>();

	/**
	 * The set of outgoing parameters names required 
	 * for the client to work properly */
	protected final Set<String> expectedOut=new HashSet<String>();

	/**
	 * The set of incoming additional parameters names  
	 *  taken into account by the underlying service */
	protected final Set<String> optionalIn=new HashSet<String>(); 

	//N'est pas tres important cote server mais pour generer le client , c'est indispensable de savoir l'integralite des noms de params qu'ue servlet peut retourner (pour generer le reviver)
	/**
	 * The set of outgoing additional parameters names  
	 *  taken into account by the client */
	protected final Set<String> optionalOut=new HashSet<String>();

	/**
	 * The set of test classes where to find 
	 * the test methods to execute after business */
	protected final Set<Class<?>> testClasses = new HashSet<Class<?>>() ;



	/**
	 * The default initialization of the JEEZServlet.
	 * This method favors the static template initialization
	 * to the dynamic template initialization 
	 * (using the annotation @WebService) */
	@Override
	public void init() throws ServletException {
		super.init();

		for(String paramName : jeezParamNames){
			String paramValue = getInitParameter(paramName);
			System.out.println("JEEZServlet/init::"
					+" getInitParameter("+paramName+") = "+paramValue
					+" - jeez."+paramName+" = ");

			switch (paramName) {
			case "serviceClassName":
				if(this.serviceClassName!=null)break;
				this.serviceClassName=paramValue;
				System.out.println("jeez.serviceClassName : "+this.serviceClassName);
				break;
			case "serviceMethodName":
				if(this.serviceMethodName!=null)break;
				this.serviceMethodName=paramValue;
				System.out.println("jeez.serviceMethodName : "+this.serviceMethodName);
				break;
			case "requireAuth":
				if(this.requireAuth!=null)break;
				this.requireAuth = Boolean.parseBoolean(paramValue);
				System.out.println("jeez.requireAuth : "+this.requireAuth);
				break;	
			case "expectedIn":
				if(!this.expectedIn.isEmpty())break;
				this.expectedIn.addAll(Arrays.asList(Utils.split(paramValue)));
				System.out.println("jeez.expectedIn : "+this.expectedIn);
				break;
			case "expectedOut":
				this.expectedOut.addAll(Arrays.asList(Utils.split(paramValue)));
				System.out.println("jeez.expectedOut : "+this.expectedOut);
				break;
			case "optionalIn":
				this.optionalIn.addAll(Arrays.asList(Utils.split(paramValue)));
				System.out.println("jeez.optionalIn : "+this.optionalIn);
				break;
			case "optionalOut":
				this.optionalOut.addAll(Arrays.asList(Utils.split(paramValue)));
				System.out.println("jeez.optionalOut : "+this.optionalOut);
				break;
			case "testClasses":
				/*try {
				for(String classQN : Utils.split(testClassesParam))
					this.testClasses.add(Class.forName(classQN));
			} catch (ClassNotFoundException e) {throw new RuntimeException(e);}
				 */
				break;			 
			default:; 
			}
		}
	}



	/**
	 * The default method that check the connectivity of the related service
	 * @param request 
	 * @throws Exception */
	protected boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			) throws Exception{
		return request.getSession(false)==null;
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

		response.setContentType("text/plain");

		JSONObject params = new JSONObject();

		Map<String,String>requestParams=MapRefiner.refine(request.getParameterMap());

		System.out.println("JEEZServlet/beforeBusiness:: requestParams : "+requestParams+" - expectedIn : "+expectedIn +" - expectedOut : "+expectedOut);

		for(String expected : expectedIn) {
			JSONObject res = paramIsValid(requestParams,expected,params,true);
			if (!res.getBoolean("valid")){
				System.out.print(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			System.out.print(" -> Valid");
			params = JSONRefiner.merge(
					params,(JSONObject) res.get("supportedParams"));
		}
		System.out.println();

		for(String optional : optionalIn){
			JSONObject res = paramIsValid(requestParams,optional,params,false);
			if (!res.getBoolean("valid")){
				System.out.print(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			System.out.print(" -> Valid");
			params = JSONRefiner.merge(
					params,(JSONObject) res.get("supportedParams"));
		}
		System.out.println();

		if(requireAuth && !isAuth(request,params)){
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "USER UNAUTHENTICATED");
			return null;
		}else if(!requireAuth && isAuth(request,params)){
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
		String[] typedParameterNameTab = typedParameterName.split("\\:");
		String paramName = typedParameterNameTab[0].trim();
		System.out.print(" paramName : "+paramName);

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
		if (typedParameterNameTab.length >= 2) {//typedef is provided in the template
			String paramType = typedParameterNameTab[1].trim().toLowerCase();

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

		if(!resultIsOK(result)) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SERVICE TEMPORARILY UNAVAILABLE");
			System.out.println("result failed to satisfy some postconditions");
			return;
		}
		response.getWriter().print(result);
	}	



	/** TODO later see what to do 
	 * Check if the result contains all epnOut's key 
	 * and the corresponding values are properly typed
	 * @param result
	 * @return */
	protected boolean resultIsOK(
			Object result
			){
		boolean isOK=true;
		//TODO execute tests
		/*for(String expected : expectedOut)
			if(!result.has(expected)){
				isOK=false;
				break;
			}*/

		return isOK;
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
		System.out.println("JEEZServlet/doBusiness:: static call of "+this.serviceClassName+"."+this.serviceMethodName+"({...})");
		Class<?> serviceClass=Class.forName(this.serviceClassName);	
		Method m = serviceClass.getMethod(this.serviceMethodName, new Class[]{JSONObject.class});
		return m.invoke(serviceClass.newInstance(), new Object[]{params});
	}

}