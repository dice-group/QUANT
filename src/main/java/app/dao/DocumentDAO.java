package app.dao;
//package org.dice.qa;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.controller.SparqlCorrection;
import app.model.Dataset;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.DocumentList;
import app.model.UserDatasetCorrection;
import app.model.UserDatasetCorrectionTemp;
import app.sparql.SparqlService;

public class DocumentDAO {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(DocumentDAO.class);
	
	 public List<DocumentList> getCollections(int userId, List<DatasetList> listDataset) {
		 List<DocumentList> tasks = new ArrayList<DocumentList>();
		 BasicDBObject sortObj = new BasicDBObject();
		 sortObj.put("id",1);
		 for (int x=0; x<listDataset.size(); x++)	{	
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
				DBCursor cursor = coll.find().sort(sortObj); //Find All
				
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					DocumentList item = new DocumentList();
					item.setDatasetVersion(listDataset.get(x).getName());
					item.setId(q.getId());
					item.setQuestion(q.getLanguageToQuestion().get("en").toString());	
					item.setKeywords(q.getLanguageToKeyword());
					item.setIsCurate(false);				
					tasks.add(item);
				}	
				cursor.close();
			} catch (Exception e) {
				LOGGER.error("Got exception while trying to get collections.", e);
			}
		 	}		 	
			return tasks;
		}
	 
	 @SuppressWarnings("rawtypes")
	public List<DatasetModel> filteredDocument() {
			List<DatasetModel> tasks = new ArrayList<DatasetModel>();
			Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	HashMap<String, String> questionDatabaseVersion = new HashMap<String, String>();
		 	for (int x=0; x<listDataset.size(); x++) {
		 		DBCursor cursor = null;
			try {	
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name 	
				DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
				cursor = coll.find(); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
								
					String qaldVersion = listDataset.get(x).getName();
					String[] st = qaldVersion.split("_");
					String st1 = st[0].substring(st[0].length() - 1);
					String questionKey = q.getLanguageToQuestion().get("en").toString();
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
				} catch (Exception e) {}		
				finally {
					if(cursor != null) {
						cursor.close();
					}
				}
		 	}
		 	//Get filtered dataset 
		 	Set set = questionDatabaseVersion.entrySet();
		    Iterator iterator = set.iterator();
		    int id_new = 1;
		    while(iterator.hasNext()) {
		         Map.Entry mEntry = (Map.Entry)iterator.next();		         	
		         
		         try {
		        	//build query
		     	 	BasicDBObject searchObj = new BasicDBObject();
		        	searchObj.put("languageToQuestion.en", mEntry.getKey().toString());
		 			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
		 			DBCollection coll = db.getCollection(mEntry.getValue().toString()); //Collection	
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
	 
	 public DatasetModel getDocument(String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 DatasetModel item = new DatasetModel();
		 searchObj.put("id", id);
		 try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
				DBCollection coll = db.getCollection(datasetVersion);
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);					
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
					item.setOutOfScope(q.getOutOfScope());
				}
				cursor.close();
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	 
	 public List<DocumentList> getAllDatasets(int userId, String userName, String role) {		 
		 List<DocumentList> tasks = new ArrayList<DocumentList>();
		 	Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id",1);
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			
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
							item.setIsCurate(udcDao.isDocumentExist(userId, q.getId(), listDataset.get(x).getName()));
							item.setIsRemoved(udcDao.isDocumentRemoved(userId, q.getId(), listDataset.get(x).getName()));
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
							item.setIsCurate(udcDao.isDocumentExist(userId, q.getId(), q.getDatasetVersion()));
							item.setIsRemoved(udcDao.isDocumentRemoved(userId, q.getId(), q.getDatasetVersion()));
							tasks.add(item);
						}	
						cursor.close();
					} catch (Exception e) {}
			}	 
			return tasks;
		}
	 /*
	  * This method is used to update document in MongoDB
	  */
	 public void updateDocument(DatasetModel document, String collectionName) {
		 try {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", document.getId());
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection(collectionName);
			
			coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 }

	 /*
	  * This method is used to create an object for update or save purpose in MongoDB
	  */
	private BasicDBObject toBasicDBObject(DatasetModel document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("id", document.getId());
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
	
	//Correction Parts
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public DatasetSuggestionModel implementCorrection(String id, String datasetVersion, String curatedStatus, int userId) {		
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
		try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered");
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
											
					//System.out.println("This is the SPARQL: "+ query);
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
						
						//System.out.println("Answers size is : "+answers.size());
						
						if (answerStatus) {
							if (!(answerType.equals(answerTypeChecking(answers))) || (answerType.equals(""))) {	//					
								item.setAnswerTypeSugg(answerTypeChecking(answers));								
							}
						}					
					}
					
					//System.out.println("Answer Status: "+answerStatus);									
					//item.setResultStatus(query);
					//check whether it needs to provide SPARQL suggestion
					boolean sparqlStatus = true;
					if (!answerStatus) {	
						try {
							Map<String, List<String>> sparqlSuggestion = sparqlCorrection(query);
							/*System.out.println("Sparql Query "+query);
							System.out.println("Sparql Suggestion "+sparqlSuggestion);*/
							Map<String,List<String>> sparqlAndCaseList = new HashMap<String,List<String>>();
							Set<String> answerList = new HashSet();
							
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
																			
										if (ss.isASKQuery(languageToQuestionEn)) {
											String result = ss.getResultAskQuery(mapEntry.getKey());										
											answerList.add(result);														
										}else {				
											answerList = ss.getQuery(mapEntry.getKey());
											//answerList.addAll(results);										
										}	
										sparqlAndCaseList.put(mapEntry.getKey(), mapEntry.getValue());
										
									}
									
									item.setSparqlAndCaseList(sparqlAndCaseList);
									item.setAnswerFromVirtuosoList(answerList);
								}	
							}						
						} catch (Exception e) {
							item.setResultStatus("error");
							item.setSparqlCorrectionStatus(false);
							//item.setResultStatus(String.valueOf(answerStatus));
							// TODO: handle exception
						}												
					}
					/*if (answerStatus) {
						sparqlStatus = false;
						item.setSparqlCorrectionStatus(sparqlStatus);
					}*/
						
					
					//Check whether it needs to display button View Suggestion
					if (outOfScopeChecking(query, languageToQuestionEn).equals("false")) {
						//System.out.println("Result status is this: false");
						item.setResultStatus("false");
					}else {
						//System.out.println("Result status is this: true");
						item.setResultStatus("true");
					}
					
					
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
	
	//Apply SPARQL correction
	public Map<String, List<String>> sparqlCorrection (String sparqlQuery) throws ParseException {
		SparqlCorrection sparqlC = new SparqlCorrection();
		return sparqlC.findNewProperty(sparqlQuery);	
	}
	
	//Check whether the answer contains Umlaut character
	public boolean isUmlaut(String input) {
		//final String REGEX_umlaut = "[üöäßÜÖÄ]";
		if (input.contains("ü") || input.contains("ö") || input.contains("ä") || input.contains("ß") || input.contains("Ü") || input.contains("Ö") || input.contains("Ä")) {
			return true;
		}
		return false;
	}
	
	//normalize Umlaut character in answer
	public String replaceUmlaut(String input) {

	     //replace all lower Umlauts
	     String output = input.replace("ü", "ue")
	                          .replace("ö", "oe")
	                          .replace("ä", "ae")
	                          .replace("ß", "ss");

	     //first replace all capital umlaute in a non-capitalized context (e.g. Übung)
	     output = output.replace("Ü(?=[a-zäöüß ])", "Ue")
	                    .replace("Ö(?=[a-zäöüß ])", "Oe")
	                    .replace("Ä(?=[a-zäöüß ])", "Ae");

	     //now replace all the other capital umlaute
	     output = output.replace("Ü", "UE")
	                    .replace("Ö", "OE")
	                    .replace("Ä", "AE");

	     return output;
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
	//Check Answer Type of select query
	public String answerTypeChecking (Set<String> answers) {
		//final String REGEX_URI = "^(\\w+):(\\/\\/)?[-a-zA-Z0-9+&@#()\\/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#()\\/%=~_|]";
		final Set<String> VALID_SCHEMES = Collections.unmodifiableSet(new HashSet<String>(Arrays.asList(
			      "http://", "https://", "ftp://", "ftps://", "http%3a//", "https%3a//", "ftp%3a//", "ftps%3a//")));
		try {
			if(answers.size()>=1) {
				
				Iterator<String> answerIt = answers.iterator();

				//the first element
				String answer = answerIt.next();
				//System.out.println("answer is "+answer);			
				if (isUmlaut(answer)) {				
					answer = replaceUmlaut(answer);
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
					if (isUmlaut(answer)) {
						answer = replaceUmlaut(answer);
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
	
	//Deal with exponential value
	public Integer reformatExponentialValue(String exponentialValue){
        BigDecimal myNumber= new BigDecimal(exponentialValue);
        Integer result=myNumber.intValue(); 
        return result;
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
	    @SuppressWarnings("unused")
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

        return input.matches("([0-9]{4})-([0-9]{2})-([0-9]{2})") || input.matches("([0-9]{3})-([0-9]{1})-([0-9]{1})");
    }
	
	//Check Out of Scope Value
	public String outOfScopeChecking(String sparqlQuery, String languageToQuestionEn) {		
		SparqlService ss = new SparqlService();	
		String resultStatus="";
		if (ss.isASKQuery(languageToQuestionEn)) {			
			if (ss.getResultAskQuery(sparqlQuery).equals(null)) {
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
	
	public int countQaldDataset(String datasetVersion) {
		 	try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection(datasetVersion); //Collection
				DBCursor cursor = coll.find(); //Find All				
				return cursor.count();	
				
			} catch (Exception e) {}
		
		return 0;
	}
	//determine previous document
	public String getPreviousDocument(String currentId, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			BasicDBObject cSearchObj = new BasicDBObject();
			cSearchObj.put("$lt", currentId);
			searchObj.put("id", cSearchObj);
			
			BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id", -1);
			
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection(datasetVersion); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); 
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					return q.getId();
				}
				cursor.close();				
			} catch (Exception e) {}
			return null;
		}
		//determine next document
		public String getNextDocument(String currentId, String datasetVersion) {
			BasicDBObject searchObj = new BasicDBObject();
			BasicDBObject cSearchObj = new BasicDBObject();
			cSearchObj.put("$gt", currentId);
			searchObj.put("id", cSearchObj);
			
			BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id", 1);
			
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection(datasetVersion); //Collection
				DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1); 
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					return q.getId();
				}
				cursor.close();				
			} catch (Exception e) {}
			return null;
		}
	public String getNextCollection(String currentCollection) {
		Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	for (int x=0; x<listDataset.size(); x++) {
	 		if (listDataset.get(x).getName().equals(currentCollection)) {
	 			if (x==(listDataset.size()-1)) {
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
	
	public String getLastRecordCollection(String collectionName) {
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",-1);
		try {
			//call mongoDb
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection(collectionName); //Collection
			DBCursor cursor = coll.find().sort(sortObj).limit(1); 
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				return q.getId();
			}
			cursor.close();				
		} catch (Exception e) {}
		return null;
	}
	
	public String getNextRecordCollection(String collectionName) {
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);
		try {
			//call mongoDb
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection(collectionName); //Collection
			DBCursor cursor = coll.find().sort(sortObj).limit(1); 
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				return q.getId();
			}
			cursor.close();				
		} catch (Exception e) {}
		return null;
	}
	
	//get generated keywords suggestion for question that does not have keywords
	public Map<String, List<String>> getGeneratedKeywords (String id, String datasetVersion, String question){
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);
		searchObj.put("languageToQuestion.en", question);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("QuestionWithGeneratedKeywords"); //Collection
			DBCursor cursor = coll.find(searchObj); 
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				return q.getLanguageToKeyword();
			}
			cursor.close();
		}catch (Exception e) {}
		return null;
	}
	//generate keywords from question that does not have keywords 
	public List<String> generateKeywords(String question) throws FileNotFoundException, IOException{	
		//read a file that contains English stopwords		
		FileReader fileReader = new FileReader("C:/Users/riagu/Documents/englishStopwords.txt");
		//FileReader fileReader = new FileReader(filename);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        List<String> englishStopwords = new ArrayList<String>();
        String element = null;         
        while ((element = bufferedReader.readLine()) != null) 
        {
        	englishStopwords.add(element);
        }         
        bufferedReader.close();
        question = question.toLowerCase();
        if (question.endsWith("?") || question.endsWith(".") || question.endsWith("!")) {
			question = question.substring(0, question.length() - 1);
		}
		question = " " + question + " ";
		System.out.println(question);		
		for (String term : englishStopwords) {			
			question = question.replace(" "+ term +" ", " ");			
		}
		question = question.substring(1, question.length() - 1);
		String[] wordsList = question.split(" ");
		List<String> keywordsSuggestion = Arrays.asList(wordsList);
		bufferedReader.close();
		fileReader.close();
		return keywordsSuggestion;		
	}
	
	
	public void updateDocument(DatasetModel document) {
		 BasicDBObject searchObj = new BasicDBObject();
		 searchObj.put("id", document.getId());
		 try {			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection(document.getDatasetVersion());
			
			coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 }
	
	//check whether a question needs keywords translations. This process will check whether the keywords are already completed with their translations (11 languages)
	public boolean doesNeedKeywordsTranslations (String id, String datasetVersion, String question) {
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
	
	//check whether a question needs translations. This process will check whether the question is already completed with their translations in 11 languages
		public boolean doesNeedQuestionTranslations (String id, String datasetVersion, String question) {
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
				if ((q.getLanguageToQuestion().size() == 11)) {
					return false;
				}			
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
		}
		return true;
		}
		
	//check whether a question needs added keywords translations. This process will check whether the keywords are already provided in AddedTranslations table
	public boolean doesNeedAddedKeywordsTranslations (String id, String datasetVersion, String question) {
	BasicDBObject searchObj = new BasicDBObject();
	searchObj.put("id", id);
	searchObj.put("datasetVersion", datasetVersion);
	searchObj.put("languageToQuestion.en", question);
	try {
		DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
		DBCollection coll = db.getCollection("AddedTranslations"); //Collection
		DBCursor cursor = coll.find(searchObj).limit(1); 
		while (cursor.hasNext()) {				
			DBObject dbobj = cursor.next();
			Gson gson = new GsonBuilder().create();
			DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
			if (!(q.getLanguageToKeyword().isEmpty())) {
				return true;
			}			
		}
		cursor.close();
	}catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	
	
	//check whether a question needs keyword suggestion	
	public boolean doesNeedKeywordSuggestions (String id, String question, String datasetVersion) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("languageToQuestion.en", question);
		searchObj.put("id", id);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection(datasetVersion); //Collection
			DBCursor cursor = coll.find(searchObj).limit(1); 
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				if (q.getLanguageToKeyword().isEmpty()) {
					return true;
				}
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
		}
		return false;
	}
	
	//check whether a question needs suggested keywords translations	
		public boolean doesNeedTranslationsOfSuggestedKeywords (String id, String datasetVersion, String question) {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("languageToQuestion.en", question);
			searchObj.put("id", id);
			searchObj.put("datasetVersion", datasetVersion);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("keywordsSuggestionsTranslations"); //Collection
				DBCursor cursor = coll.find(searchObj); 
				while (cursor.hasNext()) {				
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					if (q.getLanguageToKeyword().isEmpty()) {
						return true;
					}
				}
				cursor.close();
			}catch (Exception e) {
					// TODO: handle exception
			}
			return false;
		}
		
	//provide question translations for the one that needs all translations
	public DatasetModel getQuestionTranslations (String id, String datasetVersion, String question) {
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
				
				//reverse characters order in Persian Keywords translations
				Map<String, List<String>> newTranslations = new HashMap<String, List<String>>();
				for (Map.Entry<String, List<String>> mapEntry : q.getLanguageToKeyword().entrySet()) {
					if (mapEntry.getKey().equals("fa")) {
						String reversedForParsi;
						List<String> newParsiTranslations = new ArrayList<String>();
						for (String element: mapEntry.getValue()) {
							reversedForParsi = this.reverseString(element);
							newParsiTranslations.add(reversedForParsi);
						}
						newTranslations.put("fa", newParsiTranslations);
					}else {
						newTranslations.put(mapEntry.getKey(), mapEntry.getValue());
					}
				}
				q.setLanguageToKeyword(newTranslations);
				
				//reverse characters order in Persian Question translations
				Map<String, String> newQTranslations = new HashMap<String, String>();
				for (Map.Entry<String, String> mapEntryQ : q.getLanguageToQuestion().entrySet()) {
					if (mapEntryQ.getKey().equals("fa")) {
						String reversedQForParsi;
						reversedQForParsi = this.reverseString(mapEntryQ.getValue());							
						newQTranslations.put("fa", reversedQForParsi);
					}else {
						newQTranslations.put(mapEntryQ.getKey(), mapEntryQ.getValue());
					}
				}
				q.setLanguageToQuestion(newQTranslations);
				return q;
				//return 1;
			}else {
				DBCollection coll1 = db.getCollection("AddedTranslations"); //collection of suggested question translations for questions that already have some tranlations (but not complete) 
				DBCursor cursor1 = coll1.find(searchObj).limit(1);
				while (cursor1.hasNext()) {
					DBObject dbobj1 = cursor1.next();
					Gson gson1 = new GsonBuilder().create();
					DatasetModel q1 = gson1.fromJson(dbobj1.toString(), DatasetModel.class);
					DatasetModel result = new DatasetModel();
					//check whether number of suggestion is 11 (all translations). It should only for those that missed
					//if (q1.getLanguageToQuestion().size() > 10) {
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
						BasicDBObject searchObj2 = new BasicDBObject();
						searchObj2.put("id", id);
						DBCollection coll2 = db.getCollection(q1.getDatasetVersion()); 
						DBCursor cursor2 = coll2.find(searchObj2);
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
						}
						cursor2.close();
						//get missed translations for questions
						Map<String, String> finalSuggQ = new HashMap<String, String>();
						for (String aQ: keyAllSuggQ) {
							if (!(keyExistingQ.contains(aQ))) {
								//put into final suggestion
								for (Map.Entry<String, String> mapEQ: q1.getLanguageToQuestion().entrySet()) {
									if (mapEQ.getKey().equals(aQ)) {										
										if (aQ.equals("fa")) {
											String reversedQForParsi;
											reversedQForParsi = this.reverseString(mapEQ.getValue());							
											finalSuggQ.put("fa", reversedQForParsi);
										}else {
											finalSuggQ.put(mapEQ.getKey(), mapEQ.getValue());
										}
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
										if (aK.equals("fa")) {
											String reversedForParsi;
											List<String> newParsiTranslations = new ArrayList<String>();
											for (String element: mapEK.getValue()) {
												reversedForParsi = this.reverseString(element);
												newParsiTranslations.add(reversedForParsi);
											}
											finalSuggK.put("fa", newParsiTranslations);
										}else {
											finalSuggK.put(mapEK.getKey(), mapEK.getValue());
										}									
										break;
									}
								}
							}
						}					
					result.setLanguageToKeyword(finalSuggK);
					result.setLanguageToQuestion(finalSuggQ);
					return result;
					//}										
				}
				cursor1.close();
				//return 2;
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
			}
		return null;		
	}
	
	//provide question translations for the one that already has some translations but needs the rest of targetted languages translations
	public DatasetModel getQuestionAddedTranslations (String id, String datasetVersion, String question) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);
		searchObj.put("languageToQuestion.en", question);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("AddedTranslations"); //Collection
			DBCursor cursor = coll.find(searchObj).limit(1);			
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				return q;
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
			}
		return null;		
	}
	
	//provide keywords translations for question that has keywords suggestion. This process will get data from keywordsSuggestionsTranslations collection
	public Map<String, List<String>> getTranslationsOfSuggestedKeywords (String id, String datasetVersion, String question) {
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("datasetVersion", datasetVersion);
		searchObj.put("languageToQuestion.en", question);
		try {
			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
			DBCollection coll = db.getCollection("keywordsSuggestionsTranslations"); //Collection
			DBCursor cursor = coll.find(searchObj).limit(1);			
			while (cursor.hasNext()) {				
				DBObject dbobj = cursor.next();
				Gson gson = new GsonBuilder().create();
				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
				//get Persian translations
				Map<String, List<String>> newTranslations = new HashMap<String, List<String>>();
				for (Map.Entry<String, List<String>> mapEntry : q.getLanguageToKeyword().entrySet()) {
					if (mapEntry.getKey().equals("fa")) {
						String reversedForParsi;
						List<String> newParsiTranslations = new ArrayList<String>();
						for (String element: mapEntry.getValue()) {
							reversedForParsi = this.reverseString(element);
							newParsiTranslations.add(reversedForParsi);
						}
						newTranslations.put("fa", newParsiTranslations);
					}else {
						newTranslations.put(mapEntry.getKey(), mapEntry.getValue());
					}
				}
				q.setLanguageToKeyword(newTranslations);				
				return q.getLanguageToKeyword();
			}
			cursor.close();
		}catch (Exception e) {
				// TODO: handle exception
			}
		return null;		
	}
	
	//reverse characters in Persian Translations
	public String reverseString(String data)
    {
        StringBuilder reversedString = new StringBuilder();
 
        // append a string into StringBuilder input1
        reversedString.append(data);
 
        // reverse StringBuilder input1
        reversedString = reversedString.reverse();
        return reversedString.toString();       
    }
	
	//get suggestion to be displayed in view detail page
	public DatasetSuggestionModel getSuggestion (int userId, String id, String datasetVersion) {
		DatasetSuggestionModel result = new DatasetSuggestionModel();
		BasicDBObject searchObj = new BasicDBObject();
		searchObj.put("id", id);
		searchObj.put("userId", userId);
		String[] arrayString = new String[2];
		arrayString[0]="curated";
		arrayString[1]="noNeedChanges";
	
		BasicDBObject searchWithOR= new BasicDBObject();
		searchWithOR.put("$in", arrayString);
		searchObj.put("status", searchWithOR);
		
		BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("revision", -1);	
		
		try {	
			DB db = MongoDBManager.getDB("QaldCuratorFiltered");
			DBCollection coll = db.getCollection("UserDatasetCorrection");
			DBCursor cursor = coll.find(searchObj).sort(sortObj).limit(1);			
			if (!cursor.hasNext()) {
				result = this.implementCorrection(id, datasetVersion, "not curated", userId);
			}else {
				while (cursor.hasNext()) {				
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					//check whether suggestion has been accepted or not for each field
					DatasetSuggestionModel theSugg = this.implementCorrection(id, datasetVersion, "curated", userId);
					if (!(q.getAnswerType().equals(theSugg.getAnswerTypeSugg()))) {
						result.setAnswerTypeSugg(theSugg.getAnswerTypeSugg());
					}
					if (!(q.getOutOfScope().equals(theSugg.getOutOfScopeSugg()))) {
						result.setOutOfScopeSugg(theSugg.getOutOfScopeSugg());
					} 
					if (!(q.getAggregation().equals(theSugg.getAggregationSugg()))) {
						result.setAggregationSugg(theSugg.getAggregationSugg());
					}
					if (!(q.getOnlydbo().equals(theSugg.getOnlyDboSugg()))) {
						result.setOnlyDboSugg(theSugg.getOnlyDboSugg());
					}
					if (!(q.getHybrid().equals(theSugg.getHybridSugg()))) {
						result.setHybridSugg(theSugg.getHybridSugg());
					}
				}
				cursor.close();
			}
			cursor.close();
			return result;				
		}catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}
	
	//get dataset for particular user in evaluation process
	public String getDatasetName(int userId) {
		String datasetName = null;
		switch(userId) {
		case 7	: datasetName = "Dataset9"; break;
		case 8	: datasetName = "Dataset9"; break;
		case 9	: datasetName = "Dataset1"; break;
		case 10	: datasetName = "Dataset1"; break;
		case 11	: datasetName = "Dataset3"; break;
		case 12	: datasetName = "Dataset3"; break;
		case 13	: datasetName = "Dataset5"; break;
		case 14	: datasetName = "Dataset5"; break;
		case 15	: datasetName = "Dataset7"; break;
		case 16	: datasetName = "Dataset7"; break;		
		}
		return datasetName;
	}
	
}	
	

