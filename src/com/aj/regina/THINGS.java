package com.aj.regina;

import java.util.Date;

import com.aj.tools.Caller;
import com.mongodb.*;

import org.bson.types.ObjectId;
import org.json.JSONObject;
import tools.db.DBException;

/**
 * @author AJoan
 *
 * Using THINGS allows :
 * More logical not concerning database in services
 * we prefer convert incoming JSON into BSON (binary format)
 * before insertion into the database : https://www.mongodb.com/json-and-bson */
public class THINGS{


	/**
	 * @DESCRIPTION insert {things} in the {collection}
	 * @param things
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static DBCommit add(
			JSONObject things,
			DBCollection collection
	) throws DBException{
		DBAction action = DBAction.ADD;
		BasicDBObject doc = dressJSON(things).append("_date",new Date());
		WriteResult wr = collection.insert(doc);
		logDBAction(things,collection,Caller.whoIsAsking(),action);
		return new DBCommit(collection,(ObjectId)doc.get("_id"),action, wr);
	}
	
	

	/**
	 * @DESCRIPTION update {things} somewhere in the {collection} where {where} condition match
	 * @param things
	 * @param where
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static WriteResult replaceOne(
			JSONObject where,
			JSONObject things,
			DBCollection collection
	) throws DBException{
		WriteResult wr = collection.update(
				dressJSON(where),
				dressJSON(things),
				false,false
		);
		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.UPDATEONE);
		return wr;
	}



	/**TODO : update _date
	 * @DESCRIPTION update {things} everywhere in the {collection} where {where} condition match
	 * @param things
	 * @param where
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static WriteResult replaceAll(
			JSONObject where,
			JSONObject things,
			DBCollection collection,
			String caller
	) throws DBException{
		WriteResult wr = collection.update(
				dressJSON(where),
				dressJSON(things),
				false,true
		);
		logDBAction(things,collection,caller,DBAction.UPDATEALL);
		return wr;
	}


	//TODO faire un update et un upsert(add if not exists) : itere sur le json et remplace les value par des $set de values

	/**TODO : update _date
	 * @DESCRIPTION upsert {things} somewhere in the {collection} where {where} condition match
	 * @param where
	 * @param things
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static WriteResult putOne(
			JSONObject where,
			JSONObject things,
			DBCollection collection
	) throws DBException{
		WriteResult wr = collection.update(
				dressJSON(where),
				dressJSON(things).append("_date",new Date()),
				true,false
		);
		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.PUTONE);
		return wr;
	}



	/**
	 * @DESCRIPTION upsert {things} everywhere in the {collection} where {where} condition match
	 * @param things
	 * @param where
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static WriteResult putAll(
			JSONObject where,
			JSONObject things,
			DBCollection collection
			) throws DBException{
		WriteResult wr = collection.update(
				dressJSON(where),
				dressJSON(things).append("_date",new Date()),
				true,true
		);
		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.PUTALL);
		return wr;
	}



	/**
	 * @DESCRIPTION match if {things} exists in database(
	 * NB: A thing is literally an entry in the map (<K,V>)
	 * @param things
	 * @param collection
	 * @param caller
	 * @throws DBException */
	public static boolean exists(
			JSONObject things,
			DBCollection collection
	) throws DBException{
		boolean response = collection.find(
				dressJSON(things)
		).limit(1).hasNext(); //limit 1 is for optimisation : https://blog.serverdensity.com/checking-if-a-document-exists-mongodb-slow-findone-vs-find/
		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.EXISTS);
		return response;
	}


	/**
	 * @DESCRIPTION returns things in the map from the table using SQL select requests
	 * @param things
	 * @param collection
	 * @param caller
	 * @return */
	public static DBObject getOne(
			JSONObject things,
			DBCollection collection
	){
		DBObject dbo = collection.findOne(
				dressJSON(things)
		);
		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.GETONE);
		return dbo;
	}



	/**
	 * @DESCRIPTION returns things in the map from the table using SQL select requests
	 * @param things
	 * @param collection
	 * @param caller
	 * @return */
	public static DBCursor get(
			JSONObject things,
			DBCollection collection
	){
		DBCursor dbc = collection.find(
				dressJSON(things)
		);
		logDBAction(things,collection,Caller.whoIsAsking(),DBAction.GET);
		return dbc;
	}



	/**
	 * @DESCRIPTION remove things from the table using SQL delete requests
	 * @param things
	 * @param collection
	 * @param caller
	 * @return
	 * @throws DBException */
	public static DBCommit remove(
			JSONObject things,
			DBCollection collection
	) throws DBException{
		DBAction action = DBAction.REMOVE;
		BasicDBObject doc = dressJSON(things);
		WriteResult wr = collection.remove(doc);
		logDBAction(things,collection,Caller.whoIsAsking(),action);
		return new DBCommit(collection,(ObjectId)doc.get("_id"),action, wr);
	}

	
	/**
	 * @description 
	 * Reformat a JSONObject into a BasicDBObject
	 * @param json
	 * @return */
	private static BasicDBObject dressJSON(JSONObject json){
		return new BasicDBObject(
				json.toMap()
		);
	}

	
	/**
	 * @description 
	 * Print a database related action that happened 
	 * @param things
	 * @param collection
	 * @param caller
	 * @param action */
	private static void logDBAction(
			JSONObject things,
			DBCollection collection,
			String caller,
			DBAction action
	) {
		switch (action) {
			case ADD:
				System.out.println("This Things : '"+things+"' have been added to coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
				break;

			case UPDATEONE:
				System.out.println("This Things : '"+things+"' have been updated once in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
				break;

			case UPDATEALL:
				System.out.println("This Things : '"+things+"' have been updated everywhere in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
				break;

			case PUTONE:
				System.out.println("This Things : '"+things+"' have been upserted once in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
				break;

			case PUTALL:
				System.out.println("This Things : '"+things+"' have been upserted everywhere in coll'"+collection.getFullName()+"' at the request of '"+caller+"'");
				break;

			case REMOVE:
				System.out.println("This Things : '"+things+"' have been removed everywhere in coll '"+collection.getFullName()+"' at the request of '"+caller+"'");
				break;

			case EXISTS:
				System.out.println(caller +" asked if this Things : '"+things+"' are currently present in coll '"+collection.getFullName()+"'");
				break;

			case GETONE:
				System.out.println(caller +" asked to retrieve this Things : '"+things+"' once from coll '"+collection.getFullName()+"'");
				break;

			default: System.out.println("I'v no idea wtf you're searching to log for!");
		}
	}
}