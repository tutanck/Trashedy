package com.aj.jeez;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.aj.jeez.annotations.Checkout;
import com.aj.jeez.annotations.core.CheckoutsRadar;
import com.aj.jeez.annotations.exceptions.CheckoutAnnotationMisuseException;
import com.aj.jeez.exceptions.JEEZException;
import com.aj.tools.Mr;
import com.aj.tools.Stretch;
import com.aj.tools.jr.JR;


/**
 * Abstract servlet-template as a basis for all the JEEZServlets policies.
 * JEEZServlet defines some useful default methods that can help to connect the services layer
 * outside of the server to any client that uses HTTP requests to communicate with the exposed services.
 * This class is abstract and must be extended and override a least one HTTP doMethod
 * (doGet,doPost,doPut,doDelete) to be usable.
 * Its descendants can as well be used as : 
 * -a real servlet 
 * -or just as a template used by the @WebService annotation to dynamically add servlets that will follow the template with specific configuration for each of them
 * -or both : in that case each dynamically created servlet will override the default template configuration with its specific parameters specified in the annotation. 
 * * @author Anagbla Joan */
public abstract class JEEZServlet extends HttpServlet{
	private static final long serialVersionUID = 1L;


	/**
	 * The set that contains all the names of parameters
	 * JEEZ needs to initialize jeezServlets */
	private final Map<String,Object> jeezAttr = new HashMap<String,Object>();

	/**
	 * The qualified name of the class where to find 
	 * the serice's method to call */
	protected String serviceClass=null; 

	/**
	 * The qualified name of the serice's method 
	 * to be called */
	protected String serviceMethod=null;

	/**
	 * Specify if the underlying service 
	 * need to the user to be authenticated or not */
	protected boolean requireAuth = false;

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
	protected final Set<Class<?>> checkClasses = new HashSet<Class<?>>();

	private Map<Class<?>,Set<Method>> checkouts;

	//initialization block 
	{	//act as a global reference to the class attributes
		jeezAttr.put("serviceClass",this.serviceClass); 
		jeezAttr.put("serviceMethod",this.serviceMethod);
		jeezAttr.put("requireAuth",this.requireAuth); 
		jeezAttr.put("expectedIn",this.expectedIn);
		jeezAttr.put("expectedOut",this.expectedOut);
		jeezAttr.put("optionalIn",this.optionalIn);
		jeezAttr.put("optionalOut",this.optionalOut);
		jeezAttr.put("checkClasses",this.checkClasses);
	}


	/**
	 * Default initialization of the JEEZServlet attributes.
	 * -----------------------------------------------------------
	 * This method favors the dynamic template initialization
	 * (using the annotation @WebService parameters)
	 * to the static template initialization
	 * (direct setting from servlet class attributes)
	 * Thus, dynamically adding a servlet via the
	 * @WebService annotation will override default attributes
	 * set in the sevlet policy (servlet's template) class
	 * for {this} dynamically added servlet with the attributes 
	 * specified in the @WebService annotation EXECPT for the
	 * checkClasses for whom @WebService parameters are added to the set 
	 * of checkClasses already defined in the template 
	 * -----------------------------------------------------------
	 * However, if an attribute is not specified 
	 * in the @WebService annotation
	 * this attribute will not be modified in the servlet 
	 * and will keep its policy's default value.
	 * -----------------------------------------------------------
	 * Anyway, if you specify an <init-param> in the web.xml
	 * for a JEEZServlet's descendant JEEZ will not be able 
	 * to modify the corresponding JEEZServlet's attribute. 
	 * -----------------------------------------------------------
	 * This entire method is only useful in case of
	 * using the @WebService annotation 
	 * else (should and will) not be executed.
	 * It will update (set and override) JEEZServlet attributes
	 * present in the @WebService annotations*/
	@Override @SuppressWarnings("unchecked")
	public void init() throws ServletException {
		super.init();
		
		System.out.println("JEEZServlet/init::"+getClass().getCanonicalName()+" : new instance initialisation requested for service");
		
		Enumeration<String> servletInitParamsNames = getInitParameterNames();
		while(servletInitParamsNames.hasMoreElements()){ 
			String paramName = servletInitParamsNames.nextElement();
			System.out.println("JEEZ just found in ServletConfig an init parameter named '"+paramName+"'.");
			if(jeezAttr.containsKey(paramName)){
				String paramValue = getInitParameter(paramName);
				System.out.println("   --->JEEZServlet/init::");
				System.out.println("       *getInitParameter('"+paramName+"') = '"+paramValue+"'");
				System.out.println("       *bf::jeez."+paramName+" = "+jeezAttr.get(paramName));

				switch (paramName) {
				case "serviceClass":
					this.serviceClass=paramValue;
					System.out.println("       *af::jeez."+paramName+" = "+this.serviceClass);
					break;
				case "serviceMethod":
					this.serviceMethod=paramValue;
					System.out.println("       *af::jeez."+paramName+" = "+this.serviceMethod);
					break;
				case "requireAuth":
					this.requireAuth = Boolean.parseBoolean(paramValue);
					System.out.println("       *af::jeez."+paramName+" = "+this.requireAuth);
					break;	
				case "checkClasses":
					try {//TODO tester 
						Stretch.addClassesToSet((Set<Class<?>>)jeezAttr.get(paramName), paramValue);
						checkouts = CheckoutsRadar.findAnnotatedServices(checkClasses);
					} catch (ClassNotFoundException | CheckoutAnnotationMisuseException e) {						
						throw new RuntimeException(e);
					}
					System.out.println("       *af::jeez."+paramName+" = "+jeezAttr.get(paramName));
					break;	 			
				default : //No worries we are covered by the above if-clause that checks it's a jeezAttr 
					Stretch.reSet((Set<String>)jeezAttr.get(paramName), paramValue);
					System.out.println("       *af::jeez."+paramName+" = "+jeezAttr.get(paramName));
					break;		
				}
			} 
		}
		
		System.out.println("JEEZServlet/init::"+serviceClass+"."+serviceMethod+" initialised and now waiting for call...");
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
		
		System.out.println("JEEZServlet/beforeBusiness::"+serviceClass+"."+serviceMethod+" --> before business...");

		response.setContentType("text/plain");

		JSONObject params = new JSONObject();

		Map<String,String>requestParams=Mr.refine(request.getParameterMap());

		System.out.println("JEEZServlet/beforeBusiness::requestParams : "+requestParams+" - expectedIn : "+expectedIn +" - expectedOut : "+expectedOut);

		for(String expected : expectedIn) {
			JSONObject res = paramIsValid(requestParams,expected,params,true);
			if (!res.getBoolean("valid")){
				System.out.println(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			System.out.println(" -> Valid");
			params = JR.merge(
					params,(JSONObject) res.get("supportedParams"));
		}

		for(String optional : optionalIn){
			JSONObject res = paramIsValid(requestParams,optional,params,false);
			if (!res.getBoolean("valid")){
				System.out.println(" -> Not Valid");
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
			System.out.println(" -> Valid");
			params = JR.merge(
					params,(JSONObject) res.get("supportedParams"));
		}

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
		
		System.out.println("JEEZServlet/beforeBusiness::"+serviceClass+"."+serviceMethod+" --> after business...");
		
		if(!resultIsOK(result)) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SERVICE TEMPORARILY UNAVAILABLE");
			System.out.println("result failed to satisfy some postconditions");
			return;
		}
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
				String chkName=chk.name().length()==0?checkClass.getCanonicalName()+"."+checkout.getName():chk.name();
				try{
					boolean approved=(boolean)checkout.invoke(checkClass.newInstance(), new Object[]{result,this.expectedOut.toArray(new String[]{}),this.optionalOut.toArray(new String[]{})});
					if(!approved)
						if(chk.clientsafe())
							return false;
						else
							System.out.println("JEEZServlet/resultIsOK:: Checkout '"+chkName+"' failed with no consequences on the service '"+serviceClass+"."+serviceMethod+"' 's result.");
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
		System.out.println("JEEZServlet/doBusiness:: static call of : "+this.serviceClass+"."+this.serviceMethod+"("+params+")");
		Class<?> serviceClass=Class.forName(this.serviceClass);	
		Method m = serviceClass.getMethod(this.serviceMethod, new Class[]{JSONObject.class});
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

}