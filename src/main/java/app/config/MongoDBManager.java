package app.config;

import com.mongodb.DB;
import com.mongodb.MongoClient;

public class MongoDBManager {
	private static MongoClient mongo = null;
	private static DB db = null;
	private MongoDBManager() {};
	
	public static synchronized DB getDB(String dbName) throws Exception {
		if(mongo == null) {
			MongoClient mongo = new MongoClient("localhost",27017);
			db = new DB(mongo, "QaldCurator");
		} 
		return db;
	}
}
