package com.aj.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.json.JSONException;
import org.json.JSONObject;

import com.aj.tools.jr.AbsentKeyException;
import com.aj.tools.jr.InvalidKeyException;
import com.aj.tools.jr.JR;

public class PropIO {

	private String fileName;
	private File file;

	public PropIO(
			String fileName
			) throws IOException {
		this.fileName=fileName;
		file=new File(this.fileName);
		if(!file.exists())
			file.createNewFile();
		System.out.println("PropertiesIO/PropertiesIO :: File created at "+file.getAbsolutePath());
	}


	/**
	 * Primitive : write the property file
	 * @param properties
	 * @return
	 * @throws IOException */
	public JSONObject out(
			JSONObject properties
			) throws IOException {
		OutputStream output = null;
		Properties prop = new Properties();

		try {
			output = new FileOutputStream(fileName);
			prop.putAll(properties.toMap());
			prop.store(output, null);
			return properties;
		} finally {
			if (output != null) 
				try { output.close(); }
			catch (IOException e) {e.printStackTrace();}
		}
	}

	
	
	
	/**
	 * Primitive : Read the property file
	 * @return
	 * @throws IOException */
	public JSONObject in(
			) throws IOException {
		Properties prop = new Properties();
		InputStream input = null;

		try {
			input = new FileInputStream(fileName);
			prop.load(input);
			return new JSONObject(prop);
		} finally {
			if (input != null) 
				try {input.close();}
			catch (IOException e) {e.printStackTrace();}
		}
	}
	
	


	/**
	 * 
	 * @param properties
	 * @return
	 * @throws IOException */
	public JSONObject put(
			JSONObject properties
			) throws IOException {
		return out(JR.merge(in(),properties));
	}



	/**
	 * 
	 * @param keyValString
	 * @return
	 * @throws IOException
	 * @throws InvalidKeyException */
	public JSONObject put(
			String...keyValString
			) throws IOException, InvalidKeyException {

		JSONObject jo = new JSONObject();
		for(String keyEntry : keyValString){
			if(!keyEntry.contains("->")) 
				throw new 
				InvalidKeyException("The key entry '"+keyEntry+"' does not contains the universal seprator '->'");
			String [] entry= keyEntry.split("->");
			jo.put(entry[0].trim(),entry[1].trim());
		}
		return put(jo);
	}


	/**
	 * 
	 * @param propertyNames
	 * @return
	 * @throws IOException
	 * @throws AbsentKeyException */
	public JSONObject remove(
			String... propertyNames
			) throws IOException, AbsentKeyException {
		return out(JR.clean(in(), propertyNames));
	}


	/**
	 * 
	 * @return
	 * @throws IOException */
	public JSONObject clear(
			) throws IOException {
		return out(new JSONObject());
	}


	
	/**
	 * Return all properties identified by their {propertyNames}
	 * @param propertyNames
	 * @return
	 * @throws IOException
	 * @throws AbsentKeyException */
	public JSONObject getEntry(
			String... propertyNames
			) throws IOException, AbsentKeyException {
		JSONObject prop=in();
		return JR.slice(prop, propertyNames);
	}
	
	public String get(
			String propertyName
			) throws IOException, AbsentKeyException {
		JSONObject prop=in();
		return JR.slice(prop, propertyName).getString(propertyName);
	}


	/**
	 * Check if all {propertyNames} exist in the related property file
	 * @param propertyNames
	 * @return
	 * @throws IOException
	 * @throws AbsentKeyException */
	public boolean exist(
			String ...propertyNames
			) throws IOException {
		JSONObject in=in();
		for(String propertyName : propertyNames)
			if(!in.has(propertyName))
				return false;
		return true;
	}

	@Override
	public String toString() {
		try {
			return in().toString();
		} catch (IOException e) {
			return super.toString()+": related property file '"+fileName+"' not found";
		}
	}

	public String getFileName() {return fileName;}

	public static void main(String[] args) throws JSONException, IOException, AbsentKeyException, InvalidKeyException {
		PropIO jeez=new PropIO("jeez.properties");
		jeez.clear();
		jeez.put(JR.wrap("rootpackage->mood"));
		
		/*PropertiesIO io=new PropertiesIO("test.properties");
		System.out.println(io);
		io.put("lol->lol");
		io.clear();
		io.put("lol1->lol1");
		io.put("lol1->lol1");
		io.put("lol2->lol2");
		io.put("lol3->lol3");
		io.remove("lol3");
		System.out.println(io.exist("lol3"));
		io.put("lol4->lol4");
		io.put("lol5->lol5");
		io.put("lol1->lol1");
		System.out.println(io.getEntry("lol1"));
		System.out.println(io.get("lol1"));
		System.out.println(io);
		System.out.println(io.get("lol"));*/
	}

}
