package tproject.business.user.grade.core;

import com.mongodb.DBCollection;

import tproject.business.user.grade.db.GradeDB;

/**
 * @author AJoan*/
public class GradeCore{
	
	public static DBCollection userdb = GradeDB.collection;
	
}
