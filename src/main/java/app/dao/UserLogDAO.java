package app.dao;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.model.UserDatasetCorrection;
import app.model.UserLog;

public class UserLogDAO {
	public void addLogCurate(UserLog userLog) {		
		try {
			
			BasicDBObject newDbObj = toBasicDBObject(userLog);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserLog");
			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	}
	private BasicDBObject toBasicDBObject(UserLog document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("logType", document.getLogType());
		newdbobj.put("logInfo", document.getLogInfo());
		newdbobj.put("logDate", document.getLogDate());
		newdbobj.put("ipAddress", document.getIpAddress());
		return newdbobj;
	}
	public List<UserLog> getUserLogs(int userId){
		List<UserLog> userLogs = new ArrayList<UserLog>();
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);
		//searchObj.put("logType", "curate");
		try {
			//call mongoDb
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserLog"); //Collection
			DBCursor cursor = coll.find(searchObj); //Find All
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				UserLog q = gson.fromJson(dbobj.toString(), UserLog.class);
				UserLog userLog = new UserLog();
				userLog.setUserId(q.getUserId());
				userLog.setLogType(q.getLogType());
				userLog.setLogInfo(q.getLogInfo());
				userLog.setLogDate(q.getLogDate());
				userLog.setIpAddress(q.getIpAddress());
				userLogs.add(userLog);
			}
		} catch (Exception e) {}
		return userLogs;
		
	}
}
