package mood.users.places.db;

import java.util.Date;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;

import tools.db.DBConnectionManager;

public class UserPlacesDB {


	private static DBCollection collection = DBConnectionManager.getMongoDBCollection("Pp");

	/**
	 * Update user profile places and add current profile place to the historic 
	 * @param uid
	 * @param places
	 * @throws DBException*/
	public static void updatePp(String uid,String places) {
		collection.update(new BasicDBObject().append("uid",uid),
				new BasicDBObject()
				.append("$addToSet",
						new BasicDBObject()
						.append("history",
								new BasicDBObject()
								.append("places",places)
								.append("date",new Date())))
				.append("$set",
						new BasicDBObject()
						.append("places",places)
						.append("date",new Date()) ),true,false);
	}

	/**
	 * Return user current profile places without historic
	 * @param uid */
	public static String getPp(String uid) {
		DBObject res =collection.findOne(
				new BasicDBObject().append("uid",uid));
		if(res!=null)
			return (String)res.get("places");
		return null;
	}
}
