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
import app.model.UserDatasetCollection;
import app.model.UserDatasetCorrection;

public class UserDatasetCollectionDAO {
	String COLLECTIONAME = "UserDatasetCollection";
	String DATABASENAME = "QaldCuratorFiltered";
	/*
	  * This method is used to update document in MongoDB
	  */
	 public void addDocument(UserDatasetCollection document) {
		 try {
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB(DATABASENAME);
			DBCollection coll = db.getCollection(COLLECTIONAME);
			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 }
	 public void updateDocument(UserDatasetCollection document) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", document.getId());
		 searchObj.put("datasetVersion", document.getDatasetVersion());
		 searchObj.put("userId", document.getUserId());
		 try {
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB(DATABASENAME);
			DBCollection coll = db.getCollection(COLLECTIONAME);
			
			coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 }
	 public void removeAllDocument(int userId) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("userId", userId);
		 try {
			
			DB db = MongoDBManager.getDB(DATABASENAME);
			DBCollection coll = db.getCollection(COLLECTIONAME);
			
			coll.remove(searchObj);
		 } catch (Exception e) {}
	 }
	 /*
	  * This method is used to create an object for update or save purpose in MongoDB
	  */
	private BasicDBObject toBasicDBObject(UserDatasetCollection document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("transId", document.getTransId());
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("id", document.getId());
		newdbobj.put("datasetVersion", document.getDatasetVersion());
		newdbobj.put("sparqlEndpoint", document.getSparqlEndpoint());
		newdbobj.put("answerType", document.getAnswerType());
		newdbobj.put("aggregation", document.getAggregation());
		newdbobj.put("onlydbo", document.getOnlydbo());
		newdbobj.put("hybrid", document.getHybrid());
		newdbobj.put("sparqlQuery", document.getSparqlQuery());
		newdbobj.put("pseudoSparqlQuery", document.getPseudoSparqlQuery());
		newdbobj.put("goldenAnswer", document.getGoldenAnswer());
		newdbobj.put("languageToQuestion", document.getLanguageToQuestion());
		newdbobj.put("languageToKeyword", document.getLanguageToKeyword());
		newdbobj.put("outOfScope", document.getOutOfScope());
		
		return newdbobj;
	}
	/**
	 * This function used for retrieve all dataset
	 */
	public List<UserDatasetCollection> getAllDatasets(int userId) {
		List<UserDatasetCollection> tasks = new ArrayList<UserDatasetCollection>();
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB(DATABASENAME); //Database Name
				DBCollection coll = db.getCollection(COLLECTIONAME); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCollection q = gson.fromJson(dbobj.toString(), UserDatasetCollection.class);
					UserDatasetCollection item = new UserDatasetCollection();
					item.setDatasetVersion(q.getDatasetVersion());;
					item.setId(q.getId());
					item.setAnswerType(q.getAnswerType());
					item.setAggregation(q.getAggregation());
					item.setOnlydbo(q.getOnlydbo());
					item.setHybrid(q.getHybrid());
					item.setLanguageToQuestion(q.getLanguageToQuestion());
					item.setLanguageToKeyword(q.getLanguageToKeyword());
					item.setSparqlQuery(q.getSparqlQuery());
					item.setPseudoSparqlQuery(q.getPseudoSparqlQuery());
					item.setGoldenAnswer(q.getGoldenAnswer());
					tasks.add(item);
				}
				cursor.close();			
			} catch (Exception e) {}
			return tasks;
	}
	/*
	 * This function used to retrieve information from single document
	 */
	public UserDatasetCollection getDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCollection item = new UserDatasetCollection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 try {
				DB db = MongoDBManager.getDB(DATABASENAME);
				DBCollection coll = db.getCollection(COLLECTIONAME);
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCollection q = gson.fromJson(dbobj.toString(), UserDatasetCollection.class);					
					item.setId(q.getId());
					item.setDatasetVersion(q.getDatasetVersion());
					item.setAnswerType(q.getAnswerType());
					item.setAggregation(q.getAggregation());
					item.setOnlydbo(q.getOnlydbo());
					item.setHybrid(q.getHybrid());
					item.setLanguageToQuestion(q.getLanguageToQuestion());
					item.setLanguageToKeyword(q.getLanguageToKeyword());
					item.setSparqlQuery(q.getSparqlQuery());
					item.setPseudoSparqlQuery(q.getPseudoSparqlQuery());
					item.setGoldenAnswer(q.getGoldenAnswer());
					item.setOutOfScope(q.getOutOfScope());
					item.setUserId(q.getUserId());
					item.setTransId(q.getTransId());
					item.setSparqlEndpoint(q.getSparqlEndpoint());
				}
				cursor.close();
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	//determine previous document
			public UserDatasetCollection getPreviousDocument(int userId, String currentId, String datasetVersion) {
				BasicDBObject searchObj = new BasicDBObject();
				BasicDBObject cSearchObj = new BasicDBObject();
				cSearchObj.put("$lt", currentId);
				searchObj.put("id", cSearchObj);
				searchObj.put("userId", userId);
				BasicDBObject sortObj = new BasicDBObject();
				sortObj.put("id", -1);
				UserDatasetCollection item = new UserDatasetCollection();
				try {
					//call mongoDb
					DB db = MongoDBManager.getDB(DATABASENAME); //Database Name
					DBCollection coll = db.getCollection(COLLECTIONAME); //Collection
					DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); 
					while (cursor.hasNext()) {
						DBObject dbobj = cursor.next();
						Gson gson = new GsonBuilder().create();
						UserDatasetCollection q = gson.fromJson(dbobj.toString(),UserDatasetCollection.class);
						item.setId(q.getId());
						item.setDatasetVersion(q.getDatasetVersion());
						item.setAnswerType(q.getAnswerType());
						item.setAggregation(q.getAggregation());
						item.setOnlydbo(q.getOnlydbo());
						item.setHybrid(q.getHybrid());
						item.setLanguageToQuestion(q.getLanguageToQuestion());
						item.setLanguageToKeyword(q.getLanguageToKeyword());
						item.setSparqlQuery(q.getSparqlQuery());
						item.setPseudoSparqlQuery(q.getPseudoSparqlQuery());
						item.setGoldenAnswer(q.getGoldenAnswer());
						item.setOutOfScope(q.getOutOfScope());
						item.setUserId(q.getUserId());
						item.setSparqlEndpoint(q.getSparqlEndpoint());
						item.setTransId(q.getTransId());
					}
					cursor.close();				
				} catch (Exception e) {}
				return item;
			}
			//determine next document
			public UserDatasetCollection getNextDocument(int userId, String currentId, String datasetVersion) {
				BasicDBObject searchObj = new BasicDBObject();
				BasicDBObject cSearchObj = new BasicDBObject();
				cSearchObj.put("$gt", currentId);
				searchObj.put("id", cSearchObj);
				searchObj.put("userId", userId);
				
				BasicDBObject sortObj = new BasicDBObject();
				sortObj.put("id", 1);
				UserDatasetCollection item = new UserDatasetCollection();
				try {
					//call mongoDb
					DB db = MongoDBManager.getDB(DATABASENAME); //Database Name
					DBCollection coll = db.getCollection(COLLECTIONAME); //Collection
					DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); 
					while (cursor.hasNext()) {
						DBObject dbobj = cursor.next();
						Gson gson = new GsonBuilder().create();
						UserDatasetCollection q = gson.fromJson(dbobj.toString(),UserDatasetCollection.class);
						item.setId(q.getId());
						item.setDatasetVersion(q.getDatasetVersion());
						item.setAnswerType(q.getAnswerType());
						item.setAggregation(q.getAggregation());
						item.setOnlydbo(q.getOnlydbo());
						item.setHybrid(q.getHybrid());
						item.setLanguageToQuestion(q.getLanguageToQuestion());
						item.setLanguageToKeyword(q.getLanguageToKeyword());
						item.setSparqlQuery(q.getSparqlQuery());
						item.setPseudoSparqlQuery(q.getPseudoSparqlQuery());
						item.setGoldenAnswer(q.getGoldenAnswer());
						item.setOutOfScope(q.getOutOfScope());
						item.setUserId(q.getUserId());
						item.setSparqlEndpoint(q.getSparqlEndpoint());
						item.setTransId(q.getTransId());
					}
					cursor.close();				
				} catch (Exception e) {}
				return item;
			}	
			
}
