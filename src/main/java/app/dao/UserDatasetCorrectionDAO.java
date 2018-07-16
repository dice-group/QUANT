package app.dao;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.JSONObject;
import org.json.simple.JSONArray;
import org.json.simple.parser.ParseException;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.fasterxml.jackson.core.type.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.model.CorrectionSummary;
import app.model.Dataset;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.DocumentList;
import app.model.User;
import app.model.UserDatasetCorrection;
import app.model.UserDatasetCorrectionTemp;
import app.model.UserLog;
import app.sparql.SparqlService;
import app.util.TranslatorService;
import app.dao.DocumentDAO;
import app.controller.SparqlCorrection;

public class UserDatasetCorrectionDAO {
	public UserDatasetCorrection getDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 
		 String[] arrayString = new String[2];
		 arrayString[0]="curated";
		 arrayString[1]="noNeedChanges";

		 BasicDBObject searchWithOR= new BasicDBObject();
		 searchWithOR.put("$in", arrayString);
		 searchObj.put("status", searchWithOR);
		 
		 //searchObj.put("status", "curated");
		 BasicDBObject sortObj = new BasicDBObject();
		 sortObj.put("revision", -1);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrection");
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
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
					item.setUserId(q.getUserId());
					item.setRevision(q.getRevision());
					item.setStartingTimeCuration(q.getStartingTimeCuration());
					item.setFinishingTimeCuration(q.getFinishingTimeCuration());
					item.setTransId(q.getTransId());
					item.setStatus(q.getStatus());
				}
				return item;
		 } catch (Exception e) {}
		 return item;
	 }	
	
	//get detail of removed document
	public UserDatasetCorrection getRemovedDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);	 
		 searchObj.put("status", "removed");	 
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
					item.setUserId(q.getUserId());
					item.setRevision(q.getRevision());
					item.setStartingTimeCuration(q.getStartingTimeCuration());
					item.setFinishingTimeCuration(q.getFinishingTimeCuration());
					item.setTransId(q.getTransId());
					item.setStatus(q.getStatus());
				}
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	
	public UserDatasetCorrection getDocumentCurationProcess(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 		 
		 String[] arrayString = new String[2];
		 arrayString[0]="curated";
		 arrayString[1]="noNeedChanges";

		 BasicDBObject searchWithOR= new BasicDBObject();
		 searchWithOR.put("$in", arrayString);
		 searchObj.put("status", searchWithOR);		 
		 BasicDBObject sortObj = new BasicDBObject();
		 sortObj.put("revision", -1);
		 /*BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();		 
		 List<BasicDBObject> searchArguments = new ArrayList<BasicDBObject>();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 searchObj.put("status", "curated");
		 searchArguments.add(searchObj);
		 BasicDBObject searchObj1 = new BasicDBObject();
		 List<BasicDBObject> searchArguments1 = new ArrayList<BasicDBObject>();
		 searchObj1.put("id", id);
		 searchObj1.put("datasetVersion", datasetVersion);
		 searchObj1.put("userId", userId);
		 searchObj1.put("status", "noNeedChanges");
		 searchArguments1.add(searchObj1);
		 List<BasicDBObject> searchArgumentsAll = new ArrayList<BasicDBObject>();
		 searchArgumentsAll.add(searchObj);
		 searchArgumentsAll.add(searchObj1);
		 BasicDBObject searchObject = new BasicDBObject();
		 searchObject.put("$or", searchArgumentsAll);	 
		 
		 BasicDBObject sortObj = new BasicDBObject();
		 sortObj.put("revision", -1);*/
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrection");
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
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
					item.setUserId(q.getUserId());
					item.setRevision(q.getRevision());
					item.setStartingTimeCuration(q.getStartingTimeCuration());
					item.setFinishingTimeCuration(q.getFinishingTimeCuration());
					item.setTransId(q.getTransId());
					item.setStatus(q.getStatus());
				}
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	
	public UserDatasetCorrectionTemp getTempDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrectionTemp item = new UserDatasetCorrectionTemp();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 BasicDBObject sortObj = new BasicDBObject();
		 sortObj.put("lastRevision", -1);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrectionTemp");
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrectionTemp q = gson.fromJson(dbobj.toString(), UserDatasetCorrectionTemp.class);					
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
					item.setLastRevision(q.getLastRevision());
					item.setTransId(q.getTransId());					
				}
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	
	//remove a document for remove function
	public void removeDocumentBeforeCurationDone(UserDatasetCorrection document) {
		 BasicDBObject searchObj = new BasicDBObject();		 
		 searchObj.put("id", document.getId());
		 searchObj.put("datasetVersion", document.getDatasetVersion());
		 searchObj.put("userId", document.getUserId());		 
		 BasicDBObject sortObj = new BasicDBObject();
		 sortObj.put("startingTimeCuration", -1);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrection");
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
					q.setAggregation(document.getAggregation());
					q.setAnswerType(document.getAnswerType());
					q.setDatasetVersion(document.getDatasetVersion());
					q.setGoldenAnswer(document.getGoldenAnswer());
					q.setHybrid(document.getHybrid());
					q.setId(document.getId());
					q.setLanguageToKeyword(document.getLanguageToKeyword());
					q.setLanguageToQuestion(document.getLanguageToQuestion());			
					q.setOnlydbo(document.getOnlydbo());
					q.setOutOfScope(document.getOutOfScope());
					q.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
					q.setRevision(document.getRevision());
					q.setSparqlQuery(document.getSparqlQuery());
					q.setStatus(document.getStatus());
					q.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					q.setUserId(document.getUserId());										
					q.setRemovingTime(document.getRemovingTime());
					updateDocument(q);
				}				
		 } catch (Exception e) {}
		 
	 }
	
	//delete temporary document after curation is done
	public void deleteTempDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();		 
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserDatasetCorrectionTemp");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrectionTemp q = gson.fromJson(dbobj.toString(), UserDatasetCorrectionTemp.class);										
					deleteDocument(q);
				}
				
		 } catch (Exception e) {}
		 
	 }
	
	//delete process applied in UserDatasetCorrectionTemp collection	
	 public void deleteDocument(UserDatasetCorrectionTemp document) {
		 try {		
			BasicDBObject newDbObj = toBasicDBObjectDelete(document);		
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrectionTemp");			
			coll.remove(newDbObj);
		 } catch (Exception e) {}
	 }	
		
		//delete process applied in UserLogTemp collection	
		 public void deleteTempCurationLog(UserLog document) {
			 try {		
				BasicDBObject newDbObj = toBasicDBObjectDeleteTempLog(document);		
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLogTemp");			
				coll.remove(newDbObj);
			 } catch (Exception e) {}
		 }
	 
	 //get all curation log from TempUserLog and store them in UserLog. After that, the log info is removed from UserLogTemp
	 public void getAllCurationLogAndStoreAndDelete (int userId, String id, String datasetVersion, String startingTime, String finishingTime) {
		 BasicDBObject searchObj = new BasicDBObject();		 
		 searchObj.put("logInfo.id", id);
		 searchObj.put("logInfo.datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLogTemp");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserLog q = gson.fromJson(dbobj.toString(), UserLog.class);										
					storeCurationLog(q, startingTime, finishingTime);
					deleteTempCurationLog(q);
				}				
		 } catch (Exception e) {}
	 }
	 
	//get all curation log from TempUserLog and remove them for Remove Function
	 public void removeAllCurationLogForRemoveFunction (int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();		 
		 searchObj.put("logInfo.id", id);
		 searchObj.put("logInfo.datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLogTemp");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserLog q = gson.fromJson(dbobj.toString(), UserLog.class);				
					deleteTempCurationLog(q);
				}				
		 } catch (Exception e) {}
	 }
	 
	 //Store curation log from temporary user log in UserLog collection	
	 public void storeCurationLog(UserLog document, String startingTime, String finishingTime) {
		 try {		
			BasicDBObject newDbObj = toBasicDBObjectStoreCurationLog(document, startingTime, finishingTime);		
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserLog");			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 }
	 
	
	public List<UserDatasetCorrection> getAllDatasets(int userId) {
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj); //Find All
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
					item.setStartingTimeCuration(q.getStartingTimeCuration());
					item.setFinishingTimeCuration(q.getFinishingTimeCuration());
					item.setRevision(q.getRevision());
					item.setStatus(q.getStatus());
					tasks.add(item);
				}							
			} catch (Exception e) {}
			return tasks;
	}
	
	//Get curated questions in particular qald version
	public List<UserDatasetCorrection> getAllDatasetsInParticularVersion(int userId, String qaldTrain, String qaldTest) {
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);
		searchObj.put("datasetVersion", qaldTrain);
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("transId",-1);
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj); //Find All
				UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
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
				searchObj.put("userId", userId);
				searchObj.put("datasetVersion", qaldTest);
				DBCursor cursor1 = coll.find(searchObj).sort(sortObj); //Find All
				while (cursor1.hasNext()) {
					DBObject dbobj1 = cursor1.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj1.toString(), UserDatasetCorrection.class);
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
	
	//Store final update of curation process in UserDatasetCorrection table	
	 public void addDocument(UserDatasetCorrection document) {
		 try {		
			BasicDBObject newDbObj = toBasicDBObject(document);		
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrection");			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 }
	 
	//Store any update during curation process in a temporary table
	 public void addDocumentInTempTable(UserDatasetCorrectionTemp document) {
		 try {		
			BasicDBObject newDbObj = toBasicDBObjectTemp(document);		
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrectionTemp");			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 }
	 
	 //Get starting time of curation
	 public String getStartingTimeCuration (int userId, String id, String datasetVersion) {
	 BasicDBObject searchObj = new BasicDBObject();
	 searchObj.put("id", id);
	 searchObj.put("datasetVersion", datasetVersion);
	 searchObj.put("userId", userId);
	 BasicDBObject sortObj = new BasicDBObject();
	 sortObj.put("startingTimeCuration", -1);
	 try {			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
			DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); //Find All	 
			while (cursor.hasNext()) {		
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
				return q.getStartingTimeCuration();
			}
		 } catch (Exception e) {}
		 return null;
	 }
	 
	//Get revision value of curation
	 public int getRevisionOfCuration (int userId, String id, String datasetVersion) {
	 BasicDBObject searchObj = new BasicDBObject();
	 searchObj.put("id", id);
	 searchObj.put("datasetVersion", datasetVersion);
	 searchObj.put("userId", userId);
	 BasicDBObject sortObj = new BasicDBObject();
	 sortObj.put("startingTimeCuration", -1);
	 try {			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
			DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); //Find All	 
			while (cursor.hasNext()) {		
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
				return q.getRevision();
			}
		 } catch (Exception e) {}
		 return 0;
	 }
	 
	 public void updateDocument(UserDatasetCorrection document) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", document.getId());
		 searchObj.put("datasetVersion", document.getDatasetVersion());
		 searchObj.put("userId", document.getUserId());
		 searchObj.put("startingTimeCuration", document.getStartingTimeCuration());
		 try {			
			 BasicDBObject newDbObj = toBasicDBObject(document);	
			 DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			 DBCollection coll = db.getCollection("UserDatasetCorrection");			
			 coll.update(searchObj, newDbObj);
		 } catch (Exception e) {
			 
		 }
	 }
	 
	 public void updateTempDocument(UserDatasetCorrectionTemp document) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", document.getId());
		 searchObj.put("datasetVersion", document.getDatasetVersion());
		 searchObj.put("userId", document.getUserId());		 
		 try {			
			 BasicDBObject newDbObj = toBasicDBObjectTemp(document);	
			 DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			 DBCollection coll = db.getCollection("UserDatasetCorrectionTemp");			
			 coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 }
	//This method is used to create an object for update, save, and delete purpose in MongoDB	  
		private BasicDBObject toBasicDBObject(UserDatasetCorrection document) {
			BasicDBObject newdbobj = new BasicDBObject();
			newdbobj.put("transId", document.getTransId());
			newdbobj.put("id", document.getId());
			newdbobj.put("userId", document.getUserId());
			newdbobj.put("datasetVersion", document.getDatasetVersion());
			newdbobj.put("answerType", document.getAnswerType());
			newdbobj.put("aggregation", document.getAggregation());
			newdbobj.put("hybrid", document.getHybrid());
			newdbobj.put("onlydbo", document.getOnlydbo());		
			newdbobj.put("sparqlQuery", document.getSparqlQuery());
			newdbobj.put("pseudoSparqlQuery", document.getPseudoSparqlQuery());
			newdbobj.put("outOfScope", document.getOutOfScope());		
			newdbobj.put("languageToQuestion", document.getLanguageToQuestion());
			newdbobj.put("languageToKeyword", document.getLanguageToKeyword());
			newdbobj.put("goldenAnswer", document.getGoldenAnswer());
			newdbobj.put("revision", document.getRevision());
			newdbobj.put("startingTimeCuration", document.getStartingTimeCuration());
			newdbobj.put("finishingTimeCuration", document.getFinishingTimeCuration());		
			newdbobj.put("status", document.getStatus());
			newdbobj.put("removingTime", document.getRemovingTime());
			return newdbobj;
		}
	
	//This method is used to create an object for update or save purpose in MongoDB	 
		private BasicDBObject toBasicDBObjectTemp(UserDatasetCorrectionTemp document) {
			BasicDBObject newdbobj = new BasicDBObject();
			newdbobj.put("transId", document.getTransId());
			newdbobj.put("id", document.getId());
			newdbobj.put("userId", document.getUserId());
			newdbobj.put("datasetVersion", document.getDatasetVersion());
			newdbobj.put("answerType", document.getAnswerType());
			newdbobj.put("aggregation", document.getAggregation());
			newdbobj.put("hybrid", document.getHybrid());
			newdbobj.put("onlydbo", document.getOnlydbo());		
			newdbobj.put("sparqlQuery", document.getSparqlQuery());
			newdbobj.put("pseudoSparqlQuery", document.getPseudoSparqlQuery());
			newdbobj.put("outOfScope", document.getOutOfScope());		
			newdbobj.put("languageToQuestion", document.getLanguageToQuestion());
			newdbobj.put("languageToKeyword", document.getLanguageToKeyword());
			newdbobj.put("goldenAnswer", document.getGoldenAnswer());
			newdbobj.put("lastRevision", document.getLastRevision());			
			return newdbobj;
		}

	// This method is used to create an object for deleting a document from UserDatasetCorrectionTemp in MongoDB	
	private BasicDBObject toBasicDBObjectDelete(UserDatasetCorrectionTemp document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("transId", document.getTransId());
		newdbobj.put("id", document.getId());
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("datasetVersion", document.getDatasetVersion());
		newdbobj.put("answerType", document.getAnswerType());
		newdbobj.put("aggregation", document.getAggregation());
		newdbobj.put("hybrid", document.getHybrid());
		newdbobj.put("onlydbo", document.getOnlydbo());		
		newdbobj.put("sparqlQuery", document.getSparqlQuery());
		newdbobj.put("pseudoSparqlQuery", document.getPseudoSparqlQuery());
		newdbobj.put("outOfScope", document.getOutOfScope());		
		newdbobj.put("languageToQuestion", document.getLanguageToQuestion());
		newdbobj.put("languageToKeyword", document.getLanguageToKeyword());
		newdbobj.put("goldenAnswer", document.getGoldenAnswer());
		newdbobj.put("lastRevision", document.getLastRevision());		
		return newdbobj;
	}
	
	// This method is used to create an object for storing curation log in UserLog collection	
	private BasicDBObject toBasicDBObjectStoreCurationLog(UserLog document, String startingTime, String finishingTime) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("logInfo", document.getLogInfo());		
		newdbobj.put("logDate", document.getLogDate());
		newdbobj.put("logType", "curated");
		newdbobj.put("ipAddress", document.getIpAddress());
		newdbobj.put("startingTimeCuration", startingTime);
		newdbobj.put("finishingTimeCuration", finishingTime);				
		return newdbobj;
	}
	
	// This method is used to create an object for deleting curation log in UserLogTemp collection	
	private BasicDBObject toBasicDBObjectDeleteTempLog(UserLog document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("logInfo", document.getLogInfo());		
		newdbobj.put("logDate", document.getLogDate());
		newdbobj.put("ipAddress", document.getIpAddress());						
		return newdbobj;
	}
	
	//This function checks whether a document is alreadey curated
	public Boolean isDocumentCurated(int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 searchObj.put("status", "curated");
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
	
	//This function checks whether a document exists in UserDatasetCorrection collection
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
	
	public Boolean isDocumentRemoved(int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 searchObj.put("status", "removed");
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
	
	//check whether keywords suggestion have been accepted	
	public boolean haveKeywordsSuggestionBeenAccepted (int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				if (!(q.getLanguageToKeyword().isEmpty())) {
					return true;
				}
			}
		}catch (Exception e) {
				// TODO: handle exception
		}
		return false;
	}
		
	//provide direct keywords translations for the one that the keywords have been added using keywords suggestion	
	public Map<String, List<String>> generateKeywordsTranslations (int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);
		searchObj.put("userId", userId);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				List <String> keywords = q.getLanguageToKeyword().get("en");
				
				//call TranslatorService function
				//ObjectMapper mapper = new ObjectMapper();
				TranslatorService ts = new TranslatorService();
				Map <String, List<String>> results = ts.translateNewKeywords(keywords);				
				return results;				
			}			
		}catch (Exception e) {
				// TODO: handle exception
		}
		return null;
	}
	
	
	//Correction Parts
	public DatasetSuggestionModel implementCorrection(String id, String datasetVersion, String curatedStatus, int userId) {		
		BasicDBObject searchObj = new BasicDBObject();
		 DatasetSuggestionModel item = new DatasetSuggestionModel();
		 BasicDBObject sortObj = new BasicDBObject();		 
		 String dbName = "";
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				if (curatedStatus == "curated") { 
					searchObj.put("datasetVersion", datasetVersion);
					searchObj.put("userId", userId);
					searchObj.put("status", "curated");
					searchObj.put("id", id);
					sortObj.put("revision", -1);
					dbName = "UserDatasetCorrection";
				}else if (curatedStatus == "in curation"){ 
					searchObj.put("datasetVersion", datasetVersion);
					searchObj.put("userId", userId);
					searchObj.put("id", id);
					sortObj.put("id", -1);
					dbName = "UserDatasetCorrectionTemp";
				}else if (curatedStatus == "not curated"){
					dbName = datasetVersion;
					searchObj.put("id", id);
					sortObj.put("id", -1); 
				}
					
				DBCollection coll = db.getCollection(dbName);
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
				SparqlService ss = new SparqlService();	
				String query = "";
				String languageToQuestionEn = "";
				String answerType = "";
				String outOfScope = "";
				String aggregation = "";
				String hybridValue = "";
				String onlyDboValue = "";
				
				
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();					
					if (curatedStatus.equals("curated")) {
						UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
						query = q.getSparqlQuery();
						languageToQuestionEn = q.getLanguageToQuestion().get("en");
						answerType = q.getAnswerType();
						outOfScope = q.getOutOfScope();
						aggregation = q.getAggregation();
						hybridValue = q.getHybrid();
						onlyDboValue = q.getOnlydbo();
					}else if (curatedStatus.equals("in curation")){
						UserDatasetCorrectionTemp q = gson.fromJson(dbobj.toString(), UserDatasetCorrectionTemp.class);
						query = q.getSparqlQuery();
						languageToQuestionEn = q.getLanguageToQuestion().get("en");
						answerType = q.getAnswerType();
						outOfScope = q.getOutOfScope();
						aggregation = q.getAggregation();
						hybridValue = q.getHybrid();
						onlyDboValue = q.getOnlydbo();
					}else if (curatedStatus.equals("not curated")){
						DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
						query = q.getSparqlQuery();
						languageToQuestionEn = q.getLanguageToQuestion().get("en");
						answerType = q.getAnswerType();
						outOfScope = String.valueOf(q.getOutOfScope());
						aggregation = String.valueOf(q.getAggregation());
						hybridValue = String.valueOf(q.getHybrid());
						onlyDboValue = String.valueOf(q.getOnlydbo());
					}				 
											
					
					boolean answerStatus=false;				
					if (ss.isASKQuery(languageToQuestionEn)) {
						String answer = ss.getResultAskQuery(query);
						if (answer.equals(null)) {
							answerStatus = false; 
						}else {
							answerStatus = true;
							if (!answerType.equals(booleanAnswerTypeChecking(answer)) || (answerType.equals(null))) {						
								item.setAnswerTypeSugg(booleanAnswerTypeChecking(answer));						
							}
						}						
					}else {
						Set<String> answers = ss.getResultsFromCurrentEndpoint(query);
						answerStatus = true;						
						if (answers.isEmpty() || answers.equals(null)) {
							answerStatus = false;
						}else {
							for (String element:answers) {								
								if (element == null) {
									answerStatus = false;
									break;
								}
							}
						}
						
						System.out.println("Answers size is : "+answers.size());
						
						if (answerStatus) {
							if (!(answerType.equals(answerTypeChecking(answers))) || (answerType.equals(""))) {	//					
								item.setAnswerTypeSugg(answerTypeChecking(answers));								
							}
						}					
					}
					
					System.out.println("Answer Status: "+answerStatus);
					//System.out.println("Answer Type: "+ answerType);					
					
					//check whether it needs to provide SPARQL suggestion
					if (!answerStatus) {	
						try {
							Map<String, List<String>> sparqlSuggestion = sparqlCorrection(query);
							System.out.println("Sparql Query "+query);
							System.out.println("Sparql Suggestion "+sparqlSuggestion);
							ArrayList<String> listOfSuggestion = new ArrayList<String>();
							Map<String,List<String>> sparqlAndCaseList = new HashMap<String,List<String>>();
							List<String> answerList = new ArrayList<String>();
							for (Map.Entry<String, List<String>> mapEntry : sparqlSuggestion.entrySet()) {								
								if (mapEntry.getKey().contains("is missing")) {									
									String newSuggestion = mapEntry.getKey() + ". This question should be removed from the dataset.";
									sparqlAndCaseList.put(newSuggestion, mapEntry.getValue());									
									answerList.add("-");
									item.setAnswerFromVirtuosoList(answerList);
								}else {									
									/** Retrieve answer from Virtuoso current endpoint **/
									Set<String> results = new HashSet();									
									if (ss.isASKQuery(languageToQuestionEn)) {
										String result = ss.getResultAskQuery(mapEntry.getKey());										
										answerList.add(result);														
									}else {				
										results = ss.getQuery(mapEntry.getKey());
										answerList.addAll(results);										
									}	
									sparqlAndCaseList.put(mapEntry.getKey(), mapEntry.getValue());									
								}
								
								item.setSparqlAndCaseList(sparqlAndCaseList);
								item.setAnswerFromVirtuosoList(answerList);
							}							
						} catch (Exception e) {
							// TODO: handle exception
						}												
					}		
					
					//Check whether it needs to display button View Suggestion
					if (outOfScopeChecking(query, languageToQuestionEn).equals("false")) {
						//System.out.println("Result status is this: false");
						item.setResultStatus("false");
					}else {
						//System.out.println("Result status is this: true");
						item.setResultStatus("true");
					}
					//item.setResultStatus("what's happening");
					
					if (!AggregationChecking(query).equals(aggregation)) {
						item.setAggregationSugg(AggregationChecking(query));
					}				
					
					if (!onlyDboChecking(query).equals(onlyDboValue)) {
						item.setOnlyDboSugg(onlyDboChecking(query));
					}
										
					if (!HybridChecking(query).equals(hybridValue)) {
						item.setHybridSugg(HybridChecking(query));
					}					
										
					String oos = "";					
					
					if (outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("false")) {
						oos ="true";
					}else if (outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("true")) {
						oos=null;
					}else if (outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("null")) {
						oos="false";
					}else if (outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("null")) {
						oos="true";
					}else if (outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("true")) {
						oos="false";
					}else if (outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("false")) {
						oos=null;
					}				
					item.setOutOfScopeSugg(oos);
					//System.out.println("OOS value is "+oos);
				}				
		 } catch (Exception e) { e.printStackTrace(); }
		 return item;
	 }
		
			 public String answerTypeChecking (Set<String> answers) {
					final String REGEX_URI = "^(\\w+):(\\/\\/)?[-a-zA-Z0-9+&@#()\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#()\\/%=~_|]";
					DocumentDAO dDAO = new DocumentDAO();
					if(answers.size()>=1) {
						//System.out.println("Answer size is "+answers.size());
						Iterator<String> answerIt = answers.iterator();
						String answer = answerIt.next();
						//System.out.println("answer is "+answer);			
						if (dDAO.isUmlaut(answer)) {				
							answer = dDAO.replaceUmlaut(answer);
						}		
						int URIExist = 0;
						boolean isUri=answer.matches(REGEX_URI);
						if (isUri) {
							URIExist = 1;
						}
						//System.out.println("isURI is "+isUri);
						while(answerIt.hasNext()) {
							if (isUri) {
								URIExist = 1;
							}				 
							answer = answerIt.next().toLowerCase();
							if (dDAO.isUmlaut(answer)) {
								answer = dDAO.replaceUmlaut(answer);
							}
							isUri=answer.matches(REGEX_URI);
						}
						//System.out.println("last isURI is "+isUri);
						if(URIExist == 1) {
							//System.out.println("Regex URI is True");
							return "resource";
						}			
						//check if all are date
						boolean isDate = validateDateFormat(answer.toString());
						while(answerIt.hasNext()&& isDate) {
							answer = answerIt.next();
							isUri &= validateDateFormat(answer.toString());
						}
						if (isDate) {
							return "date";
						}
						//check if it is a number
						if ((isNumeric(answer)) || (answer.matches("\\d.*"))) {
							return "number";
						}				
						//otherwise assume it is a string
						return "string";		
					}
					
					//otherwise its empty
					return "";
				}
		//Check Query Modifier on SPARQL query
		public String queryModifierChecking (String partOfSparql) {
			Pattern p = Pattern.compile("(GROUP BY|HAVING|ORDER BY|LIMIT)");
			Matcher m = p.matcher(partOfSparql);
			
			String queryModifier = ""; 
			if (m.find()) {
				if (partOfSparql.contains("GROUP BY")) {
					queryModifier = "GROUP BY";
				}else if (partOfSparql.contains("HAVING")) {
				  	queryModifier = "HAVING";
				} else if (partOfSparql.contains("ORDER BY")) {
					queryModifier = "ORDER BY";
				} else {
					queryModifier = "LIMIT";
				}			
			}
			return queryModifier;			
		}
		
		//Check Aggregation Value
		public String AggregationChecking (String sparqlValue)
		{
			Pattern p = Pattern.compile("(COUNT|SUM|AVG|MIN|MAX|SAMPLE|GROUP_CONCAT|GROUPCONCAT|VECTOR_AGG|COUNT DISTINCT)");
			Matcher m = p.matcher(sparqlValue);
			
			if (m.find()) {			
				return ("true");
			}else
			{						
				return ("false");
			}			
		}
		
		//Check onlyDbo Value
		public String onlyDboChecking (String sparqlQuery) {
			if (sparqlQuery.toString().toLowerCase().contains("dbo:"))
			{
				return ("true");
			}else
			{
				return ("false");
			}
		}
		
		//Check Hybrid Value
		public String HybridChecking (String sparqlValue) {
			if (sparqlValue.toLowerCase().contains("text:")) {
				return ("true");
			}else
			{
				return ("false");
			}
		}		
		//Convert a string into a number	
		public static boolean isNumeric(String str)  
		{  
		  try  
		  {  
		    double d = Double.parseDouble(str);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
		    return false;  
		  }  
		  return true;  
		}	
		//Check whether a string is a date	
		public static boolean validateDateFormat(String input) {
	        return input.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})");
	    }
		
		//Check Out of Scope Value
		public String outOfScopeChecking(String sparqlQuery, String languageToQuestionEn) {		
			SparqlService ss = new SparqlService();	
			String resultStatus="";
			if (ss.isASKQuery(languageToQuestionEn)) {			
				if (ss.getResultAskQuery(sparqlQuery).equals("null")) {
					resultStatus = "false";
				}else {
					resultStatus = "true";
				}					
			}else {
				if (ss.isNullAnswerFromEndpoint(sparqlQuery)) {
					resultStatus = "false";
				}else {
					resultStatus = "true";
				}							
			}
			return resultStatus;
		}
		// is a field curated?
		public Boolean isItemCurated (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			//searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);
			BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("finishingTimeCuration", -1);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLog");
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
				while (cursor.hasNext()) {
					return true;
				}
			}catch (Exception e) {}
			return false;			
		}
		
		//is keyword curated
		public Boolean isKeywordCurated (int userId, String id, String datasetVersion, Map<String, List<String>> item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLog");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					return true;
				}
			}catch (Exception e) {}
			return false;
		}
		
		//is question curated
		public Boolean isQuestionCurated (int userId, String id, String datasetVersion, Map<String, String> item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLog");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					return true;
				}
			}catch (Exception e) {}
			return false;
		}
		
		public int countQaldDataset(int userId, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("datasetVersion", datasetVersion);
			try {
					//call mongoDb
					DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
					DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
					DBCursor cursor = coll.find(searchObj); //Find All
					
					return cursor.count();				
				} catch (Exception e) {}
			
			return 0;
		}
		//determine previous document
		public UserDatasetCorrection getPreviousDocument(int userId, String currentId, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			BasicDBObject cSearchObj = new BasicDBObject();
			cSearchObj.put("$lt", currentId);
			searchObj.put("id", cSearchObj);
			searchObj.put("datasetVersion", datasetVersion);
			
			BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id", -1);
			UserDatasetCorrection item = new UserDatasetCorrection();
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); 
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(),UserDatasetCorrection.class);
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
					item.setRevision(q.getRevision());
					item.setStartingTimeCuration(q.getStartingTimeCuration());
					item.setFinishingTimeCuration(q.getFinishingTimeCuration());
					item.setTransId(q.getTransId());
					item.setStatus(q.getStatus());
				}
								
			} catch (Exception e) {}
			return item;
		}
		//determine next document
		public UserDatasetCorrection getNextDocument(int userId, String currentId, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			BasicDBObject cSearchObj = new BasicDBObject();
			cSearchObj.put("$gt", currentId);
			searchObj.put("id", cSearchObj);
			searchObj.put("userId", userId);
			searchObj.put("datasetVersion", datasetVersion);
			
			BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id", 1);
			UserDatasetCorrection item = new UserDatasetCorrection();
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); 
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(),UserDatasetCorrection.class);
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
					item.setRevision(q.getRevision());
					item.setStartingTimeCuration(q.getStartingTimeCuration());
					item.setFinishingTimeCuration(q.getFinishingTimeCuration());
					item.setTransId(q.getTransId());
					item.setStatus(q.getStatus());
				}
								
			} catch (Exception e) {}
			return item;
		}
		
		public List<CorrectionSummary> getCorrectionSummary() {
			List<CorrectionSummary> csList = new ArrayList<CorrectionSummary>();List<User> users = new ArrayList<User>();
			 BasicDBObject searchObj = new BasicDBObject();
			 searchObj.put("id", 1);
			 try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("User"); //Collection
				DBCursor cursor = coll.find().sort(searchObj); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					
					User q = gson.fromJson(dbobj.toString(), User.class);
					
					CorrectionSummary itemCS = new CorrectionSummary();
					itemCS.setUserId(q.getId());
					itemCS.setName(q.getName());
					itemCS.setUsername(q.getUsername());
					itemCS.setQald1(countQaldDataset(q.getId(), "QALD1_Test_dbpedia")+countQaldDataset(q.getId(), "QALD1_Train_dbpedia"));
					itemCS.setQald2(countQaldDataset(q.getId(), "QALD2_Test_dbpedia")+countQaldDataset(q.getId(), "QALD2_Train_dbpedia"));
					itemCS.setQald3(countQaldDataset(q.getId(), "QALD3_Test_dbpedia")+countQaldDataset(q.getId(), "QALD3_Train_dbpedia"));
					itemCS.setQald4(countQaldDataset(q.getId(), "QALD4_Test_Multilingual")+countQaldDataset(q.getId(), "QALD4_Train_Multilingual"));
					itemCS.setQald5(countQaldDataset(q.getId(), "QALD5_Test_Multilingual")+countQaldDataset(q.getId(), "QALD5_Train_Multilingual"));
					itemCS.setQald6(countQaldDataset(q.getId(), "QALD6_Test_Multilingual")+countQaldDataset(q.getId(), "QALD6_Train_Multilingual"));
					itemCS.setQald7(countQaldDataset(q.getId(), "QALD7_Test_Multilingual")+countQaldDataset(q.getId(), "QALD7_Train_Multilingual"));
					itemCS.setQald8(countQaldDataset(q.getId(), "QALD8_Test_Multilingual")+countQaldDataset(q.getId(), "QALD8_Train_Multilingual"));
					csList.add(itemCS);
					
				}
				return csList;
			 }catch (Exception e) {
				 
			 }
			return null;
		}
		
		//Check Answer Type of Ask Query
		public String booleanAnswerTypeChecking(String answer) {
			//check if it is boolean			
			if(answer.toLowerCase().equals("true") || answer.toLowerCase().equals("false")) {
				return "boolean";
			}
			else {
				return null;
			}
		}
		
		//Apply SPARQL correction
		public Map<String, List<String>> sparqlCorrection (String sparqlQuery) throws ParseException {
			SparqlCorrection sparqlC = new SparqlCorrection();
			return sparqlC.findNewProperty(sparqlQuery);
			
		}		
		public String getNextCollection(String currentCollection) {
			Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	for (int x=0; x<listDataset.size(); x++) {
		 		if (listDataset.get(x).getName().equals(currentCollection)) {
		 			if (x==15) {
		 				return null;
		 			}else {
		 				return listDataset.get(x+1).getName();
		 			}
		 		}
		 	}
		 	return null;
		}
		public String getPreviousCollection(String currentCollection) {
			Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	for (int x=0; x<listDataset.size(); x++) {
		 		if (listDataset.get(x).getName().equals(currentCollection)) {
		 			if (x==0) {
		 				return null;
		 			}else {
		 				return listDataset.get(x-1).getName();
		 			}
		 		}
		 	}
		 	return null;
		}
}
