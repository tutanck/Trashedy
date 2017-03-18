package tools.db;

import java.net.UnknownHostException;
import java.sql.SQLException;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.Mongo;

public class DBConnectionManager {
	
	private static DB db=null;
	
	/*Mongo*/
	public static String mongo_host = "127.0.0.1"; //Mongo server address
	public static int mongo_port = 27017; //Mongo server port
	public static String mongo_db = "mood_db"; //Mongo Database's name


	/**
	 * Mongo server connection
	 * @return */
	public static DB getMongoDB() {
		try {
			if(db==null)
			db = new Mongo(mongo_host,mongo_port).getDB(mongo_db);
		}catch (UnknownHostException e) {
			e.printStackTrace();
		}
		return db;
	}

	/**
	 * Access to MongoDb collection 
	 * @param collectionName
	 * @return */
	public static DBCollection getMongoDBCollection(
			String collectionName
			){
		return getMongoDB().getCollection(collectionName);
	}
	
	
	//tests
	public static void main(String[] args) throws SQLException {
		System.out.println("Trying to connect with MongoDB");
		getMongoDB();
		System.out.println("Success...");
		String collName="comments";
		System.out.println("Trying to retrieve MongoDB Collection '"+collName+"'");
		getMongoDBCollection(collName);
		System.out.println("Success...");
	}
}