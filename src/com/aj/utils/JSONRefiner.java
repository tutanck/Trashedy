package com.aj.utils;

import org.json.JSONObject;

import java.util.*;

/**
 * @author ANAGBLA Joan */
//TODO relire attentivement tt repose su ca
public class JSONRefiner {	

	/**
	 * @description 
	 * Return an JSONObject equivalent of the {map}
	 * @param map
	 * @return */
	public static JSONObject jsonify(
			Map<?,?> map
			){
		return new JSONObject(map);
	}
	
	
	public static JSONObject wrap(String key, Object value){
		return new JSONObject().put(key, value);
	}


	/**
	 * @description
	 * Return a sliced json according to the subset of {subKeys}
	 * The sliced json is a copy of the {whole} json and does not undergo any changes.
	 * 
	 * @param whole
	 * @param subKeys
	 * @return 
	 * @throws AbsentKeyException */
	public static JSONObject slice(
			JSONObject whole,
			String[]subKeys
			) throws AbsentKeyException{
		JSONObject sliced= new JSONObject();
		for(String key : new HashSet<String>(Arrays.asList(subKeys)))
			if(whole.has(key))
				sliced.put(key, whole.get(key));
			else throw new 
			AbsentKeyException("The key '"+key+"' does not exist in '"+whole+"'");
		return sliced;
	}

	
	/**
	 * @description
	 * Return a sliced json according to the subset of {subKeys}
	 * The sliced json is a copy of the {whole} json and does not undergo any changes.
	 * 
	 * @param whole
	 * @param subKeys
	 * @return */
	public static JSONObject clean(
			JSONObject whole,
			String[]subKeys
			) throws AbsentKeyException{
		JSONObject clean= new JSONObject(whole.toMap());
		for(String key : new HashSet<String>(Arrays.asList(subKeys)))
			if(whole.has(key))
				clean.remove(key);
		return clean;
	}


	/**
	 * @Description 
	 * Subdivide a json's {trunc} in two json's branches following {subKeys} keys
	 * One branch will contains all entries whose key is in {subKeys}
	 * The other branch will contains all entries in initial {trunc} except whose key is in {subKeys}
	 * @param trunk
	 * @param subKeys
	 * @return  
	 * @throws AbsentKeyException */
	public static List<JSONObject> branch(
			JSONObject trunk,
			String[]subKeys
			) throws AbsentKeyException{
		JSONObject branch0 = new JSONObject();
		JSONObject branch1 = new JSONObject(trunk.toMap());

		for(String key : new HashSet<String>(Arrays.asList(subKeys)))
			if(trunk.has(key)){
				branch0.put(key, trunk.get(key));
				branch1.remove(key);
			}
			else throw new
			AbsentKeyException("The key '"+key+"' does not exist in '"+trunk+"'");

		List<JSONObject>node = new ArrayList<>();
		node.add(0,branch0);
		node.add(1,branch1);
		return node;
	}
	
	
	/**
	 * @description
	 * Merge to JSONObject together
	 * @param branch1
	 * @param branch2
	 * @return */
	public static JSONObject merge(
			JSONObject branch1,
			JSONObject branch2
			){
		JSONObject trunk = new JSONObject(branch1.toMap());

		for(String key : branch2.keySet())
			trunk.put(key, branch2.get(key));
		 
		return trunk;
	}

	
	
	/**
	 * @Description
	 * Rename {json}'s keys by replacing them 
	 * by the associated value in the {keyMap} without
	 * changing the associated values in the {json}.
	 * No change is performed on the keys that are not in {keyMap}.
	 * 
	 * @param json
	 * @param keyMap
	 * @return  
	 * @throws AbsentKeyException */
	public static JSONObject renameJSONKeys(
			JSONObject json,
			Map<String,String> keyMap
			) throws AbsentKeyException{
		JSONObject aliasJSON = new JSONObject(json.toMap());
		for(String key : keyMap.keySet())
			if(json.has(key)){
				aliasJSON.remove(key);
				aliasJSON.put(keyMap.get(key), json.get(key));
			}
			else throw new
			AbsentKeyException("The key '"+key+"' does not exist in '"+json+"'");
		return aliasJSON;
	}
	
	
	/**
	 * @Description
	 * Rename {json}'s keys by replacing them 
	 * by the associated value in the {keyMapString} without
	 * changing the associated values in the {json}.
	 * No change is performed on the keys that are not in {keyMapString}.
	 * 
	 * @param json
	 * @param keyMapString
	 * @return
	 * @throws AbsentKeyException
	 * @throws InvalidKeyException */
	public static JSONObject renameJSONKeys(
			JSONObject json,
			String[] keyMapString
			) throws AbsentKeyException, InvalidKeyException{
		JSONObject aliasJSON = new JSONObject(json.toMap());
		for(String keyEntry : keyMapString){
			if(!keyEntry.contains("->")) 
				throw new 
				InvalidKeyException("The key entry '"+keyEntry+"' does not contains the universal seprator '->'");
			String [] entry= keyEntry.split("->");
			String oldKey = entry[0].trim();
			String newKey = entry[1].trim();
			if(json.has(oldKey)){
				aliasJSON.remove(oldKey);
				aliasJSON.put(newKey, json.get(oldKey));
			}
			else throw new
			AbsentKeyException("The key '"+keyEntry+"' does not exist in '"+json+"'");
		}
		return aliasJSON;
	}



	public static void main(String[] args) throws AbsentKeyException {
		JSONObject jo = new JSONObject()
				.put("lola","lola")
				.put("lol0","oui")
				.put("lol1",12.7)
				.put("lol2",true)
				.put("lol3",12);

		System.out.println("sliced : "+slice(jo, new String[]{"lol1","lol3"}));
		System.out.println("jo : "+jo+"\n");

		System.out.println("node : "+branch(jo,new String[]{"lol1","lol2"}));
		System.out.println("jo : "+jo+"\n");	

		Map<String, String> kmap=new HashMap<>();
		kmap.put("lol1", "newlol1");
		kmap.put("lol3", "newlol3");
		//kmap.put("lol", "newlol"); //lol don't exist in jo --> except
		System.out.println("aliasMap : "+renameJSONKeys(jo,kmap));
		System.out.println("jo : "+jo+"\n");
		
		System.out.println("clean : "+clean(jo,new String[]{"lola"}));
		System.out.println("jo : "+jo+"\n");
	}
}