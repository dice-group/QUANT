package app.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/*
import org.zkoss.mongodb.model.Question;
import org.zkoss.mongodb.model.Questions;
import org.zkoss.sparql.Query;
import org.zkoss.zk.ui.event.SelectEvent;
import org.zkoss.zul.ListModelList;
*/
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.model.Dataset;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.sparql.SparqlService;
import rationals.properties.isEmpty;

public class DocumentDAO {
	 public List<DatasetModel> getAllDocuments() {
		 List<DatasetModel> tasks = new ArrayList<DatasetModel>();
		 
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCurator"); //Database Name
				DBCollection coll = db.getCollection("QALD8_Test_Multilingual"); //Collection
				DBCursor cursor = coll.find(); //Find All
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					DatasetModel item = new DatasetModel();
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
				return tasks;				
			} catch (Exception e) {}
			return null;
		}
	 
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
		 	}
		 	//Get filtered dataset 
		 	Set set = questionDatabaseVersion.entrySet();
		    Iterator iterator = set.iterator();
		    HashMap<String, String> xx = new HashMap<String, String>();
		    int id_new = 1;
		    while(iterator.hasNext()) {
		         Map.Entry mEntry = (Map.Entry)iterator.next();
		         //xx.put(mentry.getKey().toString(), mentry.getValue().toString());	
		         
		         try {
		        	//build query
		     	 	BasicDBObject searchObj = new BasicDBObject();
		        	searchObj.put("languageToQuestion.en", mEntry.getKey().toString());
		 			DB db = MongoDBManager.getDB("QaldCurator"); //Database Name
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
				DB db = MongoDBManager.getDB("QaldCurator");
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
				return item;
		 } catch (Exception e) {}
		 return item;
	 }
	 public List<DatasetModel> getAllDatasets() {
		 List<DatasetModel> tasks = new ArrayList<DatasetModel>();
		 	Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	BasicDBObject sortObj = new BasicDBObject();
			sortObj.put("id",1);
		 	for (int x=0; x<listDataset.size(); x++) {
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCurator"); //Database Name
				DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
				DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					DatasetModel item = new DatasetModel();
					item.setDatasetVersion(listDataset.get(x).getName());
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
					tasks.add(item);
				}								
			} catch (Exception e) {}
		 }
			return tasks;
		}
	 /*
	  * This method is used to update document on MongoDB
	  */
	 public void updateDocument(DatasetModel document, String collectionName) {
		 try {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", document.getId());
			
			BasicDBObject newDbObj = toBasicDBObject(document);
			
			DB db = MongoDBManager.getDB("QaldCurator");
			DBCollection coll = db.getCollection(collectionName);
			
			coll.update(searchObj, newDbObj);
		 } catch (Exception e) {}
	 }

	 /*
	  * This method is used to create an object for update or save purpose process on MongoDB
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
	public DatasetSuggestionModel implementCorrection(String id, String datasetVersion) {
		 BasicDBObject searchObj = new BasicDBObject();
		 DatasetSuggestionModel item = new DatasetSuggestionModel();
		 searchObj.put("id", id);
		 try {
				DB db = MongoDBManager.getDB("QaldCurator");
				DBCollection coll = db.getCollection(datasetVersion);
				DBCursor cursor = coll.find(searchObj);
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);
					String answerType = q.getAnswerType();				
					String[] strArray = (String[]) q.getGoldenAnswer().toArray(new String[ q.getGoldenAnswer().size()]);
					String answer=strArray[0];
					
					if (!answerType.equals(AnswerTypeChecking(answerType,answer))) {						
						item.setAnswerTypeSugg(AnswerTypeChecking(answerType, answer));						
					}
					String query = q.getSparqlQuery().toString();
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
							
					String outOfScope = q.getOutOfScope().toString();	
					
					if ((outOfScopeChecking(query).isEmpty()) && (outOfScope.toLowerCase().equals("false"))) {
						item.setOutOfScopeSugg("true");
					}else if ((outOfScopeChecking(query).isEmpty()) && (outOfScope.toLowerCase().equals("true"))) {
								item.setOutOfScopeSugg("");
							}else if ((outOfScope.equals("")) && (!outOfScopeChecking(query).equals(""))) {
										item.setOutOfScopeSugg("false");
									}else if ((outOfScope.isEmpty()) && (outOfScopeChecking(query).equals(""))) {
										item.setOutOfScopeSugg("true");
										}
					
					String result = outOfScopeChecking(query);
					item.setOutOfScopeSugg(result);
					//System.out.println("This is the answer: "+ outOfScopeChecking(query));
				}
				
		 } catch (Exception e) {}
		 return item;
	 }
	
	//Check Answer Type
	public String AnswerTypeChecking (String answerType, String answerValue) {
		String finalAnswerType = "";		
		if (answerValue.toLowerCase().startsWith("http://")) {
			//System.out.println("This is http");
			if (answerType.toLowerCase().equals("resource")) {
				finalAnswerType = answerType;
			}else
			{				
				finalAnswerType = "resource";
			}	
		}
			else if (validateDateFormat(answerValue))
			{
				//System.out.println("this is date");
				if (answerType.toLowerCase().equals("date")) {
					finalAnswerType = answerType;
				}else
				{
					finalAnswerType =  "date";
				}
			}
				else if ((isNumeric(answerValue)) || (answerValue.matches("\\d.*")))
				{					
					if (answerType.toLowerCase().equals("number")) {
						finalAnswerType = answerType;
					}else
					{					
						finalAnswerType = "number";
					}
				}else if ((answerValue.toString().equals("true")) || (answerValue.toString().equals("false"))){
							if (answerType.toLowerCase().equals("boolean")) {
								finalAnswerType = answerType;
							}else
							{					
								finalAnswerType = "boolean";
							}
						}			
						else if (answerValue.toLowerCase().matches("\\w.*")){
							if (answerType.toLowerCase().equals("string")) {
								finalAnswerType = answerType;
							}else
							{
								finalAnswerType =  "string";
							}
						}		
		return finalAnswerType;
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
	
	//Covert a string into a number	
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
	public String outOfScopeChecking(String sparqlQuery) {
		/** Pretty display Query Sparql **/
		SparqlService ss = new SparqlService();		
		/** Retrieve online answer from current endpoint **/
		String onlineAnswer = ss.getQuery(sparqlQuery).toString();
		return onlineAnswer;
	}
}
