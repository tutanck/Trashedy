package mood.user.post.db;

import com.mongodb.DBCollection;

import tools.db.DBManager;


/**
 * @author AJoan */
public class PostDB {

	public static DBCollection collection = DBManager.collection("posts");	
}