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
import app.model.DatasetModel;
import app.model.UserDatasetCorrection;

public class UserDatasetCorrectionDAO {
	public UserDatasetCorrection getDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrection");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);					
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
				}
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	public List<UserDatasetCorrection> getAllDatasets() {
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();
		 
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				DBCursor cursor = coll.find(); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
					UserDatasetCorrection item = new UserDatasetCorrection();
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
							
			} catch (Exception e) {}
			return tasks;
	}
	/*
	  * This method is used to update document in MongoDB
	  */
	 public void addDocument(UserDatasetCorrection document) {
		 try {
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrection");
			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 }
	 public void updateDocument(UserDatasetCorrection document) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", document.getId());
		 searchObj.put("datasetVersion", document.getDatasetVersion());
		 searchObj.put("userId", document.getUserId());
		 try {
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrection");
			
			coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 }
	 /*
	  * This method is used to create an object for update or save purpose in MongoDB
	  */
	private BasicDBObject toBasicDBObject(UserDatasetCorrection document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("transId", document.getTransId());
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("revision", document.getRevision());
		newdbobj.put("lastRevision", document.getLastRevision());
		newdbobj.put("id", document.getId());
		newdbobj.put("datasetVersion", document.getDatasetVersion());
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
	public Boolean isDocumentExist(int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrection");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					return true;
				}
				
		 } catch (Exception e) {
			 return false;
		 }
		 return false;
		
	}
	 
}
