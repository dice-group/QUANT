package app.dao;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
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
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
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
import app.model.QALD9TrainData;
import app.model.User;
import app.model.UserDatasetCorrection;
import app.model.UserDatasetCorrectionTemp;
import app.model.UserLog;
import app.sparql.SparqlService;
import app.util.TranslatorService;
import app.dao.DocumentDAO;
import app.controller.SparqlCorrection;

public class UserDatasetCorrectionDAO {
	//This function is used to get documents from UserDatasetCorrection that have same results of curation 
		public UserDatasetCorrection getDocumentWithSameResults(String id, String datasetVersion) {
			 BasicDBObject searchObj = new BasicDBObject();
			 UserDatasetCorrection item = new UserDatasetCorrection();
			 searchObj.put("id", id);
			 searchObj.put("datasetVersion", datasetVersion);
			
			 try {
					DB db = MongoDBManager.getDB("QaldCuratorFiltered");
					DBCollection coll = db.getCollection("Annotator2AllStatus");
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
					cursor.close();
					return item;
			 } catch (Exception e) {}
			 return item;
		 }
	
	//This function is used to get documents from UserDatasetCorrection with all status: curated, removed, no changes needed
	public UserDatasetCorrection getDocumentFromAnyStatus(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);	 
		 
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
					item.setUserId(q.getUserId());
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
				cursor.close();
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	
	//This function is used to get documents from QuestiosWithDifferentCurationResult 
		public UserDatasetCorrection getDocumentForManualChecking(int userId, String id, String datasetVersion) {
			 BasicDBObject searchObj = new BasicDBObject();
			 UserDatasetCorrection item = new UserDatasetCorrection();
			 searchObj.put("id", id);
			 searchObj.put("datasetVersion", datasetVersion);
			 searchObj.put("userId", userId);	 
			 
			 BasicDBObject sortObj = new BasicDBObject();		 
			 sortObj.put("revision", -1);
			 try {
					DB db = MongoDBManager.getDB("QaldCuratorFiltered");
					DBCollection coll = db.getCollection("QuestiosWithDifferentCurationResult");
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
					cursor.close();
					return item;
			 } catch (Exception e) {}
			 return item;
		 }
	
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
				cursor.close();
				return item;
		 } catch (Exception e) {}
		 return null;
	 }	
	
	public UserDatasetCorrection getCuratedDocument(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);		 
		 searchObj.put("status", "curated"); 
		
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
				cursor.close();
				return item;
		 } catch (Exception e) {}
		 return null;
	 }
	
	public UserDatasetCorrection getDocumentByRevision(int userId, String id, String datasetVersion, int revision) {
		 BasicDBObject searchObj = new BasicDBObject();
		 UserDatasetCorrection item = new UserDatasetCorrection();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 searchObj.put("revision", revision);
		 /*String[] arrayString = new String[2];
		 arrayString[0]="curated";
		 arrayString[1]="noNeedChanges";

		 BasicDBObject searchWithOR= new BasicDBObject();
		 searchWithOR.put("$in", arrayString);
		 searchObj.put("status", searchWithOR);*/
		 
		 searchObj.put("status", "curated");
		 BasicDBObject sortObj = new BasicDBObject();
		 //sortObj.put("revision", -1);
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
				cursor.close();
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
				cursor.close();
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
				cursor.close();
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
				cursor.close();
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	
	//remove a document for cancel function. This happens when user decides to cancel doing the curation during the time of curation
	public void cancelCuration(int userId, String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();		 
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);		 
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
					deleteDocumentForCancelCuration(q);
				}
				cursor.close();
		 } catch (Exception e) {}
		 
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
					q.setSparqlSuggestion(document.getSparqlSuggestion());
					q.setStatus(document.getStatus());
					q.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					q.setUserId(document.getUserId());										
					q.setRemovingTime(document.getRemovingTime());
					updateDocument(q);
				}
				cursor.close();
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
				cursor.close();
		 } catch (Exception e) {}
		 
	 }
	
	//delete process applied in UserDatasetCorrection collection for cancel curation process	
	 public void deleteDocumentForCancelCuration(UserDatasetCorrection document) {
		 try {		
			BasicDBObject newDbObj = toBasicDBObject(document);		
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrection");			
			coll.remove(newDbObj);
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
	 public void getAllCurationLogAndStoreAndDelete (int userId, String id, String datasetVersion, String startingTime, String finishingTime, int revision) {
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
					q.setStartingTimeCuration(startingTime);
					q.setFinishingTimeCuration(finishingTime);
					q.setRevision(revision);
					storeCurationLog(q);
					deleteTempCurationLog(q);
				}	
				cursor.close();
		 } catch (Exception e) {}
	 }
	 
	//delete all curation log from TempUserLog
		 public void deleteAllCurationLog (int userId, String id, String datasetVersion) {
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
					cursor.close();
			 } catch (Exception e) {}
		 }
	 
	//get all curation log from TempUserLog and remove them for Remove and Cancel function
	 public void removeAllCurationLogFromUserLogTemp (int userId, String id, String datasetVersion) {
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
				cursor.close();
		 } catch (Exception e) {}
	 }
	 
	 //Store curation log from temporary user log in UserLog collection	
	 public void storeCurationLog(UserLog document) {
		 try {		
			BasicDBObject newDbObj = toBasicDBObjectStoreCurationLog(document);		
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserLog");			
			coll.save(newDbObj);
		 } catch (Exception e) {}
	 }
	 
	
	public List<UserDatasetCorrection> getAllDatasets(int userId) {
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();		
		BasicDBObject searchObj1 = new BasicDBObject();
		//BasicDBObject matchObj = new BasicDBObject();	
		BasicDBObject searchObj2 = new BasicDBObject();
		BasicDBObject searchObj3 = new BasicDBObject();
		BasicDBObject dataObj = new BasicDBObject();
		dataObj.put("id", "$id");
		dataObj.put("datasetVersion", "$datasetVersion");
		//dataObj.put("languageToQuestion", "$languageToQuestion");
		dataObj.put("userId", "$userId");		
		searchObj2.put("_id", dataObj);
		searchObj3.put("$max", "$revision");
		searchObj2.put("latestData", searchObj3);	
		searchObj1.put("$group", searchObj2);
		
	
			try {				
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				List<DBObject> flatPipeline = Arrays.asList(searchObj1);
				AggregationOptions aggregationOptions = AggregationOptions.builder()
						                                    .batchSize(100)
						                                    .allowDiskUse(true)
						                                    .build();
				Cursor cursor = coll.aggregate(flatPipeline,aggregationOptions);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					
					Gson gson = new GsonBuilder().create();
					
					UserDatasetCorrection q = gson.fromJson(dbobj.get("_id").toString(), UserDatasetCorrection.class);
					if (q.getUserId() == userId) {
						UserDatasetCorrection data = this.getDocumentFromAnyStatus(userId, q.getId(), q.getDatasetVersion());
						if (data.getId() != null) {
							UserDatasetCorrection item = new UserDatasetCorrection();
							item.setDatasetVersion(data.getDatasetVersion());;
							item.setId(data.getId());
							item.setAnswerType(data.getAnswerType());
							item.setAggregation(data.getAggregation());
							item.setOnlydbo(data.getOnlydbo());
							item.setHybrid(data.getHybrid());
							item.setLanguageToQuestion(data.getLanguageToQuestion());
							item.setLanguageToKeyword(data.getLanguageToKeyword());
							item.setSparqlQuery(data.getSparqlQuery());
							item.setPseudoSparqlQuery(data.getPseudoSparqlQuery());
							item.setGoldenAnswer(data.getGoldenAnswer());
							item.setStartingTimeCuration(data.getStartingTimeCuration());
							item.setFinishingTimeCuration(data.getFinishingTimeCuration());
							item.setRevision(data.getRevision());
							item.setStatus(data.getStatus());
							tasks.add(item);
						}
					}
				}	
				cursor.close();
			} catch (Exception e) {}
			return tasks;		
	}
	
	//check whether there is an unfinished curation process. This task will be done by checking whether there is a record with null finishingCurationTime value in UserDatasetCorrection. If it is the case, this record and related record will be removed from UserDatasetCorrection, UserDatasetCorrectionTemp, and UserLogTemp  
	public void checkUnfinishedCuration(int userId) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);		
		searchObj.put("finishingTimeCuration", "0");
		searchObj.put("status", null);
		
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
			DBCursor cursor = coll.find(searchObj);
			while(cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
				deleteDocumentForCancelCuration(q);
				//remove the record from temp table
				deleteTempDocument(userId, q.getId(), q.getDatasetVersion());
				//remove all related logs
				deleteAllCurationLog(userId, q.getId(), q.getDatasetVersion());				
			}
			cursor.close();
		}catch (Exception e) {
			// TODO: handle exception
		}
	}
	//Get curated questions in particular qald version
	public List<UserDatasetCorrection> getAllDatasetsInParticularVersion(int userId, String qaldTrain, String qaldTest) {
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();		
		BasicDBObject searchObj1 = new BasicDBObject();
		BasicDBObject matchObj = new BasicDBObject();	
		BasicDBObject searchObj2 = new BasicDBObject();
		BasicDBObject searchObj3 = new BasicDBObject();
		BasicDBObject dataObj = new BasicDBObject();
		dataObj.put("id", "$id");
		dataObj.put("datasetVersion", "$datasetVersion");		
		dataObj.put("userId", "$userId");		
		searchObj2.put("_id", dataObj);
		searchObj3.put("$max", "$revision");
		searchObj2.put("latestData", searchObj3);		
		searchObj1.put("$group", searchObj2);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				List<DBObject> flatPipeline = Arrays.asList(searchObj1);
				AggregationOptions aggregationOptions = AggregationOptions.builder()
						                                    .batchSize(100)
						                                    .allowDiskUse(true)
						                                    .build();
				Cursor cursor = coll.aggregate(flatPipeline,aggregationOptions);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.get("_id").toString(), UserDatasetCorrection.class);
					if ((q.getDatasetVersion().equals(qaldTrain) || q.getDatasetVersion().equals(qaldTest)) && q.getUserId() == userId) {
						UserDatasetCorrection data = this.getDocumentFromAnyStatus(userId, q.getId(), q.getDatasetVersion());		
						UserDatasetCorrection item = new UserDatasetCorrection();						
						item.setDatasetVersion(data.getDatasetVersion());
						item.setId(data.getId());
						item.setAnswerType(data.getAnswerType());
						item.setAggregation(data.getAggregation());
						item.setOnlydbo(data.getOnlydbo());
						item.setHybrid(data.getHybrid());
						item.setLanguageToQuestion(data.getLanguageToQuestion());
						item.setLanguageToKeyword(data.getLanguageToKeyword());
						item.setSparqlQuery(data.getSparqlQuery());
						item.setPseudoSparqlQuery(data.getPseudoSparqlQuery());
						item.setGoldenAnswer(data.getGoldenAnswer());
						item.setRevision(data.getRevision());
						item.setStatus(data.getStatus());						
						tasks.add(item);
					}			
				}
				cursor.close();
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
			cursor.close();
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
			cursor.close();
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
			newdbobj.put("sparqlSuggestion", document.getSparqlSuggestion());
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
			newdbobj.put("noNeedChangesTime", document.getNoNeedChangesTime());
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
	private BasicDBObject toBasicDBObjectStoreCurationLog(UserLog document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("userId", document.getUserId());
		newdbobj.put("logInfo", document.getLogInfo());		
		newdbobj.put("logDate", document.getLogDate());
		newdbobj.put("logType", "curated");
		newdbobj.put("logTypeKeyword", document.getLogTypeKeyword());
		newdbobj.put("logTypeQuestion", document.getLogTypeQuestion());
		newdbobj.put("ipAddress", document.getIpAddress());
		newdbobj.put("startingTimeCuration", document.getStartingTimeCuration());
		newdbobj.put("finishingTimeCuration", document.getFinishingTimeCuration());				
		newdbobj.put("revision", document.getRevision());
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
				cursor.close();
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
				cursor.close();
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
				cursor.close();
		 } catch (Exception e) {
			 return false;
		 }
		 return false;
		
	}
		
	//get rest of keywords translations. dbName1 is database name where the chosen translated keywords are stored in. dbName2 is database name where the complete translated keywords are stored in.  
	public Map<String, List<String>> getRestOfKeywordsTranslation (int userId, String id, String datasetVersion, String dbName1, String dbName2, String startingTime, String finishingTime) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);		
		searchObj.put("userId", userId);
		List<String> chosenKeywordsTranslationKeyList = new ArrayList<String>();		
		List<String> allKeywordsTranslationKeyList = new ArrayList<String>();
		List<String> KeywordsTranslationListKeyLeft = new ArrayList<String>();		
		
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			//get chosen keywords translations
			DBCollection coll1 = db.getCollection(dbName1);			
			DBCursor cursor1 = coll1.find(searchObj); 
			while (cursor1.hasNext()) {				
				DBObject dbobj1 = cursor1.next();
				Gson gson1 = new GsonBuilder().create();
				if (dbName1.equals("UserDatasetCorrectionTemp")){
					UserDatasetCorrectionTemp q1 = gson1.fromJson(dbobj1.toString(), UserDatasetCorrectionTemp.class);
					for (Map.Entry<String, List<String>> element: q1.getLanguageToKeyword().entrySet()) {
						chosenKeywordsTranslationKeyList.add(element.getKey());
					}					
				}else if (dbName1.equals("UserDatasetCorrection")){
					if (!(startingTime.equals(""))) {
						searchObj.put("startingTimeCuration", startingTime);
						searchObj.put("finishingTimeCuration", finishingTime);
					}					
					UserDatasetCorrection q1 = gson1.fromJson(dbobj1.toString(), UserDatasetCorrection.class);
					for (Map.Entry<String, List<String>> element: q1.getLanguageToKeyword().entrySet()) {
						chosenKeywordsTranslationKeyList.add(element.getKey());
					}								
				}
			}
			cursor1.close();
			
			
			//get all keywords translations in 11 languages
			DBCollection coll2 = db.getCollection(dbName2); 
			BasicDBObject searchObj2 = new BasicDBObject();
			searchObj2.put("id", id);
			searchObj2.put("datasetVersion", datasetVersion);
			DBCursor cursor2 = coll2.find(searchObj2);
			
			List<Map<String, List<String>>> allKeywordsTranslations = new ArrayList<>();
			while (cursor2.hasNext()) {				
				DBObject dbobj2 = cursor2.next();
				Gson gson2 = new GsonBuilder().create();
				DatasetModel q2 = gson2.fromJson(dbobj2.toString(), DatasetModel.class);
				for (Map.Entry<String, List<String>> element2: q2.getLanguageToKeyword().entrySet()) {
					allKeywordsTranslationKeyList.add(element2.getKey());
				}
				allKeywordsTranslations.add(q2.getLanguageToKeyword());
			}
			
			//get list of keywords translations that not be taken yet
			Map<String, List<String>> result = new HashMap<String, List<String>>();
			for (String element: allKeywordsTranslationKeyList) {
				if (!(chosenKeywordsTranslationKeyList.contains(element))) {
					//get complete keywords translations (with list of keywords)
					for (int i = 0; i < allKeywordsTranslations.size(); i++) {						
						for (Map.Entry<String, List<String>> entry: allKeywordsTranslations.get(i).entrySet()) {
					        if (element == (entry.getKey())) {					        						        	
					        	result.put(element, entry.getValue());
					        	break;
					        }				           
					    }
					}
				}
			}
			
			//reverse characters order in Persian Translations
			DocumentDAO dDao = new DocumentDAO();
			Map<String, List<String>> newTranslations = new HashMap<String, List<String>>();
			for (Map.Entry<String, List<String>> mapEntry : result.entrySet()) {
				if (mapEntry.getKey().equals("fa")) {
					String reversedForParsi;
					List<String> newParsiTranslations = new ArrayList<String>();
					for (String element: mapEntry.getValue()) {
						reversedForParsi = dDao.reverseString(element);
						newParsiTranslations.add(reversedForParsi);
					}
					newTranslations.put("fa", newParsiTranslations);
				}else {
					newTranslations.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
			return newTranslations;
		}catch (Exception e) {
				//TODO: handle exception
		}
		return null;
	}
	
	//get rest of question translations. dbName1 is database name where the chosen translated keywords are stored in.  
		public Map<String, String> getRestOfQuestionTranslation (int userId, String id, String datasetVersion, String dbName, String question, String startingTime, String finishingTime) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", id);
			searchObj.put("datasetVersion", datasetVersion);		
			searchObj.put("userId", userId);
			List<String> chosenQuestionTranslationKeyList = new ArrayList<String>();		
			List<String> allQuestionTranslationKeyList = new ArrayList<String>();					
			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				//get chosen keywords translations
				DBCollection coll1 = db.getCollection(dbName);			
				DBCursor cursor1 = coll1.find(searchObj); 
				while (cursor1.hasNext()) {				
					DBObject dbobj1 = cursor1.next();
					Gson gson1 = new GsonBuilder().create();
					if (dbName.equals("UserDatasetCorrectionTemp")){
						UserDatasetCorrectionTemp q1 = gson1.fromJson(dbobj1.toString(), UserDatasetCorrectionTemp.class);
						for (Map.Entry<String, String> element: q1.getLanguageToQuestion().entrySet()) {
							chosenQuestionTranslationKeyList.add(element.getKey());
						}					
					}else if (dbName.equals("UserDatasetCorrection")){
						searchObj.put("startingTimeCuration", startingTime);
						searchObj.put("finishingTimeCuration", finishingTime);
						UserDatasetCorrection q1 = gson1.fromJson(dbobj1.toString(), UserDatasetCorrection.class);
						for (Map.Entry<String, String> element: q1.getLanguageToQuestion().entrySet()) {
							chosenQuestionTranslationKeyList.add(element.getKey());
						}								
					}
				}
				cursor1.close();
				//get all question translations in 11 languages either from AllTranslations or AddedTranslations
				DocumentDAO docDaoObj = new DocumentDAO();	
				DatasetModel dmObj = docDaoObj.getQuestionTranslations(id, datasetVersion, question);
				Map<String, String> allQuestionTranslations = dmObj.getLanguageToQuestion();
				//get the key of all question translations Map
				for (Map.Entry<String, String> element2: allQuestionTranslations.entrySet()) {
					allQuestionTranslationKeyList.add(element2.getKey());
				}
				
				//get list of question translations that not be taken yet
				Map<String, String> result = new HashMap<String, String>();
				for (String element: allQuestionTranslationKeyList) {
					if (!(chosenQuestionTranslationKeyList.contains(element))) {
						//get complete keywords translations (with list of keywords)
						for (Map.Entry<String, String> entry: allQuestionTranslations.entrySet()) {
					        if (element == (entry.getKey())) {					        						        	
					        	result.put(element, entry.getValue());
					        	break;
					        }				           
						}
					}
				}			
				return result;
			}catch (Exception e) {
					//TODO: handle exception
			}
			return null;
		}
		
		//get rest of keywords translations. dbName1 is database name where the chosen translated keywords are stored in.  
				public Map<String, List<String>> getRestOfKeywordsTranslationNotSuggestion (int userId, String id, String datasetVersion, String dbName, String question, String startingTime, String finishingTime) {
					BasicDBObject searchObj = new BasicDBObject();
					searchObj.put("id", id);
					searchObj.put("datasetVersion", datasetVersion);		
					searchObj.put("userId", userId);
					List<String> chosenKeywordsTranslationKeyList = new ArrayList<String>();		
					List<String> allKeywordsTranslationKeyList = new ArrayList<String>();					
					
					try {
						DB db = MongoDBManager.getDB("QaldCuratorFiltered");
						//get chosen keywords translations
						DBCollection coll1 = db.getCollection(dbName);			
						DBCursor cursor1 = coll1.find(searchObj); 
						while (cursor1.hasNext()) {				
							DBObject dbobj1 = cursor1.next();
							Gson gson1 = new GsonBuilder().create();
							if (dbName.equals("UserDatasetCorrectionTemp")){
								UserDatasetCorrectionTemp q1 = gson1.fromJson(dbobj1.toString(), UserDatasetCorrectionTemp.class);
								for (Map.Entry<String, List<String>> element: q1.getLanguageToKeyword().entrySet()) {
									chosenKeywordsTranslationKeyList.add(element.getKey());
								}					
							}else if (dbName.equals("UserDatasetCorrection")){
								searchObj.put("startingTimeCuration", startingTime);
								searchObj.put("finishingTimeCuration", finishingTime);
								UserDatasetCorrection q1 = gson1.fromJson(dbobj1.toString(), UserDatasetCorrection.class);
								for (Map.Entry<String, List<String>> element: q1.getLanguageToKeyword().entrySet()) {
									chosenKeywordsTranslationKeyList.add(element.getKey());
								}								
							}
						}
						cursor1.close();
						//get all Keywords translations in 11 languages either from AllTranslations or AddedTranslations
						DocumentDAO docDaoObj = new DocumentDAO();	
						DatasetModel dmObj = docDaoObj.getQuestionTranslations(id, datasetVersion, question);
						Map<String, List<String>> allKeywordsTranslations = dmObj.getLanguageToKeyword();
						//get the key of all Keywords translations Map
						for (Map.Entry<String, List<String>> element2: allKeywordsTranslations.entrySet()) {
							allKeywordsTranslationKeyList.add(element2.getKey());
						}
						
						//get list of Keywords translations that not be taken yet
						Map<String, List<String>> result = new HashMap<String, List<String>>();
						for (String element: allKeywordsTranslationKeyList) {
							if (!(chosenKeywordsTranslationKeyList.contains(element))) {
								//get complete keywords translations (with list of keywords)
								for (Map.Entry<String, List<String>> entry: allKeywordsTranslations.entrySet()) {
							        if (element == (entry.getKey())) {					        						        	
							        	result.put(element, entry.getValue());
							        	break;
							        }				           
								}
							}
						}			
						return result;
					}catch (Exception e) {
							//TODO: handle exception
					}
					return null;
				}
	
	//check whether keywords or question translations have been accepted completely for 11 language during curation process
	public boolean areTranslationsCompleteDuringCurationProcess (int userId, String id, String datasetVersion, String item) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);		
		searchObj.put("userId", userId);		
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrectionTemp"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				UserDatasetCorrectionTemp q = gson.fromJson(dbobj.toString(), UserDatasetCorrectionTemp.class);
				if (item.equals("keywords")) {
					if (q.getLanguageToKeyword().size() > 10) {
						return true;
					}
				}else if (item.equals("question")) {
					if (q.getLanguageToQuestion().size() > 10) {
						return true;
					}
				}	
			}
			cursor.close();
		}catch (Exception e) {
				//TODO: handle exception
		}
		return false;
	}
	
	//check whether keywords or question translations has been accepted completely for 11 languages	
		public boolean areTranslationsComplete (int userId, String id, String datasetVersion, String startingTimeCuration, String finishingTimeCuration, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", id);
			searchObj.put("datasetVersion", datasetVersion);		
			searchObj.put("userId", userId);
			searchObj.put("startingTimeCuration", startingTimeCuration);
			searchObj.put("finishingTimeCuration", finishingTimeCuration);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				while (cursor.hasNext()) {				
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);	
					if (item.equals("keywords")) {
						if (q.getLanguageToKeyword().size() > 10) {
							return true;
						}
					}else if (item.equals("question")) {
						if (q.getLanguageToQuestion().size() > 10) {
							return true;
						}
					}
					
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
		
		//check whether question translations has been accepted completely for 11 languages	during curation process
		public boolean areQuestionTranslationsCompleteDuringCurationProcess (int userId, String id, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", id);
			searchObj.put("datasetVersion", datasetVersion);		
			searchObj.put("userId", userId);			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrectionTemp"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				while (cursor.hasNext()) {				
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrectionTemp q = gson.fromJson(dbobj.toString(), UserDatasetCorrectionTemp.class);	
					if (q.getLanguageToQuestion().size() > 10) {
						return true;
					}
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
		
		//check whether question translations has been accepted completely for 11 languages	
				public boolean areQuestionTranslationsComplete (int userId, String id, String datasetVersion, String startingTimeCuration, String finishingTimeCuration) {
					BasicDBObject searchObj = new BasicDBObject();
					searchObj.put("id", id);
					searchObj.put("datasetVersion", datasetVersion);		
					searchObj.put("userId", userId);
					searchObj.put("startingTimeCuration", startingTimeCuration);
					searchObj.put("finishingTimeCuration", finishingTimeCuration);
					try {
						DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
						DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
						DBCursor cursor = coll.find(searchObj); 
						while (cursor.hasNext()) {				
							DBObject dbobj = cursor.next();
							Gson gson = new GsonBuilder().create();
							UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);	
							if (q.getLanguageToQuestion().size() > 10) {
								return true;
							}
						}
						cursor.close();
					}catch (Exception e) {
							//TODO: handle exception
					}
					return false;
				}
	
	//check whether keywords translations has been accepted completely for 11 languages	during curation process
	public boolean isKeywordsTranslationsCompleteDuringCuration (int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);		
		searchObj.put("userId", userId);		
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrectionTemp"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				UserDatasetCorrectionTemp q = gson.fromJson(dbobj.toString(), UserDatasetCorrectionTemp.class);	
				if (q.getLanguageToKeyword().size() > 10) {
					return true;
				}
			}
		}catch (Exception e) {
				//TODO: handle exception
		}
		return false;
	}
	
	//check whether keywords suggestion have been accepted. 	
	public boolean haveKeywordsSuggestionBeenAccepted (int userId, String id, String datasetVersion, String startingTime, String finishingTime, int revision) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("logInfo.id", id);
		searchObj.put("logInfo.datasetVersion", datasetVersion);
		searchObj.put("logInfo.field", "languageToKeyword");
		searchObj.put("userId", userId);
		searchObj.put("logTypeKeyword", "suggestion");
		searchObj.put("startingTimeCuration", startingTime);
		searchObj.put("finishingTimeCuration", finishingTime);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserLog"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			if (cursor.hasNext()) {				
				return true;				
			}else {
				BasicDBObject searchObj1 = new BasicDBObject();
				while (revision != 0) {
					revision = revision - 1;
					searchObj1.put("logInfo.id", id);
					searchObj1.put("logInfo.datasetVersion", datasetVersion);
					searchObj1.put("logInfo.field", "languageToKeyword");
					searchObj1.put("userId", userId);
					searchObj1.put("logTypeKeyword", "suggestion");
					searchObj1.put("revision", revision);
					DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
					DBCollection coll1 = db1.getCollection("UserLog"); 
					DBCursor cursor1 = coll1.find(searchObj1);
					if (cursor1.hasNext()) {
						return true;						
					}
					cursor1.close();
				}
				
			}
			cursor.close();
		}catch (Exception e) {
				//TODO: handle exception
		}
		return false;
	}
	
	//check whether keywords suggestion have been accepted during curation process. 	
	public boolean haveKeywordsSuggestionBeenAcceptedDuringCurationProcess (int userId, String id, String datasetVersion, int revision) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("logInfo.id", id);
		searchObj.put("logInfo.datasetVersion", datasetVersion);
		searchObj.put("logInfo.field", "languageToKeyword");
		searchObj.put("userId", userId);
		searchObj.put("logTypeKeyword", "suggestion");
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserLogTemp"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			if (cursor.hasNext()) {				
				return true;				
			}else {
				BasicDBObject searchObj1 = new BasicDBObject();
				while (revision != 0) {
					revision = revision - 1;
					searchObj1.put("logInfo.id", id);
					searchObj1.put("logInfo.datasetVersion", datasetVersion);
					searchObj1.put("logInfo.field", "languageToKeyword");
					searchObj1.put("userId", userId);
					searchObj1.put("logTypeKeyword", "suggestion");
					searchObj1.put("revision", revision);
					DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
					DBCollection coll1 = db1.getCollection("UserLog"); 
					DBCursor cursor1 = coll1.find(searchObj1);
					if (cursor1.hasNext()) {
						return true;						
					}
					cursor1.close();
				}
			}
			cursor.close();
		}catch (Exception e) {
				//TODO: handle exception
		}
		return false;
	}
	
	//check whether suggested keywords have been translated 	
		public boolean haveKeywordsSuggestionBeenTranslated (int userId, String id, String datasetVersion, String startingTime, String finishingTime, int revision) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", "languageToKeyword");
			searchObj.put("userId", userId);	
			searchObj.put("logTypeKeyword", "translation");
			searchObj.put("startingTimeCuration", startingTime);
			searchObj.put("finishingTimeCuration", finishingTime);
			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserLog"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				if (cursor.hasNext()) {				
					return true;				
				}else {
					BasicDBObject searchObj1 = new BasicDBObject();
					while (revision != 0) {
						revision = revision - 1;
						searchObj1.put("logInfo.id", id);
						searchObj1.put("logInfo.datasetVersion", datasetVersion);
						searchObj1.put("logInfo.field", "languageToKeyword");
						searchObj1.put("userId", userId);
						searchObj1.put("logTypeKeyword", "translation");
						searchObj1.put("revision", revision);
						DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
						DBCollection coll1 = db1.getCollection("UserLog"); 
						DBCursor cursor1 = coll1.find(searchObj1);
						if (cursor1.hasNext()) {
							return true;						
						}
						cursor1.close();
					}
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
	
		//check whether question have been translated either for all or added translations 	
		public boolean hasQuestionBeenTranslated (int userId, String id, String datasetVersion, String startingTime, String finishingTime, int revision) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", "languageToQuestion");
			searchObj.put("userId", userId);	
			searchObj.put("logTypeQuestion", "translation");
			searchObj.put("startingTimeCuration", startingTime);
			searchObj.put("finishingTimeCuration", finishingTime);
			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserLog"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				/*while (cursor.hasNext()) {				
					return true;				
				}*/
				if (cursor.hasNext()) {				
					return true;				
				}else {
					BasicDBObject searchObj1 = new BasicDBObject();
					while (revision != 0) {
						revision = revision - 1;
						searchObj1.put("logInfo.id", id);
						searchObj1.put("logInfo.datasetVersion", datasetVersion);
						searchObj1.put("logInfo.field", "languageToQuestion");
						searchObj1.put("userId", userId);
						searchObj1.put("logTypeQuestion", "translation");
						searchObj1.put("revision", revision);
						DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
						DBCollection coll1 = db1.getCollection("UserLog"); 
						DBCursor cursor1 = coll1.find(searchObj1);
						if (cursor1.hasNext()) {
							return true;						
						}
						cursor1.close();
					}
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
		
		//check whether question have been translated either for all or added translations during curation process	
		public boolean hasQuestionBeenTranslatedDuringCurationProcess (int userId, String id, String datasetVersion, int revision) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", "languageToQuestion");
			searchObj.put("userId", userId);	
			searchObj.put("logTypeQuestion", "translation");
			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserLogTemp"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				if (cursor.hasNext()) {				
					return true;				
				}else {
					BasicDBObject searchObj1 = new BasicDBObject();
					while (revision != 0) {
						revision = revision - 1;
						searchObj1.put("logInfo.id", id);
						searchObj1.put("logInfo.datasetVersion", datasetVersion);
						searchObj1.put("logInfo.field", "languageToQuestion");
						searchObj1.put("userId", userId);
						searchObj1.put("logTypeQuestion", "translation");
						searchObj1.put("revision", revision);
						DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
						DBCollection coll1 = db1.getCollection("UserLog"); 
						DBCursor cursor1 = coll1.find(searchObj1);
						if (cursor1.hasNext()) {
							return true;						
						}
						cursor1.close();
					}
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
				
		//check whether keywords have been translated either for all or added translations 	
		public boolean haveKeywordsBeenTranslated (int userId, String id, String datasetVersion, String startingTime, String finishingTime, int revision) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", "languageToKeyword");
			searchObj.put("userId", userId);	
			searchObj.put("logTypeKeyword", "translation");
			searchObj.put("startingTimeCuration", startingTime);
			searchObj.put("finishingTimeCuration", finishingTime);
			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserLog"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				if (cursor.hasNext()) {				
					return true;				
				}else {
					BasicDBObject searchObj1 = new BasicDBObject();
					while (revision != 0) {
						revision = revision - 1;
						searchObj1.put("logInfo.id", id);
						searchObj1.put("logInfo.datasetVersion", datasetVersion);
						searchObj1.put("logInfo.field", "languageToKeyword");
						searchObj1.put("userId", userId);
						searchObj1.put("logTypeKeyword", "translation");
						searchObj1.put("revision", revision);
						DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
						DBCollection coll1 = db1.getCollection("UserLog"); 
						DBCursor cursor1 = coll1.find(searchObj1);
						if (cursor1.hasNext()) {
							return true;						
						}
						cursor1.close();
					}
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
		
		//check whether keywords have been translated either for all or added translations during curation process	
		public boolean haveKeywordsBeenTranslatedDuringCurationProcess (int userId, String id, String datasetVersion, int revision) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", "languageToKeyword");
			searchObj.put("userId", userId);	
			searchObj.put("logTypeKeyword", "translation");
			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserLogTemp"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				if (cursor.hasNext()) {				
					return true;				
				}else {
					BasicDBObject searchObj1 = new BasicDBObject();
					while (revision != 0) {
						revision = revision - 1;
						searchObj1.put("logInfo.id", id);
						searchObj1.put("logInfo.datasetVersion", datasetVersion);
						searchObj1.put("logInfo.field", "languageToKeyword");
						searchObj1.put("userId", userId);
						searchObj1.put("logTypeKeyword", "translation");
						searchObj1.put("revision", revision);
						DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
						DBCollection coll1 = db1.getCollection("UserLog"); 
						DBCursor cursor1 = coll1.find(searchObj1);
						if (cursor1.hasNext()) {
							return true;						
						}
						cursor1.close();
					}
				}
				cursor.close();
			}catch (Exception e) {
					//TODO: handle exception
			}
			return false;
		}
				
	//check whether suggested keywords have been translated during curation process. 	
	public boolean haveKeywordsSuggestionBeenTranslatedDuringCurationProcess (int userId, String id, String datasetVersion, int revision) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("logInfo.id", id);
		searchObj.put("logInfo.datasetVersion", datasetVersion);
		searchObj.put("logInfo.field", "languageToKeyword");
		searchObj.put("userId", userId);	
		searchObj.put("logTypeKeyword", "translation");
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserLogTemp"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			if (cursor.hasNext()) {				
				return true;				
			}else {
				BasicDBObject searchObj1 = new BasicDBObject();
				while (revision != 0) {
					revision = revision - 1;
					searchObj1.put("logInfo.id", id);
					searchObj1.put("logInfo.datasetVersion", datasetVersion);
					searchObj1.put("logInfo.field", "languageToKeyword");
					searchObj1.put("userId", userId);
					searchObj1.put("logTypeKeyword", "translation");
					searchObj1.put("revision", revision);
					DB db1 = MongoDBManager.getDB("QaldCuratorFiltered"); 
					DBCollection coll1 = db1.getCollection("UserLog"); 
					DBCursor cursor1 = coll1.find(searchObj1);
					if (cursor1.hasNext()) {
						return true;						
					}
					cursor1.close();
				}
			}
			cursor.close();
		}catch (Exception e) {
				//TODO: handle exception
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
			cursor.close();
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
							//item.setSparqlCorrectionStatus(true);
							//item.setResultStatus(answerTypeChecking(answers));
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
							Set<String> answerList = new HashSet<>();
							if (sparqlSuggestion.size()!=0) {
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
				cursor.close();
		 } catch (Exception e) { e.printStackTrace(); }
		 return item;
	 }
		
	public String answerTypeChecking (Set<String> answers) {
		//final String REGEX_URI = "^(\\w+):(\\/\\/)?[-a-zA-Z0-9+&@#()\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#()\\/%=~_|]";
		final Set<String> VALID_SCHEMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
			      "http://", "https://", "ftp://", "ftps://", "http%3a//", "https%3a//", "ftp%3a//", "ftps%3a//")));
		DocumentDAO dDao = new DocumentDAO();
		try {
			if(answers.size()>=1) {
				
				Iterator<String> answerIt = answers.iterator();

				//the first element
				String answer = answerIt.next();
				//System.out.println("answer is "+answer);			
				if (dDao.isUmlaut(answer)) {				
					answer = dDao.replaceUmlaut(answer);
				}	
				
				//check whether the first element or the only one element is a URI
				int URIExist = 0;
				boolean isUri = false;
				for (String scheme: VALID_SCHEMES) {
					if (answer.contains(scheme)) {
						isUri= true;
						break;
					}
				}				
				if (isUri) {
					URIExist = 1;
				}		
				
				//check whether the second or next element (if there are some answers) is a URI
				while((answerIt.hasNext()) && (URIExist == 0)) {
					//get the next element
					answer = answerIt.next().toLowerCase();
					if (dDao.isUmlaut(answer)) {
						answer = dDao.replaceUmlaut(answer);
					}
					for (String scheme: VALID_SCHEMES) {
						if (answer.contains(scheme)) {
							isUri= true;
							break;
						}
					}	
					if (isUri) {
						URIExist = 1;
						break;
					}
				}
				
				//check whether the only one answer is a URI or there is at least one of answers is URI
				if(URIExist == 1) {				
					return "resource";
				}			
				//check if all are date
				boolean isDate = validateDateFormat(answer.toString());
				while(answerIt.hasNext()&& (!isDate)) {
					answer = answerIt.next();
					if (validateDateFormat(answer.toString())) {
						isDate = true;
						break;
					}
				}
				
				if (isDate) {
					return "date";
				}
				//System.out.println("Answers in answerTypeChecking is "+answer);
				//check if it is a number
				if ((isNumeric(answer)) || (answer.matches("\\d.*"))) {
					System.out.println("Answers in answerTypeChecking is "+answer);
					return "number";
				}		
				
				//otherwise assume it is a string
				return "string";		
			}
		}catch (Exception e) {
			// TODO: handle exception
		}		
		//otherwise its empty
		return "undefined";
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
			//if ((sparqlQuery.toString().toLowerCase().contains("dbo:")) || (sparqlQuery.toString().toLowerCase().contains("http://dbpedia.org/ontology"))) && (!sparqlQuery.toString().toLowerCase().contains("http://dbpedia.org/ontology"))
			if (sparqlQuery.toString().toLowerCase().contains("yago"))
			{
				return ("false");
			}else
			{
				return ("true");
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
				cursor.close();
			}catch (Exception e) {}
			return false;			
		}
		
		// is a field curated during curation process?
		public Boolean isItemCuratedDuringCurationProcess (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			//searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLogTemp");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					return true;
				}
				cursor.close();
			}catch (Exception e) {}
			return false;			
		}
		
		//is keyword curated 
		public Boolean isKeywordCurated (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);
			
			String[] arrayString = new String[2];
			arrayString[0]="suggestion";
			arrayString[1]="translation";

			BasicDBObject searchWithOR= new BasicDBObject();
			searchWithOR.put("$in", arrayString);
			searchObj.put("logTypeKeyword", searchWithOR);
			
			BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("finishingTimeCuration", -1);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLog");
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
				while (cursor.hasNext()) {
					return true;
				}
				cursor.close();
			}catch (Exception e) {}
			return false;
		}
		
		//is keyword curated during curation process
		public Boolean isKeywordCuratedDuringCurationProcess (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLogTemp");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					return true;
				}
				cursor.close();
			}catch (Exception e) {}
			return false;
		}
		
		//is question curated 
		public Boolean isQuestionCurated (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curated");
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
				cursor.close();
			}catch (Exception e) {}
			return false;
		}
		
		//is question curated during curation process
		public Boolean isQuestionCuratedDuringCurationProcess (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curated");
			searchObj.put("logInfo.id", id);
			searchObj.put("logInfo.datasetVersion", datasetVersion);
			searchObj.put("logInfo.field", item);			
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection("UserLogTemp");
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					return true;
				}
				cursor.close();
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
				cursor.close();				
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
				cursor.close();				
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
					itemCS.setQald1(this.getAllDatasetsInParticularVersion(q.getId(), "QALD1_Train_dbpedia", "QALD1_Test_dbpedia").size());
					itemCS.setQald2(this.getAllDatasetsInParticularVersion(q.getId(), "QALD2_Train_dbpedia", "QALD2_Test_dbpedia").size());
					itemCS.setQald3(this.getAllDatasetsInParticularVersion(q.getId(), "QALD3_Train_dbpedia", "QALD3_Test_dbpedia").size());
					itemCS.setQald4(this.getAllDatasetsInParticularVersion(q.getId(), "QALD4_Train_Multilingual", "QALD4_Test_Multilingual").size());
					itemCS.setQald5(this.getAllDatasetsInParticularVersion(q.getId(), "QALD5_Train_Multilingual", "QALD5_Test_Multilingual").size());
					itemCS.setQald6(this.getAllDatasetsInParticularVersion(q.getId(), "QALD6_Train_Multilingual", "QALD6_Test_Multilingual").size());
					itemCS.setQald7(this.getAllDatasetsInParticularVersion(q.getId(), "QALD7_Train_Multilingual", "QALD7_Test_Multilingual").size());
					itemCS.setQald8(this.getAllDatasetsInParticularVersion(q.getId(), "QALD8_Train_Multilingual", "QALD8_Test_Multilingual").size());
					//itemCS.setNoCuratedQuestion(this.getNoCuratedQuestions(q.getId());
					csList.add(itemCS);					
				}
				cursor.close();
				return csList;
			 }catch (Exception e) {
				 
			 }
			return null;
		}
		
		//
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
		
	public boolean areTwoCuratedQuestionsSame (DatasetModel fD, DatasetModel sD) {		
		if (!(fD.getAggregation().equals(sD.getAggregation()))) {
			return false; 
		}else if (!(fD.getHybrid().equals(sD.getHybrid()))){
				return false;
			}else if (!(fD.getOnlydbo().equals(sD.getOnlydbo()))) {
					return false;
				} else if (!(fD.getOutOfScope().equals(sD.getOutOfScope()))) {
						return false;
					}else if (!(fD.getAnswerType().equals(sD.getAnswerType()))) {
						return false;
						}else if (!(fD.getLanguageToKeyword().equals(sD.getLanguageToKeyword()))) {
							return false;
							}else if (!(fD.getLanguageToQuestion().equals(sD.getLanguageToQuestion()))) {
								return false;
								}else if (!(fD.getSparqlQuery().equals(sD.getSparqlQuery()))) {
									return false;
								}
		return true;
	}
	
	//check whether two multilingual questions are same
	public boolean areTwoMultilingualQuestionsSame(Map<String, String> q1, Map<String, String> q2) {
		for (Map.Entry<String, String> entryQ1: q1.entrySet()) {
			for (Map.Entry<String, String> entryQ2: q2.entrySet()) {
				String key1 = entryQ1.getKey();
				String key2 = entryQ2.getKey();
				if (key1.equals(key2)) {
					if(entryQ1.getValue()!=null && entryQ2.getValue()!=null ) {
						if (!(entryQ1.getValue().equals(entryQ2.getValue()))){
							return false;
						}else {
							break;
						}						
					}else if (entryQ1.getValue() == null && entryQ2.getValue() == null ){
						break;
					}else {
						return false;
					}
				}
			}
		}
		return true;
	}
	
	//check whether two multilingual keywords are same
		public boolean areTwoMultilingualKeywordsSame(Map<String, List<String>> q1, Map<String, List<String>> q2) {		
			for (Map.Entry<String, List<String>> entryQ1: q1.entrySet()) {
				for (Map.Entry<String, List<String>> entryQ2: q2.entrySet()) {					
					if (entryQ1.getKey().equals(entryQ2.getKey())) {
						Collection list1 = new ArrayList(Arrays.asList(entryQ1.getValue()));
						Collection list2 = new ArrayList(Arrays.asList(entryQ2.getValue()));
						list1.retainAll(list2);
						if (list1.isEmpty()) {
							return false;
						}else {
							break;
						}
					}
				}
			}
			return true;
		}
	
		//check whether two list of keywords are same
				public boolean areTwoKeywordsListSame(List<String> q1, List<String> q2) {	
					List<String> result = new ArrayList<>();				
					Collection list1 = new ArrayList(Arrays.asList(q1));
					Collection list2 = new ArrayList(Arrays.asList(q2));
					list1.retainAll(list2);
					if (list1.isEmpty()) {
						return false;
					}				
					return true;
				}
	//check whether one of the variable is null
	public boolean isNullExist(String v1, String v2) {
		if (v1 != null && v2 != null) {
			return false;
		}else {
			return true;
		}
	}
	
	//check whether all of variables are null
		public String whichOneNotNullOrAllNull(String v1, String v2) {
			String result = "";
			if (v1 == null && v2 == null) {
				result = null;
			}else if (v1 != null) {
				result = v1;
			}else if (v2 != null) {
				result = v2;
			}
			return result;
		}
	
		//sort the hashMap base on keys
		public static <K extends Comparable,V extends Comparable> Map<K,V> sortByKeysQuestionTranslations(Map<K,V> map){
	        List<K> keys = new LinkedList<K>(map.keySet());
	        Collections.sort(keys);
	      
	        //LinkedHashMap will keep the keys in the order they are inserted
	        //which is currently sorted on natural ordering
	        Map<K,V> sortedMap = new LinkedHashMap<K,V>();
	        for(K key: keys){
	            sortedMap.put(key, map.get(key));
	        }      
	        return sortedMap;
	    }
		
	//sort the hashMap base on keys
	public static <K extends Comparable,V extends Comparable> Map<K,List<V>> sortByKeysKeywordsTranslations(Map<K,List<V>> map){
        List<K> keys = new LinkedList<K>(map.keySet());
        Collections.sort(keys);
      
        //LinkedHashMap will keep the keys in the order they are inserted
        //which is currently sorted on natural ordering
        Map<K,List<V>> sortedMap = new LinkedHashMap<K,List<V>>();
        for(K key: keys){
            sortedMap.put(key, map.get(key));
        }      
        return sortedMap;
    }
	
	public Map<String, String> proceedQuestionTranslations (QALD9TrainData el, DatasetModel dm){
		Map<String, String> newQuestionTranslations = new HashMap<String, String>();
		if (this.isNullExist(el.getLanguageToQuestion().get("de"), dm.getLanguageToQuestion().get("de")) == false) {
			if (!(el.getLanguageToQuestion().get("de").equals(dm.getLanguageToQuestion().get("de")))) {
				newQuestionTranslations.put("de_01", el.getLanguageToQuestion().get("de"));
				newQuestionTranslations.put("de_02", dm.getLanguageToQuestion().get("de"));
			}else {
				newQuestionTranslations.put("de", el.getLanguageToQuestion().get("de"));
			}
		}else {							
			newQuestionTranslations.put("de", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("de"), dm.getLanguageToQuestion().get("de")));
		}				
		if (this.isNullExist(el.getLanguageToQuestion().get("ru"), dm.getLanguageToQuestion().get("ru")) == false) {
			if (!(el.getLanguageToQuestion().get("ru").equals(dm.getLanguageToQuestion().get("ru")))) {
				newQuestionTranslations.put("ru_01", el.getLanguageToQuestion().get("ru"));
				newQuestionTranslations.put("ru_02", dm.getLanguageToQuestion().get("ru"));
			}else {
				newQuestionTranslations.put("ru", el.getLanguageToQuestion().get("ru"));							
			}
		}else {							
			newQuestionTranslations.put("ru", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("ru"), dm.getLanguageToQuestion().get("ru")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("pt"), dm.getLanguageToQuestion().get("pt")) == false) {
			if (!(el.getLanguageToQuestion().get("pt").equals(dm.getLanguageToQuestion().get("pt")))) {
				newQuestionTranslations.put("pt_01", el.getLanguageToQuestion().get("pt"));
				newQuestionTranslations.put("pt_02", dm.getLanguageToQuestion().get("pt"));
			}else {
				newQuestionTranslations.put("pt", el.getLanguageToQuestion().get("pt"));
			}
		}else {							
			newQuestionTranslations.put("pt", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("pt"), dm.getLanguageToQuestion().get("pt")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("hi_IN"), dm.getLanguageToQuestion().get("hi_IN")) == false) {
			if (!(el.getLanguageToQuestion().get("hi_IN").equals(dm.getLanguageToQuestion().get("hi_IN")))) {
				newQuestionTranslations.put("hi_IN_01", el.getLanguageToQuestion().get("hi_IN"));
				newQuestionTranslations.put("hi_IN_02", dm.getLanguageToQuestion().get("hi_IN"));
			}else {
				newQuestionTranslations.put("hi_IN", el.getLanguageToQuestion().get("hi_IN"));
			}
		}else {							
			newQuestionTranslations.put("hi_IN", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("hi_IN"), dm.getLanguageToQuestion().get("hi_IN")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("fa"), dm.getLanguageToQuestion().get("fa")) == false) {						
			if (!(el.getLanguageToQuestion().get("fa").equals(dm.getLanguageToQuestion().get("fa")))) {
				newQuestionTranslations.put("fa_01", el.getLanguageToQuestion().get("fa"));
				newQuestionTranslations.put("fa_02", dm.getLanguageToQuestion().get("fa"));
			}else {
				newQuestionTranslations.put("fa", el.getLanguageToQuestion().get("fa"));
			}
		}else {							
			newQuestionTranslations.put("fa", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("fa"), dm.getLanguageToQuestion().get("fa")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("it"), dm.getLanguageToQuestion().get("it")) == false) {
			if (!(el.getLanguageToQuestion().get("it").equals(dm.getLanguageToQuestion().get("it")))) {
				newQuestionTranslations.put("it_01", el.getLanguageToQuestion().get("it"));
				newQuestionTranslations.put("it_02", dm.getLanguageToQuestion().get("it"));
			}else {
				newQuestionTranslations.put("it", el.getLanguageToQuestion().get("it"));
			}
		}else {							
			newQuestionTranslations.put("it", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("it"), dm.getLanguageToQuestion().get("it")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("fr"), dm.getLanguageToQuestion().get("fr")) == false) {
			if (!(el.getLanguageToQuestion().get("fr").equals(dm.getLanguageToQuestion().get("fr")))) {
				newQuestionTranslations.put("fr_01", el.getLanguageToQuestion().get("fr"));
				newQuestionTranslations.put("fr_02", dm.getLanguageToQuestion().get("fr"));
			}else {
				newQuestionTranslations.put("fr", el.getLanguageToQuestion().get("fr"));
			}
		}else {							
			newQuestionTranslations.put("fr", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("fr"), dm.getLanguageToQuestion().get("fr")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("ro"), dm.getLanguageToQuestion().get("ro")) == false) {
			if (!(el.getLanguageToQuestion().get("ro").equals(dm.getLanguageToQuestion().get("ro")))) {
				newQuestionTranslations.put("ro_01", el.getLanguageToQuestion().get("ro"));
				newQuestionTranslations.put("ro_02", dm.getLanguageToQuestion().get("ro"));
			}else {
				newQuestionTranslations.put("ro", el.getLanguageToQuestion().get("ro"));
			}
		}else {							
			newQuestionTranslations.put("ro", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("ro"), dm.getLanguageToQuestion().get("ro")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("es"), dm.getLanguageToQuestion().get("es")) == false) {						
			if (!(el.getLanguageToQuestion().get("es").equals(dm.getLanguageToQuestion().get("es")))) {
				newQuestionTranslations.put("es_01", el.getLanguageToQuestion().get("es"));
				newQuestionTranslations.put("es_02", dm.getLanguageToQuestion().get("es"));
			}else {
				newQuestionTranslations.put("es", el.getLanguageToQuestion().get("es"));
			}
		}else {							
			newQuestionTranslations.put("es", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("es"), dm.getLanguageToQuestion().get("es")));
		}
		
		if (this.isNullExist(el.getLanguageToQuestion().get("nl"), dm.getLanguageToQuestion().get("nl")) == false) {
			if (!(el.getLanguageToQuestion().get("nl").equals(dm.getLanguageToQuestion().get("nl")))) {
				newQuestionTranslations.put("nl_01", el.getLanguageToQuestion().get("nl"));
				newQuestionTranslations.put("nl_02", dm.getLanguageToQuestion().get("nl"));
			}else {
				newQuestionTranslations.put("nl", el.getLanguageToQuestion().get("nl"));
			}
		}else {							
			newQuestionTranslations.put("nl", this.whichOneNotNullOrAllNull(el.getLanguageToQuestion().get("nl"), dm.getLanguageToQuestion().get("nl")));
		}
		return newQuestionTranslations;
	}
	
	public Map<String, List<String>> proceedKeywordsTranslations (QALD9TrainData el, DatasetModel dm){
		Map<String, List<String>> newKeywordsTranslations = new HashMap<String, List<String>>();
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("de"),dm.getLanguageToKeyword().get("de")))==false) {
			newKeywordsTranslations.put("de_01", el.getLanguageToKeywords().get("de"));
			newKeywordsTranslations.put("de_02", dm.getLanguageToKeyword().get("de"));
		}else {
			newKeywordsTranslations.put("de", el.getLanguageToKeywords().get("de"));
		}
		
	
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("ru"),dm.getLanguageToKeyword().get("ru")))==false) {
			newKeywordsTranslations.put("ru_01", el.getLanguageToKeywords().get("ru"));
			newKeywordsTranslations.put("ru_02", dm.getLanguageToKeyword().get("ru"));
		}else {
			newKeywordsTranslations.put("ru", el.getLanguageToKeywords().get("ru"));							
		}
	
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("pt"),dm.getLanguageToKeyword().get("pt")))==false) {
			newKeywordsTranslations.put("pt_01", el.getLanguageToKeywords().get("pt"));
			newKeywordsTranslations.put("pt_02", dm.getLanguageToKeyword().get("pt"));
		}else {
			newKeywordsTranslations.put("pt", el.getLanguageToKeywords().get("pt"));
		}
		
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("hi_IN"),dm.getLanguageToKeyword().get("hi_IN")))==false) {
			newKeywordsTranslations.put("hi_IN_01", el.getLanguageToKeywords().get("hi_IN"));
			newKeywordsTranslations.put("hi_IN_02", dm.getLanguageToKeyword().get("hi_IN"));
		}else {
			newKeywordsTranslations.put("hi_IN", el.getLanguageToKeywords().get("hi_IN"));
		}

							
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("fa"),dm.getLanguageToKeyword().get("fa")))==false) {
			newKeywordsTranslations.put("fa_01", el.getLanguageToKeywords().get("fa"));
			newKeywordsTranslations.put("fa_02", dm.getLanguageToKeyword().get("fa"));
		}else {
			newKeywordsTranslations.put("fa", el.getLanguageToKeywords().get("fa"));
		}
	
	
	
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("it"),dm.getLanguageToKeyword().get("it")))==false) {
			newKeywordsTranslations.put("it_01", el.getLanguageToKeywords().get("it"));
			newKeywordsTranslations.put("it_02", dm.getLanguageToKeyword().get("it"));
		}else {
			newKeywordsTranslations.put("it", el.getLanguageToKeywords().get("it"));
		}
	
	
	
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("fr"),dm.getLanguageToKeyword().get("fr")))==false) {
			newKeywordsTranslations.put("fr_01", el.getLanguageToKeywords().get("fr"));
			newKeywordsTranslations.put("fr_02", dm.getLanguageToKeyword().get("fr"));
		}else {
			newKeywordsTranslations.put("fr", el.getLanguageToKeywords().get("fr"));
		}
	
	
	
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("ro"),dm.getLanguageToKeyword().get("ro")))==false) {
			newKeywordsTranslations.put("ro_01", el.getLanguageToKeywords().get("ro"));
			newKeywordsTranslations.put("ro_02", dm.getLanguageToKeyword().get("ro"));
		}else {
			newKeywordsTranslations.put("ro", el.getLanguageToKeywords().get("ro"));
		}
	
													
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("es"),dm.getLanguageToKeyword().get("es")))==false) {
			newKeywordsTranslations.put("es_01", el.getLanguageToKeywords().get("es"));
			newKeywordsTranslations.put("es_02", dm.getLanguageToKeyword().get("es"));
		}else {
			newKeywordsTranslations.put("es", el.getLanguageToKeywords().get("es"));
		}
							
		if ((this.areTwoKeywordsListSame(el.getLanguageToKeywords().get("nl"),dm.getLanguageToKeyword().get("nl")))==false) {
			newKeywordsTranslations.put("nl_01", el.getLanguageToKeywords().get("nl"));
			newKeywordsTranslations.put("nl_02", dm.getLanguageToKeyword().get("nl"));
		}else {
			newKeywordsTranslations.put("nl", el.getLanguageToKeywords().get("nl"));
		}
		newKeywordsTranslations.put("en", el.getLanguageToKeywords().get("en"));
		return newKeywordsTranslations;
	}
	

	
}
