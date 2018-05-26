package app.dao;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

import app.config.MongoDBManager;
import app.model.UserDatasetCollection;
import app.model.UserDatasetCorrection;

public class UserDatasetCollectionDAO {
	/*
	  * This method is used to update document in MongoDB
	  */
	 public void addDocument(UserDatasetCollection document) {
		 try {
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCollection");
			
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
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCollection");
			
			coll.update(searchObj, newDbObj);
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
}
