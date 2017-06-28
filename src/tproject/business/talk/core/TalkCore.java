package tproject.business.talk.core;

import org.json.JSONObject;

import com.aj.jeez.jr.JR;
import com.aj.jeez.jr.exceptions.AbsentKeyException;
import com.mongodb.DBCollection;

import tproject.business.talk.db.TalkDB;
import tproject.business.user.io.db.UserDB;
import tproject.tools.db.DBException;
import tproject.tools.services.ShouldNeverOccurException;

/**
 * @author AJoan*/
public class TalkCore{
	public static DBCollection talkdb = TalkDB.collection;
	public static DBCollection userdb = UserDB.collection;


	public static JSONObject A_B(
			String A, 
			String B
			) throws ShouldNeverOccurException, DBException, AbsentKeyException{
		
		return JR.wrap("$or",
						JR.list(
								JR.wrap(TalkDB._from+"->"+A,TalkDB._to+"->"+B),
								JR.wrap(TalkDB._from+"->"+B,TalkDB._to+"->"+A)
								)
						);
	}
}
