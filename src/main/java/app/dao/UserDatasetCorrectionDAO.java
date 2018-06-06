package app.dao;

import java.util.ArrayList;
import java.util.Collections;
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
					item.setLastRevision(q.getLastRevision());
					item.setTransId(q.getTransId());
					item.setStatus(q.getStatus());
				}
				return item;
		 } catch (Exception e) {}
		 return item;
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
		sortObj.put("id",1);
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
		newdbobj.put("status", document.getStatus());
		
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
	
	public Boolean isDocumentRemoved(int userId, String id, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", id);
		 searchObj.put("datasetVersion", datasetVersion);
		 searchObj.put("userId", userId);
		 searchObj.put("status", "false");
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
		public DatasetSuggestionModel implementCorrection(int userId, String id, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			 DatasetSuggestionModel item = new DatasetSuggestionModel();
			 searchObj.put("id", id);
			 try {
					DB db = MongoDBManager.getDB("QaldCuratorFiltered");
					DBCollection coll = db.getCollection(datasetVersion);
					DBCursor cursor = coll.find(searchObj);
					SparqlService ss = new SparqlService();	
					while (cursor.hasNext()) {
						DBObject dbobj = cursor.next();
						Gson gson = new GsonBuilder().create();
						DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);					 
						String answerType = q.getAnswerType();					
						
						boolean answerStatus=false;					
						String query = q.getSparqlQuery();					
						String languageToQuestionEn = q.getLanguageToQuestion().get("en").toString();
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
							System.out.println("The answers is "+answers);
							if (answers.isEmpty() || answers.equals(null)) {
								answerStatus = false;
							}else {
								for (String element:answers) {
									System.out.println("The answer is "+element);
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
								List<String> sparqlSuggestion = sparqlCorrection(query) ;
								ArrayList<String> listOfSuggestion = new ArrayList<>(sparqlSuggestion.size());
								listOfSuggestion.addAll(sparqlSuggestion);							
								Map<String,List<String>> sparqlAndAnswerList = new HashMap<String,List<String>>();							
								for (String element: listOfSuggestion) {
									if (element.contains("is missing")) {
										String newElement = element + ". This question should be removed from the dataset.";
										Collections.replaceAll(listOfSuggestion, element, newElement);	
										List<String> noAFCE = new ArrayList<String>();
										noAFCE.add("-");									
										sparqlAndAnswerList.put(newElement,noAFCE);
									}else {
										
										/** Retrieve answer from Virtuoso current endpoint **/
										Set<String> results = new HashSet();	
										List<String> resultList = new ArrayList<String>();
										if (ss.isASKQuery(languageToQuestionEn)) {
											String result = ss.getResultAskQuery(element);										
											resultList.add(result);														
										}else {				
											results = ss.getQuery(element);
											resultList.addAll(results);										
										}									
										sparqlAndAnswerList.put(element,resultList);
									}								
								}							
								item.setSparqlAndAnswerList(sparqlAndAnswerList);
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
						
						if (!AggregationChecking(query).equals(q.getAggregation().toString())) {
							item.setAggregationSugg(AggregationChecking(query));
						}
						
						String onlyDboValue = q.getOnlydbo().toString();
						if (!onlyDboChecking(query).equals(onlyDboValue)) {
							item.setOnlyDboSugg(onlyDboChecking(query));
						}
						
						String hybridValue = q.getHybrid().toString();
						if (!HybridChecking(query).equals(hybridValue)) {
							item.setHybridSugg(HybridChecking(query));
						}					
								
						String outOfScope = String.valueOf(q.getOutOfScope());					
						String oos = "12";					
						
						if (outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("false")) {
							oos ="true";
						}else if (outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("true")) {
							oos=null;
						}else if (outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("null")) {
							oos="false";
						}else if (outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("null")) {
							oos="true";
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
		// did item feature cureated?
		public Boolean isItemCurated (int userId, String id, String datasetVersion, String item) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("userId", userId);
			searchObj.put("logType", "curate");
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
					item.setLastRevision(q.getLastRevision());
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
					item.setLastRevision(q.getLastRevision());
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
		public List<String> sparqlCorrection (String sparqlQuery) throws ParseException {
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
