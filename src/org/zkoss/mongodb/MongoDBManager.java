package org.zkoss.mongodb;

import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;

/**
 * @author Ashish
 *
 */
public class MongoDBManager {

	private static MongoClient mongo = null;
	private static DB db = null;
	private MongoDBManager() {};
	
	public static synchronized DB getDB(String dbName) throws Exception {
		if(mongo == null) {
			MongoClient mongo = new MongoClient("localhost",27017);
			db = new DB(mongo, "qald");
		} 
		return db;
	}
}