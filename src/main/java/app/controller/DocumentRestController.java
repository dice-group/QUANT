package app.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletResponse;

import org.apache.jena.sparql.engine.http.Params;
//import org.json.JSONArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.MatchOperation;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.davidsoergel.dsutils.Base64.OutputStream;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
//import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.jayway.jsonpath.Criteria;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.client.model.Accumulators;
import com.mongodb.client.model.Aggregates;
import com.mongodb.operation.GroupOperation;

import app.config.MongoDBManager;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.model.Dataset;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.DocumentList;
import app.model.ModifiedSparqlManualEvaluation;
import app.model.Question;
import app.model.UserLog;
import app.model.StatePopulation;
import app.model.User;
import app.model.UserDatasetCorrection;
import app.model.UserDatasetCorrectionTemp;
import app.response.BaseResponse;
import app.response.QuestionResponse;
import app.sparql.SparqlService;
import app.util.TranslatorService;
import java.net.URL;


@RestController
@RequestMapping(value= {"/document/datasets"}, produces="application/json; charset=UTF-8")
public class DocumentRestController {
	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	@RequestMapping (value = "getAllDatasets/{userName}/{role}", method = RequestMethod.GET)
	public List<DocumentList> getAllDatasets(@PathVariable("userName") String userName, @PathVariable("role") String role) {		 
		 List<DocumentList> tasks = new ArrayList<DocumentList>();
		 	Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id",1);
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			UserDAO userDao = new UserDAO();
			User user = userDao.getUserByUsername(userName);
			
			if ((role.equals("administrator")) || (role.equals("evaluator"))){
				for (int x=0; x<listDataset.size(); x++) {
					try {
						//call mongoDb
						DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
						DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
						DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
						while (cursor.hasNext()) {
							DBObject dbobj = cursor.next();
							Gson gson = new GsonBuilder().create();
							DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
							DocumentList item = new DocumentList();
							item.setDatasetVersion(listDataset.get(x).getName());
							item.setId(q.getId());
							item.setQuestion(q.getLanguageToQuestion().get("en").toString());	
							item.setKeywords(q.getLanguageToKeyword());
							
							tasks.add(item);
						}	
						cursor.close();
					} catch (Exception e) {}
				}
			}else if (role.equals("annotator")){
				String collectionName = "";
				if ((userName.equals("annotator1")) || (userName.equals("annotator2"))) {
					collectionName = "Dataset9";
				}else if ((userName.equals("annotator3")) || (userName.equals("annotator4"))) {
					collectionName = "Dataset1";
					}else if ((userName.equals("annotator5")) || (userName.equals("annotator6"))) {
						collectionName = "Dataset3";
						} else if ((userName.equals("annotator7")) || (userName.equals("annotator8"))) {
							collectionName = "Dataset5";
							} else if ((userName.equals("annotator9")) || (userName.equals("annotator10"))) {
								collectionName = "Dataset7";
								} 	
					try {
						//call mongoDb
						DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
						DBCollection coll = db.getCollection(collectionName); //Collection
						DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
						while (cursor.hasNext()) {
							DBObject dbobj = cursor.next();
							Gson gson = new GsonBuilder().create();
							DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
							DocumentList item = new DocumentList();
							item.setDatasetVersion(q.getDatasetVersion());
							item.setId(q.getId());
							item.setQuestion(q.getLanguageToQuestion().get("en").toString());	
							item.setKeywords(q.getLanguageToKeyword());
							
							tasks.add(item);
						}	
						cursor.close();
					} catch (Exception e) {}
			}	 
			return tasks;
		}
	
	@RequestMapping (value = "getRemovedQuestionInEvaluationPerUser/{userId}/{questionStatus}", method = RequestMethod.GET)
	public List<DatasetModel> getRemovedQuestionInEvaluationPerUser(@PathVariable("userId") int userId, @PathVariable("questionStatus") String questionStatus) {
		List<DatasetModel> result = new ArrayList<DatasetModel>();
		//int userId = 9;
		//String status = "removed";
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);
		searchObj.put("status", questionStatus);	
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				result.add(q);
			}
			return result;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	@RequestMapping (value = "doesNeedKeywordsTranslations", method = RequestMethod.GET)
	public boolean doesNeedKeywordsTranslations () {
		String id = "195";
		String datasetVersion = "QALD8_Train_Multilingual";
		String question = "Who is the mayor of Rotterdam?";
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("languageToQuestion.en", question);
		searchObj.put("id", id);		
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection(datasetVersion); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				if ((q.getLanguageToKeyword().size() == 11)) {
					return false;
				}			
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
		}
		return true;
		}
	
	@RequestMapping (value = "hasQuestionBeenTranslated", method = RequestMethod.GET)
	public boolean hasQuestionBeenTranslated () {
		int userId = 7;
		String id = "177";
		String datasetVersion = "QALD8_Train_Multilingual";
		String startingTime = "20180731111608";
		String finishingTime = "20180731111624";
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
			while (cursor.hasNext()) {				
				return true;				
			}
			cursor.close();
		}catch (Exception e) {
				//TODO: handle exception
		}
		return false;
	}
	
	@RequestMapping (value = "getRestOfQuestionTranslation", method = RequestMethod.GET)
	public Map<String, String> getRestOfQuestionTranslation () {
		BasicDBObject searchObj = new BasicDBObject();
		int userId = 7;
		String id = "177";
		String datasetVersion = "QALD8_Train_Multilingual";
		String dbName = "UserDatasetCorrectionTemp";
		String question = "In which countries do people speak Japanese?";
		String startingTime = "";
		String finishingTime = "";
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
	
	@RequestMapping (value = "getKeywordsTranslationSuggestion", method = RequestMethod.GET)
	public Map<String, List<String>> getKeywordsTranslationSuggestion(){		
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel translations= documentDao.getQuestionTranslations("177", "QALD8_Train_Multilingual", "In which countries do people speak Japanese?");
		//remove english translations from suggestion
		Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
		for (Map.Entry<String, List<String>> mapEntry : translations.getLanguageToKeyword().entrySet()) {
			if (!(mapEntry.getKey().equals("en"))) {
				keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
			}						
		}
		return keywordsTranslations;
	}
	
	//function to provide dataset for each user in generating QALD9 dataset
	@RequestMapping (value = "provideDatasetForGeneratingQALD9", method = RequestMethod.GET)
	public List<DatasetModel> provideDatasetForGeneratingQALD9(){
		List<DatasetModel> allQuestion = new ArrayList<DatasetModel>();
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("AllQuestions");
			DBCursor cursor = coll.find();	
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				allQuestion.add(q);
			}
			cursor.close();
			List<DatasetModel> dataset = new ArrayList<DatasetModel>();
			int number = 1;			
			for (DatasetModel element : allQuestion) {
				if (number > 520) {
					if (number < 654) {
						dataset.add(element);						
					}else {
						break;
					}
				}
				number = number + 1;				
			}
			return dataset;
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	
	
	//function to check answer type
	@RequestMapping (value = "answerTypeChecking1", method = RequestMethod.GET)
	public String answerTypeChecking1 () {
		UserDatasetCorrectionDAO udc = new UserDatasetCorrectionDAO();
		Set<String> answers = new HashSet<>();		
		answers.add("http://dbpedia.org/resource/École_Spéciale_d'Architecture");
		final String REGEX_URI = "^(\\w+):(\\/\\/)?[-a-zA-Z0-9+&@#()\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#()\\/%=~'_|]";
		DocumentDAO dDAO = new DocumentDAO();
		
		if(answers.size()>=1) {
			System.out.println("Answer size is "+answers.size());
			Iterator<String> answerIt = answers.iterator();
			String answer = answerIt.next();
			System.out.println("answer is "+answer);			
			if (dDAO.isUmlaut(answer)) {				
				answer = dDAO.replaceUmlaut(answer);
			}		
			int URIExist = 0;
			boolean isUri=answer.matches(REGEX_URI);
			if (isUri) {
				URIExist = 1;
			}
			System.out.println("isURI is "+isUri);
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
			boolean isDate = udc.validateDateFormat(answer.toString());
			while(answerIt.hasNext()&& isDate) {
				answer = answerIt.next();
				isUri &= udc.validateDateFormat(answer.toString());
			}
			if (isDate) {
				return "date";
			}
			//check if it is a number
			if ((udc.isNumeric(answer)) || (answer.matches("\\d.*"))) {
				return "number";
			}				
			//otherwise assume it is a string
			return "string";		
		}
		
		//otherwise its empty
		return "";
	}
	
	//function to apply implementation
	@RequestMapping (value = "implementCorrection", method = RequestMethod.GET)
	public DatasetSuggestionModel implementCorrection() {
		String id = "17";
		String datasetVersion = "QALD1_Train_dbpedia";
		String curatedStatus = "not curated";
		int userId = 2;
		DocumentDAO docDaoObj = new DocumentDAO();
	 BasicDBObject searchObj = new BasicDBObject();
	 DatasetSuggestionModel item = new DatasetSuggestionModel();
	 BasicDBObject sortObj = new BasicDBObject();
	 searchObj.put("id", id); 
	 String dbName = "";
	 if (curatedStatus.equals("curated")) {
			searchObj.put("datasetVersion", datasetVersion);
			searchObj.put("userId", userId);
			searchObj.put("status", "curated");					
			sortObj.put("revision", -1);
			dbName = "UserDatasetCorrection";
	}else if (curatedStatus.equals("in curation")){ 
			searchObj.put("datasetVersion", datasetVersion);
			searchObj.put("userId", userId);	
			sortObj.put("id", -1);
			dbName = "UserDatasetCorrectionTemp";
	}else if (curatedStatus.equals("not curated")){ 
			dbName = datasetVersion;
			sortObj.put("id", -1);
	}
	 String query = "";
	try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection(dbName);
			DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);
			SparqlService ss = new SparqlService();	
			
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
										
				item.setQuery(query);
				boolean answerStatus=false;				
				if (ss.isASKQuery(languageToQuestionEn)) {
					String answer = ss.getResultAskQuery(query);
					if (answer.equals(null)) {
						answerStatus = false; 
					}else {
						answerStatus = true;
						if (!answerType.equals(docDaoObj.booleanAnswerTypeChecking(answer)) || (answerType.equals(null))) {						
							item.setAnswerTypeSugg(docDaoObj.booleanAnswerTypeChecking(answer));						
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
						if (!(answerType.equals(docDaoObj.answerTypeChecking(answers))) || (answerType.equals(""))) {	//					
							item.setAnswerTypeSugg(docDaoObj.answerTypeChecking(answers));								
						}
					}					
				}
				
				System.out.println("Answer Status: "+answerStatus);									
				//item.setResultStatus(query);
				//check whether it needs to provide SPARQL suggestion
				if (!answerStatus) {					
					try {
						Map<String, List<String>> sparqlSuggestion = docDaoObj.sparqlCorrection(query);
						/*System.out.println("Sparql Query "+query);
						System.out.println("Sparql Suggestion "+sparqlSuggestion);*/
						ArrayList<String> listOfSuggestion = new ArrayList<String>();
						Map<String,List<String>> sparqlAndCaseList = new HashMap<String,List<String>>();
						Set<String> answerList = new HashSet<>();
						
						if (sparqlSuggestion.size() == 0) {
							item.setSparqlCorrectionStatus(false);
						}else {								
							item.setSparqlCorrectionStatus(true);
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
						//item.setResultStatus(String.valueOf(answerStatus));
						// TODO: handle exception
					}												
				}
				
				//Check whether it needs to display button View Suggestion
				if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("false")) {
					//System.out.println("Result status is this: false");
					item.setResultStatus("false");
				}else {
					//System.out.println("Result status is this: true");
					item.setResultStatus("true");
				}
				
				
				if (!(docDaoObj.AggregationChecking(query).equals(aggregation))) {
					item.setAggregationSugg(docDaoObj.AggregationChecking(query));
				}				
				
				if (!(docDaoObj.onlyDboChecking(query).equals(onlyDboValue))) {
					item.setOnlyDboSugg(docDaoObj.onlyDboChecking(query));
				}
									
				if (!(docDaoObj.HybridChecking(query).equals(hybridValue))) {
					item.setHybridSugg(docDaoObj.HybridChecking(query));
				}					
									
				String oos = "";					
				
				if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("false")) {
					oos ="true";
				}else if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("true")) {
					oos=null;
				}else if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("null")) {
					oos="false";
				}else if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("false") && outOfScope.equals("null")) {
					oos="true";
				}else if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("true")) {
					oos="false";
				}else if (docDaoObj.outOfScopeChecking(query, languageToQuestionEn).equals("true") && outOfScope.equals("false")) {
					oos=null;
				}				
				item.setOutOfScopeSugg(oos);
				//System.out.println("OOS value is "+oos);
			}	
			cursor.close();
	 } catch (Exception e) { e.printStackTrace(); }
	 return item;
}
	//function to get question translations
	@RequestMapping(value = "getQuestionTranslations", method = RequestMethod.GET)
	public DatasetModel getQuestionTranslations () {
		String id= "194";
		String datasetVersion ="QALD8_Train_Multilingual";
		String question = "Is Pluto really a planet?";
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);
		searchObj.put("languageToQuestion.en", question);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("AllTranslations"); //Collection
			DBCursor cursor = coll.find(searchObj).limit(1);
			if (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				//return q;
				//return 1;
			}else {
				DatasetModel result = new DatasetModel();
				DBCollection coll1 = db.getCollection("AddedTranslations"); 
				DBCursor cursor1 = coll1.find(searchObj).limit(1);
				while (cursor1.hasNext()) {
					DBObject dbobj1 = cursor1.next();
					Gson gson1 = new GsonBuilder().create();
					DatasetModel q1 = gson1.fromJson(dbobj1.toString(), DatasetModel.class);
					
					//check whether number of suggestion is 11 (all translations). It should only for those that missed
					if (q1.getLanguageToQuestion().size() > 10) {
						//get key of all suggestion for question
						List<String> keyAllSuggQ = new ArrayList<String>();
						for (Map.Entry<String,String> allQTSug : q1.getLanguageToQuestion().entrySet()) {
							keyAllSuggQ.add(allQTSug.getKey());
						}
						
						//get key of all suggestion for keywords
						List<String> keyAllSuggK = new ArrayList<String>();
						for (Map.Entry<String,List<String>> allKTSug : q1.getLanguageToKeyword().entrySet()) {
							keyAllSuggK.add(allKTSug.getKey());
						}
						
						//get existing translations
						String dbVersion = q1.getDatasetVersion();
						DBCollection coll2 = db.getCollection(datasetVersion); 
						DBCursor cursor2 = coll2.find(searchObj);
						List<String> keyExistingQ = new ArrayList<String>();
						List<String> keyExistingK = new ArrayList<String>();
						
						while (cursor2.hasNext()) {
							DBObject dbobj2 = cursor2.next();
							Gson gson2 = new GsonBuilder().create();
							DatasetModel q2 = gson2.fromJson(dbobj2.toString(), DatasetModel.class);
							//get Question Key
							for (Map.Entry<String, String> existQ: q2.getLanguageToQuestion().entrySet()) {
								keyExistingQ.add(existQ.getKey());
							}
							//Get Keywords Key
							for (Map.Entry<String, List<String>> existK: q2.getLanguageToKeyword().entrySet()) {
								keyExistingK.add(existK.getKey());
							}	
							return q2;
						}
						
						//get missed translations for questions
						Map<String, String> finalSuggQ = new HashMap<String, String>();
						for (String aQ: keyAllSuggQ) {
							if (!(keyExistingQ.contains(aQ))) {
								//put into final suggestion
								for (Map.Entry<String, String> mapEQ: q1.getLanguageToQuestion().entrySet()) {
									if (mapEQ.getKey().equals(aQ)) {
										finalSuggQ.put(mapEQ.getKey(), mapEQ.getValue());
										break;
									}
								}
							}
						}
						
						//get missed translations for keywords
						Map<String, List<String>> finalSuggK = new HashMap<String, List<String>>();
						for (String aK: keyAllSuggK) {
							if (!(keyExistingK.contains(aK))) {
								//put into final suggestion
								for (Map.Entry<String, List<String>> mapEK: q1.getLanguageToKeyword().entrySet()) {
									if (mapEK.getKey().equals(aK)) {
										finalSuggK.put(mapEK.getKey(), mapEK.getValue());
										break;
									}
								}
							}
						}					
					result.setLanguageToKeyword(finalSuggK);
					result.setLanguageToQuestion(finalSuggQ);	
					
					}
					return q1;					
				}
				cursor1.close();
				//return 2;
			}			
		}catch (Exception e) {
				// TODO: handle exception
			}
		return null;		
	}
	
	//function to chech answer Type
	@RequestMapping(value = "answerTypeChecking", method = RequestMethod.GET)
	public int answerTypeChecking () {
		Set<String> answers = new HashSet<String>();
		answers.add("New Jersey");
		answers.add("http://dbpedia.org/resource/New_Jersey");
		DocumentDAO docDao = new DocumentDAO();
		final String REGEX_URI = "^(\\w+):(\\/\\/)?[-a-zA-Z0-9+&@#()\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#()\\/%=~_|]";
		try {
			if(answers.size()>=1) {
				
				Iterator<String> answerIt = answers.iterator();

				//the first element
				String answer = answerIt.next();
				//System.out.println("answer is "+answer);			
				if (docDao.isUmlaut(answer)) {				
					answer = docDao.replaceUmlaut(answer);
				}	
				
				//check whether the first element or the only one element is a URI
				int URIExist = 0;
				boolean isUri=answer.matches(REGEX_URI);
				if (isUri) {
					URIExist = 1;
				}
				
				//System.out.println("isURI is "+isUri);
				//check whether the second or next element (if there are some answers) is a URI
				while((answerIt.hasNext()) && (URIExist == 0)) {
					//get the next element
					answer = answerIt.next().toLowerCase();
					if (docDao.isUmlaut(answer)) {
						answer = docDao.replaceUmlaut(answer);
					}
					isUri=answer.matches(REGEX_URI);
					if (isUri) {
						URIExist = 1;
						break;
					}
				}
				
				//check whether the only one answer is a URI or there is at least one of answers is URI
				if(URIExist == 1) {				
					//return "resource";
				}			
				//check if all are date
				boolean isDate = docDao.validateDateFormat(answer.toString());
				while(answerIt.hasNext()&& (!isDate)) {
					answer = answerIt.next();
					if (docDao.validateDateFormat(answer.toString())) {
						isDate = true;
						break;
					}
				}
				
				if (isDate) {
					//return "date";
				}
				//System.out.println("Answers in answerTypeChecking is "+answer);
				//check if it is a number
				if ((docDao.isNumeric(answer)) || (answer.matches("\\d.*"))) {
					System.out.println("Answers in answerTypeChecking is "+answer);
					//return "number";
				}		
				
				//otherwise assume it is a string
				//return "string";		
			}
		}catch (Exception e) {
			// TODO: handle exception
		}		
		//otherwise its empty
		//return "undefined";
		return answers.size();
	}
	
	//function to get all curation result
	@RequestMapping (value = "getAllCurationResult", method = RequestMethod.GET)
	public List<UserDatasetCorrection> getAllCurationResult() {
		int userId = 2;
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();		
		BasicDBObject searchObj1 = new BasicDBObject();
		BasicDBObject matchObj = new BasicDBObject();	
		BasicDBObject searchObj2 = new BasicDBObject();
		BasicDBObject searchObj3 = new BasicDBObject();
		BasicDBObject dataObj = new BasicDBObject();
		dataObj.put("id", "$id");
		dataObj.put("datasetVersion", "$datasetVersion");
		dataObj.put("languageToQuestion", "$languageToQuestion");
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
					UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
					UserDatasetCorrection q = gson.fromJson(dbobj.get("_id").toString(), UserDatasetCorrection.class);				
					UserDatasetCorrection data = udcDao.getDocument(userId, q.getId(), q.getDatasetVersion());
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
					item.setStartingTimeCuration(data.getStartingTimeCuration());
					item.setFinishingTimeCuration(data.getFinishingTimeCuration());
					item.setRevision(data.getRevision());
					item.setStatus(data.getStatus());
					tasks.add(item);
				}	
				cursor.close();
			} catch (Exception e) {}
			return tasks;
	}
	//function to get rest of question translations. dbName1 is database name where the chosen translated keywords are stored in.
	/*@RequestMapping (value = "getRestOfQuestionTranslation", method = RequestMethod.GET)
	public Map<String, String> getRestOfQuestionTranslation () {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", "1");
		searchObj.put("datasetVersion", "QALD1_Test_dbpedia");		
		searchObj.put("userId", 2);
		String startingTime = "";
		String finishingTime = "";
		String dbName = "UserDatasetCorrectionTemp";
		String question = "What is the revenue of IBM?";
		String datasetVersion = "QALD1_Test_dbpedia";
		String id = "1";
		List<String> chosenQuestionTranslationKeyList = new ArrayList<String>();		
		List<String> allQuestionTranslationKeyList = new ArrayList<String>();
		List<String> QuestionTranslationListKeyLeft = new ArrayList<String>();		
		
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
	}*/
			
	//function to test getting the rest of keywords translations (those that not been taken yet)
	@RequestMapping (value = "getNotChosenKeywordsTranslations", method = RequestMethod.GET)
	public List<Map<String, List<String>>> getRestOfKeywordsTranslation () {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", "10");
		searchObj.put("datasetVersion", "QALD1_Test_dbpedia");		
		searchObj.put("userId", 2);
		String dbName1 = "UserDatasetCorrectionTemp";
		String dbName2 = "keywordsSuggestionsTranslations";
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
			searchObj2.put("id", "10");
			searchObj2.put("datasetVersion", "QALD1_Test_dbpedia");
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
			List<Map<String, List<String>>> result = new ArrayList<>();
			for (String element: allKeywordsTranslationKeyList) {
				if (!(chosenKeywordsTranslationKeyList.contains(element))) {
					//get complete keywords translations (with list of keywords)
					for (int i = 0; i < allKeywordsTranslations.size(); i++) {						
						for (Map.Entry<String, List<String>> entry: allKeywordsTranslations.get(i).entrySet()) {
					        if (element == (entry.getKey())) {
					        	Map<String,List<String>> data = new HashMap<String,List<String>>();
					        	data.put(element, entry.getValue());
					        	result.add(data);
					        	break;
					        }				           
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
	
	//Function to test counting an object size
	@RequestMapping (value = "countKeywordsElementNumber", method = RequestMethod.GET)
	public boolean countKeywordsElementNumber () {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("languageToQuestion.en", "What is the highest place of Karakoram?");
		searchObj.put("id", "8");	
		int number = -1;
		Map<String, List<String>> size = new HashMap<String, List<String>>();
		List<String> theString = new ArrayList<String>();
		theString.add("test");
		size.put("en", theString);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("QALD3_Train_dbpedia"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				DatasetModel item = new DatasetModel();								
				number = 2;
				if ((q.getLanguageToKeyword().size() == 11)) {
					return false;
				}
				//return q.getLanguageToKeyword();
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
		}
		return true;
		}
	
	//This function will get a distinct curated questions based on revision value. Therefore, only the latest curation result will be displayed
	@RequestMapping (value = "getDistinctCuratedQuestions", method = RequestMethod.GET)	
	public List<UserDatasetCorrection> getDistinctCuratedQuestions(int userId) {
		List<UserDatasetCorrection> tasks = new ArrayList<UserDatasetCorrection>();
		/*BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("userId", userId);
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);*/
		BasicDBObject searchObj1 = new BasicDBObject();
		BasicDBObject searchObj2 = new BasicDBObject();
		BasicDBObject searchObj3 = new BasicDBObject();
		searchObj2.put("_id", "$id");
		searchObj3.put("$max", "$revision");
		searchObj2.put("latestData", searchObj3);
		searchObj1.put("$group", searchObj2);
	
			try {
				//call mongoDb
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
					
					UserDatasetCorrection q = gson.fromJson(dbobj.toString(), UserDatasetCorrection.class);
					UserDatasetCorrection item = new UserDatasetCorrection();
					item.setDatasetVersion(q.getDatasetVersion());
					item.setUserId(userId);
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
				cursor.close();
			} catch (Exception e) {}
			return tasks;
	}
	
	//This function is used to get list of question that do not have keywords. Function generateKeywords will be applied on them. 
	//List of question with new keywords will be produced in order to get the translations using TranslatorServise.java
	//It is only involving dataset from QALD1 till QALD3 for evaluation purpose
	@RequestMapping (value= "getListQuestionForGenerateKeywordsTranslations", method = RequestMethod.GET)
	public List<DatasetModel> getListQuestionForGenerateKeywordsTranslations(){
		List<DatasetModel> questionList = new ArrayList<DatasetModel>();
		Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	
	 	BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);
		DocumentDAO theObj = new DocumentDAO();
		for (int x=0; x<listDataset.size(); x++) {
    		try {   			
    			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
    			DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
    			DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
    			while (cursor.hasNext()) {    				
    				DBObject dbobj = cursor.next();
    				Gson gson = new GsonBuilder().create();
    				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
    				if (q.getLanguageToKeyword().isEmpty()) {
    					List<String> keywords = theObj.generateKeywords(q.getLanguageToQuestion().get("en"));
    					Map<String, List<String>> keywordsObj = new HashMap<String, List<String>>();
    					keywordsObj.put("en", keywords);    					
    					q.setLanguageToKeyword(keywordsObj);
    					q.setDatasetVersion(listDataset.get(x).getName());
        				questionList.add(q);
    				}   				
    				
    			}   
    			cursor.close();
    		}catch (Exception e) {
				// TODO: handle exception
			}    		
		}
		return questionList;
	}
	
	
	@RequestMapping (value= "/combineAllQuestions", method = RequestMethod.GET)
	public List<DatasetModel> combineAllQuestions(){
		List<DatasetModel> questionList = new ArrayList<DatasetModel>();
		BaseResponse response = new BaseResponse();
		
		Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	
	 	BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);
		
		
		for (int x=0; x<listDataset.size(); x++) {
    		try {   			
    			DB db = MongoDBManager.getDB("MasterDatasetForEvaluation"); //Database Name
    			DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
    			DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
    			while (cursor.hasNext()) {    				
    				DBObject dbobj = cursor.next();
    				Gson gson = new GsonBuilder().create();
    				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
    				q.setDatasetVersion(listDataset.get(x).getName());
    				questionList.add(q);
    			} 
    			cursor.close();
    		}catch (Exception e) {
				// TODO: handle exception
			}    		
		}
		return questionList;
	}
	
	//Get sparql queries that have been modified in manual evaluation
	@RequestMapping(value= "/getModifiedQueries", method= RequestMethod.GET)
	public List<ModifiedSparqlManualEvaluation> getModifiedQueries() throws FileNotFoundException, IOException, ParseException{
		BaseResponse response = new BaseResponse();
		List<ModifiedSparqlManualEvaluation> listSparql = new ArrayList<ModifiedSparqlManualEvaluation>();
		
		JSONParser parser = new JSONParser();			
		JSONArray inFile = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream("C:/Users/riagu/Documents/Zafar/QALD1.json"), "UTF8"));
		
		for (Object o: inFile) {
			JSONObject obj = (JSONObject) o;
			Gson gson = new GsonBuilder().create();
			ModifiedSparqlManualEvaluation mS = gson.fromJson(obj.toString(), ModifiedSparqlManualEvaluation.class);
			ModifiedSparqlManualEvaluation data = new ModifiedSparqlManualEvaluation();
			data.setSparqlQuery(mS.getSparqlQuery());
			data.setSparqlQuery_c(mS.getSparqlQuery_c());
			listSparql.add(data);
		}
		return listSparql;
	}
	
	//call translation service class and get connected to translation shell on the server
	@RequestMapping (value="/generateKeywordsTranslations", method= RequestMethod.POST)
	public Map<String, List<String>> generateKeywordsTranslations(@RequestParam List<String> keywords, final HttpServletResponse response) throws FileNotFoundException
	{
		// CORS to allow for communication between https and http
		response.setHeader("Access-Control-Allow-Origin", "*");
		response.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE");
		response.setHeader("Access-Control-Max-Age", "3600");
		response.setHeader("Access-Control-Allow-Headers", "x-requested-with");
		TranslatorService ts = new TranslatorService();
		Map<String, List<String>> keywordsList = ts.translateNewKeywords(keywords);
		return keywordsList;
	}
		
	
	//prepare a file that contains all question without any translations completed with the translation results
	@RequestMapping(value= "/updateAllTranslationsResult", method= RequestMethod.GET)
	public List<DatasetModel> updateAllTranslationsResult() throws FileNotFoundException, IOException, ParseException {
		BaseResponse response = new BaseResponse();
		List<Question> tasks = new ArrayList<Question>();
		
		//read all translations from json file
		String json;
		JSONParser parser = new JSONParser();
		JSONArray results = new JSONArray();		
		JSONArray inFile = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream("C:/Users/riagu/Documents/newTranslations/16July2018/allTranslations.json"),"UTF8"));

		for (Object o: inFile) {
			JSONObject obj = (JSONObject) o;
			Gson gson = new GsonBuilder().create();
			Question q = gson.fromJson(obj.toString(), Question.class);
			Question task = new Question();
			task.setId(q.getId());
			//clean the english question			
			task.setLanguageToQuestion(q.getLanguageToQuestion());
			if (q.getLanguageToKeyword().isEmpty()) {
				String englishQuestion = q.getLanguageToQuestion().get("en");
				if (englishQuestion.startsWith("'")) {
					englishQuestion = englishQuestion.replaceAll("^\'", "");
				}					
				//remove dot character
				if (englishQuestion.endsWith(".")) {
					englishQuestion = englishQuestion.substring(0, englishQuestion.lastIndexOf("."));
					
				}	
				//replace many spaces with single space
				englishQuestion = englishQuestion.replaceAll("\\s+", " ");
				
				englishQuestion = englishQuestion.toLowerCase();
				if (englishQuestion.endsWith(" ")) {
					englishQuestion = englishQuestion.replaceAll("\\s$", "");
				}
				
				if (englishQuestion.endsWith("'")) {
					englishQuestion = englishQuestion.replaceAll("\'$", "");
				}
				
				if (englishQuestion.endsWith("?")) {
					englishQuestion = englishQuestion.replaceAll("\\?$", "");
				}				
							
				DocumentDAO dd = new DocumentDAO();
				List<String> keywords = dd.generateKeywords(englishQuestion);
				Map<String, List<String>> keywordsObj = new HashMap<String, List<String>>();
				keywordsObj.put("en", keywords);
				task.setLanguageToKeyword(keywordsObj);				 		
			}else {
				task.setLanguageToKeyword(q.getLanguageToKeyword());
			}									
            tasks.add(task);
		}
		
		//Select question from MongoDB based on question from file. If its found, update the translation results
		Dataset dataset = new Dataset();
		List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		BasicDBObject questionFromDBObj = new BasicDBObject();	
		List<DatasetModel> ListDM = new ArrayList<DatasetModel>();
		List<String> listQuestion = new ArrayList<String>();		
			for (Question q : tasks) {				
				try{
					DB db = MongoDBManager.getDB("QaldCuratorFiltered");					
					DBCollection coll = db.getCollection("AllQuestions");
					BasicDBObject questionObj = new BasicDBObject();
					
					String question = q.getLanguageToQuestion().get("en");					
					String englishQuestionWithMoreTranslations = q.getLanguageToQuestion().get("en");									
					questionObj.put("languageToQuestion.en", englishQuestionWithMoreTranslations);				
					DBCursor cursor = coll.find(questionObj);						
					while (cursor.hasNext()) {  
						Map<String, String> map = q.getLanguageToQuestion();
						Map<String, String> newMap = new HashMap<String, String>();
						for (Map.Entry<String, String> entry : map.entrySet())
						{
							if (entry.getKey().equals("en")) {
								newMap.put(entry.getKey(),englishQuestionWithMoreTranslations);
							}else {
								newMap.put(entry.getKey(),entry.getValue());
							}						    
						}
						DBObject dbobj = cursor.next();
	    				Gson gson = new GsonBuilder().create();
	    				DatasetModel result = gson.fromJson(dbobj.toString(), DatasetModel.class);
	    				DatasetModel questionItem = new DatasetModel();
	    				questionItem.setId(result.getId());
	    				questionItem.setDatasetVersion(result.getDatasetVersion());	    				
	    				questionItem.setLanguageToQuestion(newMap);
	    				questionItem.setLanguageToKeyword(q.getLanguageToKeyword());    				    				
						ListDM.add(questionItem);
					}	
					cursor.close();
				}catch (Exception e) {}
			}			
		return ListDM;
	}
	
	
	@RequestMapping(value= "/updateAddedTranslationsResult", method= RequestMethod.GET)
	public List<DatasetModel> updateAddedTranslationsResult() throws FileNotFoundException, IOException, ParseException {
		BaseResponse response = new BaseResponse();
		List<Question> tasks = new ArrayList<Question>();
		
		//read all translations from json file
		
		String json;
		JSONParser parser = new JSONParser();
		JSONArray results = new JSONArray();		
		JSONArray inFile = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream("C:/Users/riagu/Documents/newTranslations/16July2018/addedTranslations.json"),"UTF8"));

		for (Object o: inFile) {
			JSONObject obj = (JSONObject) o;
			Gson gson = new GsonBuilder().create();
			Question q = gson.fromJson(obj.toString(), Question.class);
			Question task = new Question();
			task.setId(q.getId());
			task.setLanguageToQuestion(q.getLanguageToQuestion());
			task.setLanguageToKeyword(q.getLanguageToKeyword());						
            tasks.add(task);
		}
		
		//Select question from MongoDB based on question from file. If its found, update the translation results
		Dataset dataset = new Dataset();
		List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		BasicDBObject questionFromDBObj = new BasicDBObject();	
		List<DatasetModel> ListDM = new ArrayList<DatasetModel>();
		List<String> listQuestion = new ArrayList<String>();		
			for (Question q : tasks) {				
				try{
					DB db = MongoDBManager.getDB("QaldCuratorFiltered");					
					DBCollection coll = db.getCollection("AllQuestions");
					BasicDBObject questionObj = new BasicDBObject();
					
					String question = q.getLanguageToQuestion().get("en");					
					String englishQuestionWithMoreTranslations = q.getLanguageToQuestion().get("en").replaceAll("^\'", "");
					englishQuestionWithMoreTranslations = englishQuestionWithMoreTranslations.replaceAll("\\s+$", "");
					englishQuestionWithMoreTranslations = englishQuestionWithMoreTranslations.replaceAll("\'$", "");					
					questionObj.put("languageToQuestion.en", englishQuestionWithMoreTranslations);											
					DBCursor cursor = coll.find(questionObj);						
					while (cursor.hasNext()) {  
						DBObject dbobj = cursor.next();
	    				Gson gson = new GsonBuilder().create();
	    				DatasetModel result = gson.fromJson(dbobj.toString(), DatasetModel.class);
	    				DatasetModel questionItem = new DatasetModel();
	    				questionItem.setId(result.getId());
	    				questionItem.setDatasetVersion(result.getDatasetVersion());
	    				questionItem.setAnswerType(result.getAnswerType());
	    				questionItem.setAggregation(result.getAggregation());
	    				questionItem.setHybrid(result.getHybrid());
	    				questionItem.setOnlydbo(result.getOnlydbo());
	    				questionItem.setSparqlQuery(result.getSparqlQuery());		    				
	    				questionItem.setPseudoSparqlQuery(result.getPseudoSparqlQuery());
	    				questionItem.setOutOfScope(result.getOutOfScope());
	    				//normalize question in english version
	    				Map<String, String> normalizedQuestion = new HashMap<String, String>();
	    				for (Map.Entry<String, String> mapEntry: q.getLanguageToQuestion().entrySet()) {
	    					if (mapEntry.getKey().equals("en")) {
	    						normalizedQuestion.put("en", englishQuestionWithMoreTranslations);
	    					}else {
	    						normalizedQuestion.put(mapEntry.getKey(), mapEntry.getValue());
	    					}
	    						
	    				}
	    				questionItem.setLanguageToQuestion(normalizedQuestion);
	    				questionItem.setLanguageToKeyword(q.getLanguageToKeyword());
	    				questionItem.setGoldenAnswer(result.getGoldenAnswer());		    				
	    				DocumentDAO documentDao = new DocumentDAO();
						documentDao.updateDocument(questionItem);
						ListDM.add(questionItem);
					}
					cursor.close();					
				}catch (Exception e) {}
			}			
		return ListDM;
	}
	
	@RequestMapping(value= "/listFailedQuestions", method = RequestMethod.GET)
	public List<DatasetModel> countFailedQuestions() {
		BaseResponse response = new BaseResponse();
		List<DatasetModel> tasks = new ArrayList<DatasetModel>();
	 	Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);
		int num_failed_questions = 0;
		for (int x=0; x<listDataset.size(); x++) {
    		try {   			
    			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
    			DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
    			DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
    			while (cursor.hasNext()) {    				
    				DBObject dbobj = cursor.next();
    				Gson gson = new GsonBuilder().create();
    				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);			
    				String languageToQuestionEn = q.getLanguageToQuestion().get("en").toString();    
    				String sprqlQuery = q.getSparqlQuery();
    				
    				//count how many questions failed returning answer from current endpoint
    				DocumentDAO dDAO = new DocumentDAO();
    				String resultStatus = dDAO.outOfScopeChecking(sprqlQuery, languageToQuestionEn);
    				if (resultStatus == "false") {
    					tasks.add(q);
    					num_failed_questions++;
    				}    				
    			}
    			cursor.close();
    			} catch (Exception e) {}
    	 	}
		System.out.println("Number of Failed Question= "+num_failed_questions);
		return tasks;
	}
			
	@RequestMapping(value = "/all", method = RequestMethod.GET)
	public List<QuestionResponse> ShowAll() {
		List<QuestionResponse> tasks = new ArrayList<QuestionResponse>();
		try {
			DB db = MongoDBManager.getDB("QaldCurator");
			DBCollection coll = db.getCollection("QALD8_Test_Multilingual");
			DBCursor cursor = coll.find();
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				QuestionResponse q = gson.fromJson(dbobj.toString(), QuestionResponse.class);
				QuestionResponse item = new QuestionResponse();
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
			return tasks;
			
		} catch (Exception e) {}
		return null;
	}
	
	@RequestMapping(value = "/filtered-documents", method = RequestMethod.GET)
	public List<DatasetModel> filteredDocument() {
		List<DatasetModel> tasks = new ArrayList<DatasetModel>();
		Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	HashMap<String, String> questionDatabaseVersion = new HashMap<String, String>();	 	
	 	for (int x=0; x<listDataset.size(); x++) {
		try {
			DB db = MongoDBManager.getDB("QaldCurator"); //Database Name
			DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
			DBCursor cursor = coll.find(); //Find All
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
							
				String qaldVersion = listDataset.get(x).getName();
				String[] st = qaldVersion.split("_");
				String st1 = st[0].substring(st[0].length() - 1);
				String questionKey = q.getLanguageToQuestion().get("en").toString();
				if (questionKey.endsWith("?") || questionKey.endsWith(".") || questionKey.endsWith("!")) {
					questionKey = questionKey.substring(0, questionKey.length() - 1);
				} 
				//check whether current question is already inside the hashmap. In this case, question is the key and qald dataset is the value			
				if (questionDatabaseVersion.containsKey(questionKey)) {
					String questionValue = questionDatabaseVersion.get(questionKey);
					String[] st2 = questionValue.split("_");
					String st3 = st2[0].substring(st2[0].length() - 1);
					if (Integer.parseInt(st1) > Integer.parseInt(st3)) {
						questionDatabaseVersion.put(questionKey, qaldVersion);						
					}						
					}else 	{
							questionDatabaseVersion.put(questionKey, qaldVersion);
							}				
				}	
			cursor.close();
			} catch (Exception e) {}		
	 	}
	 	
	 	//Get filtered dataset 
	 	Set set = questionDatabaseVersion.entrySet();
	    Iterator iterator = set.iterator();
	    HashMap<String, String> xx = new HashMap<String, String>();
	    int id_new = 1;
	    while(iterator.hasNext()) {
	         Map.Entry mEntry = (Map.Entry)iterator.next();	         	
	         
	         try {
	        	//build query
	     	 	BasicDBObject searchObj = new BasicDBObject();
	        	searchObj.put("languageToQuestion.en", mEntry.getKey().toString());
	 			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
	 			DBCollection coll = db.getCollection(mEntry.getValue().toString()); //Collection	
	 			String question = mEntry.getKey().toString();
	 			DBCursor cursor = coll.find(searchObj);
	 			while (cursor.hasNext()) {
	 				DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					DatasetModel item = new DatasetModel();
					item.setDatasetVersion(mEntry.getValue().toString());
					item.setId(String.valueOf(id_new));
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
					tasks.add(item);
	 			}
	 			cursor.close();
	         } catch (Exception e) {}
	         id_new++;	         
	      }
	    return tasks;	 	
	}
	
	private static String readUrl(String urlString) throws Exception {
	    BufferedReader reader = null;
	    try {
	        URL url = new URL(urlString);
	        reader = new BufferedReader(new InputStreamReader(url.openStream()));
	        StringBuffer buffer = new StringBuffer();
	        int read;
	        char[] chars = new char[1024];
	        while ((read = reader.read(chars)) != -1)
	            buffer.append(chars, 0, read); 

	        return buffer.toString();
	    } finally {
	        if (reader != null)
	            reader.close();
	    }
	}	
}
