package app.config;

import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.MongoClient;

public class MongoDBManager {
	private static MongoClient mongo = null;
	private static DB db = null;
	private MongoDBManager() {};
	
	public static synchronized DB getDB(String dbName) throws Exception {
		if(mongo == null) {
			MongoClient mongo = new MongoClient("localhost",27017);
			db = new DB(mongo, "QaldCuratorFiltered");
		} 
		return db;
	}
	public boolean collectionExists(final String db, final String collectionName) {
		try {
			DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db1.getCollection(collectionName); //Collection
			DBCursor cursor = coll.find();
		} catch (Exception e) {}
	    return false;
	}
}
