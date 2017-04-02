package com.aj.regina;

import org.bson.types.ObjectId;

import com.aj.utils.JSONRefiner;
import com.mongodb.DBCollection;
import com.mongodb.WriteResult;

import tools.db.DBException;

public class DBCommit {

	private DBCollection coll;
	private DBAction action;
	private ObjectId thingID;
	private WriteResult wr;

	public DBCommit(
			DBCollection coll,
			ObjectId thingID,
			DBAction action, 
			WriteResult wr		
			){
		this.wr=wr;
		this.action=action;
		this.thingID=thingID;
		this.coll=coll;
	}


	public WriteResult rollback() throws DBException{
		switch (action) {
		case ADD : return THINGS.remove(JSONRefiner.wrap("_id",thingID),coll).getWriteResult();
		default: throw new IllegalArgumentException();
		}
	}

	public DBAction getAction() {return action;} 
	public WriteResult getWriteResult() {return wr;}
	public ObjectId getThingID() {return thingID;}
	public DBCollection getCollection() {return coll;}
}
