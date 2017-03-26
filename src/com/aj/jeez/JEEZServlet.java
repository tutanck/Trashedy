package com.aj.jeez;

import java.io.IOException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONObject;

import com.aj.utils.JSONRefiner;
import com.aj.utils.MapRefiner;

/*			TODO	##trouver un moyen pour les pb cause par le send de l'http error si cette methode est redefinie
par l'user idem pour beforeBusiness  : au pire les passer en final */


/**
 * * @author Anagbla Joan */
public abstract class JEEZServlet
extends HttpServlet
implements IJEEZServlet{
	private static final long serialVersionUID = 1L;

	/**
	 * The set of incoming parameters names required 
	 * for the underlying service to work properly */
	protected Set<String> expectedIn=new HashSet<String>(); //Incoming expected parameters names

	/**
	 * The set of outgoing parameters names required 
	 * for the client to work properly */
	protected Set<String> expectedOut=new HashSet<String>(); //Outgoing expected parameters names

	/**
	 * The set of incoming additional parameters names  
	 *  taken into account by the underlying service*/
	protected Set<String> optionalIn=new HashSet<String>(); //Incoming optional parameters names

	//N'est pas tres important cote server mais pour generer le client , c'est indispensable de savoir l'integalite des noms de params qu'ue servlet peut retourner (pour generer le reviver)
	/**
	 * The set of outgoing additional parameters names  
	 *  taken into account by the underlying service*/
	protected Set<String> optionalOut=new HashSet<String>(); //Outgoing optional parameters names

	protected boolean requireAuth =false;
	

	/**
	 * @description
	 * -Set the response's content type to 'text/plain'
	 * -Performs some inspection on incoming parameters 
	 * and make sure they fit with the related service requirements/preconditions.
	 * -Send an HTTP error in case of some service's constraint violation.
	 *  
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception */
	protected JSONObject beforeBusiness(
			HttpServletRequest request,
			HttpServletResponse response
			)throws Exception {
		
		response.setContentType("text/plain");

		JSONObject params = new JSONObject();
		
		Map<String,String>requestParams=MapRefiner.refine(request.getParameterMap());

		for(String expected : expectedIn) {
			System.out.println("requestParams : "+requestParams+" - expected : "+expected);
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
				response.sendError(HttpServletResponse.SC_BAD_REQUEST, "URL MISUSED");
				return null;
			}
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
	 * @description
	 * Check if the result in the server response is sufficient and well formed (
	 * 	contains all needed keys each of the right type (in the json result) 
	 *  to considerate that the result match the service's postconditions.
	 *  )  
	 * @param request
	 * @param response
	 * @param result
	 * @param debug
	 * @throws IOException */
	protected void afterBusiness(
			HttpServletRequest request, //just a precaution (useless for now)
			HttpServletResponse response,
			JSONObject result
			)throws Exception {

		if(!resultWellFormed(result)) {
			response.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "SERVICE CURRENTLY UNAVAILABLE");
			System.out.println("{result} should at least contain all keys in {epnOut}");
			return;
		}
		response.getWriter().print(result);
	}	
	

	/**
	 * @description
	 * Default method that check the connectivity of the related service
	 * @param request 
	 * @throws Exception */
	@Override
	public boolean isAuth(
			HttpServletRequest request,
			JSONObject params
			) throws Exception{
		return request.getSession(false)==null;
	}

	

	/**
	 * @description
	 * Check if an incoming parameter is filled (exists and is not empty in the request)
	 * and properly typed according to epnIn and opnIn definitions
	 * @param requestParams
	 * @param typedParameterNameString
	 * @param supportedParams
	 * @param strict
	 * @return */
	//TODO check if it is necessary to check for null or undefined or other
	private JSONObject paramIsValid(
			Map<String,String>requestParams,
			String typedParameterNameString,
			JSONObject supportedParams,
			boolean strict
			) {
		JSONObject notValid = new JSONObject()
				.put("valid", false)
				.put("supportedParams", supportedParams); //no parameter added

		JSONObject noChanges= new JSONObject()
				.put("valid", true)
				.put("supportedParams", supportedParams); //no parameter added

		//name#string --> {[0]:name(paramName) , [1]:string(paramType)}
		String[] typedParameterNameTab = typedParameterNameString.split("#");
		String paramName = typedParameterNameTab[0];
		System.out.print(" paramName : "+paramName);
		//availability test
		if(!requestParams.containsKey(paramName)
				|| requestParams.get(paramName).equals(""))
			if (strict)
				return notValid;
			else
				return noChanges;

		//typing test
		if (typedParameterNameTab.length >= 2) {//typedef is provided in the template
			String paramType = typedParameterNameTab[1].trim().toLowerCase();
			System.out.print(" - paramType : "+paramType);
			try {
				//Copy the supported parameter now typed into a restricted json (contains only typed epn and opn)
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
					supportedParams.put(paramName, Boolean.parseBoolean(requestParams.get(paramName)));
					break;

				default:
					supportedParams.put(paramName, requestParams.get(paramName));
					break;
				}
			} catch (IllegalArgumentException iae) {
				return notValid;
			}
		}else //Copy the supported parameter as string into a restricted json (contains only tped epn and opn)
			supportedParams.put(paramName, requestParams.get(paramName));
		
		return new JSONObject()
				.put("valid", true)
				.put("supportedParams", supportedParams); //updated with the valid parameter added
	}
	
	
	/**
	 * @description
	 * Check if the result contains all epnOut's key 
	 * and the corresponding values are properly typed
	 *  
	 * @param result
	 * @return */
	private boolean resultWellFormed(
			JSONObject result
			){
		boolean resultWellFormed=true;
		for(String expected : expectedOut)
			if(!result.has(expected)){
				resultWellFormed=false;
				break;
			}

		return resultWellFormed;
	}

}