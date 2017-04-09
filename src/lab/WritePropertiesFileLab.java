package lab;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Properties;

public class WritePropertiesFileLab {

	public static void main(String[] args) {
		Properties prop = new Properties();
		OutputStream output = null;

		try {
			output = new FileOutputStream("jeez.properties");

			// set the properties value
			prop.setProperty("classpath", "/WEB-INF/classes/");
			prop.setProperty("rootpackage", "mood");

			// save properties to project root folder
			prop.store(output, null);
		} 
		catch (IOException io) { io.printStackTrace(); }
		finally {
			if (output != null) 
				try { output.close(); }
			catch (IOException e) {e.printStackTrace();}
		}
	}
}
