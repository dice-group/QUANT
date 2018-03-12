package app.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
				}
				return item;
		 } catch (Exception e) {}
		 return null;
	 }
	 public List<DatasetModel> getAllDatasets() {
		 List<DatasetModel> tasks = new ArrayList<DatasetModel>();
		 	Dataset dataset = new Dataset();
		 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
		 	for (int x=0; x<listDataset.size(); x++) {
			try {
				//call mongoDb
				DB db = MongoDBManager.getDB("QaldCurator"); //Database Name
				DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
				DBCursor cursor = coll.find(); //Find All
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
					tasks.add(item);
				}								
			} catch (Exception e) {}
		 }
			return tasks;
		}
}
