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
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;

//import org.json.JSONArray;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import com.davidsoergel.dsutils.Base64.OutputStream;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
//import com.google.common.net.MediaType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.dao.DocumentDAO;
import app.model.Dataset;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.Question;
import app.model.UserLog;
import app.response.BaseResponse;
import app.response.QuestionResponse;
import app.sparql.SparqlService;

@RestController
@RequestMapping(value= {"/document/datasets"}, produces="application/json; charset=UTF-8")
public class DocumentRestController {
	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	/**
	 * test
	 * @return 
	 * @return
	 */	
	
	@RequestMapping(value= "/addTranslations", method= RequestMethod.GET)
	public List<DatasetModel> addTranslationResults() throws FileNotFoundException, IOException, ParseException {
		BaseResponse response = new BaseResponse();
		List<Question> tasks = new ArrayList<Question>();
		
		//read all translations from json file
		String json;
		JSONParser parser = new JSONParser();
		JSONArray results = new JSONArray();		
		JSONArray inFile = (JSONArray) parser.parse(new InputStreamReader(new FileInputStream("C:/Users/riagu/Documents/addedTranslations.json"),"UTF8"));

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
				for (int x=0; x<listDataset.size(); x++) {
					BasicDBObject questionObj = new BasicDBObject();
					try{
						DB db = MongoDBManager.getDB("QaldCuratorFiltered");
						//DBCollection coll = db.getCollection(listDataset.get(x).getName());
						DBCollection coll = db.getCollection("QALD7_Test_Multilingual");
						String question = q.getLanguageToQuestion().get("en");					
						String englishQuestionWithMoreTranslations = q.getLanguageToQuestion().get("en").replaceAll("^\'", "");
						englishQuestionWithMoreTranslations = englishQuestionWithMoreTranslations.replaceAll("\\s+$", "");
						englishQuestionWithMoreTranslations = englishQuestionWithMoreTranslations.replaceAll("\'$", "");
						//listQuestion.add(englishQuestionWithMoreTranslations);
						questionObj.put("languageToQuestion.en", englishQuestionWithMoreTranslations);
						//BasicDBObject searchQuery = new BasicDBObject().append("languageToQuestion.en", "Give me all American presidents in the last 20 years.");
						//if (!searchQuery.isEmpty()) {						
							DBCursor cursor = coll.find(questionObj);						
							while (cursor.hasNext()) {  
								DBObject dbobj = cursor.next();
			    				Gson gson = new GsonBuilder().create();
			    				DatasetModel result = gson.fromJson(dbobj.toString(), DatasetModel.class);
			    				DatasetModel questionItem = new DatasetModel();
			    				questionItem.setId(result.getId());
			    				questionItem.setDatasetVersion("QALD7_Test_Multilingual");
			    				questionItem.setAnswerType(result.getAnswerType());
			    				questionItem.setAggregation(result.getAggregation());
			    				questionItem.setHybrid(result.getHybrid());
			    				questionItem.setOnlydbo(result.getOnlydbo());
			    				questionItem.setSparqlQuery(result.getSparqlQuery());		    				
			    				questionItem.setPseudoSparqlQuery(result.getPseudoSparqlQuery());
			    				questionItem.setOutOfScope(result.getOutOfScope());
			    				questionItem.setLanguageToQuestion(q.getLanguageToQuestion());
			    				questionItem.setLanguageToKeyword(q.getLanguageToKeyword());
			    				questionItem.setGoldenAnswer(result.getGoldenAnswer());		    				
			    				DocumentDAO documentDao = new DocumentDAO();
								documentDao.updateDocument(questionItem);
								ListDM.add(questionItem);
							}						
					}catch (Exception e) {}
				}
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
    				/*SparqlService ss = new SparqlService();
    				Set<String> results = new HashSet(); 
    				String result = null;
    				//results.add(null);
    				if (ss.isASKQuery(languageToQuestionEn)) {
    					result = ss.getResultAskQuery(sprqlQuery);    					
    				}else {				
    					results = ss.getResultsFromCurrentEndpoint(sprqlQuery);    					
    				}	
    				if ((result == null) || (results.contains(null))) {				
						num_failed_questions++;
    					tasks.add(q);
					}*/
    			}								
    			} catch (Exception e) {}
    	 	}
		System.out.println("Number of Failed Question= "+num_failed_questions);
		return tasks;
	}
			
	/*@RequestMapping(value= "/countFailedQuestions", method = RequestMethod.GET)
	public void countFailedQuestions() {
		BaseResponse response = new BaseResponse();
		List<DatasetModel> tasks = new ArrayList<DatasetModel>();
	 	Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);		 		
		
		try {            
            File statText = new File("C:/Users/riagu/Documents/PhD Study/First Task/ListFailedQuestions.txt");
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            
            int numFailedQuestions = 0;
    		int xx=0;
    		String onlineAnswer;
    	 	for (int x=0; x<listDataset.size(); x++) {
    		try {   			
    			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
    			DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
    			DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
    			while (cursor.hasNext()) {
    				xx++;
    				DBObject dbobj = cursor.next();
    				Gson gson = new GsonBuilder().create();
    				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);			
    				String languageToQuestionEn = q.getLanguageToQuestion().get("en").toString();    
    				String sprqlQuery = q.getSparqlQuery();
    				
    				//count how many questions failed returning answer from current endpoint
    				SparqlService ss = new SparqlService();
    				Set<String> results = new HashSet(); 
    				String result = null;
    				if (ss.isASKQuery(languageToQuestionEn)) {
    					result = ss.getResultAskQuery(sprqlQuery);    					
    				}else {				
    					results = ss.getQuery(sprqlQuery);    					
    				}	
    				if ((result == null) || (results == null)) {					
						numFailedQuestions = numFailedQuestions+1;
						w.write("Id: "+q.getId()+"\n");
						w.write("Question Dataset Version: "+listDataset.get(x).getName()+"\n");
						w.write("Question: "+ languageToQuestionEn+"\n");						
						w.write("\n\n");
					}
    			}								
    			} catch (Exception e) {}
    	 	}
    	 	w.write("Number of failed questions is "+numFailedQuestions);
    	 	w.close();
		}
        catch (IOException e) {
            	System.err.println("Problem writing to the file statsTest.txt");
        		}	 		 	
	}*/
	
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
	    int id_new = 0;
	    while(iterator.hasNext()) {
	         Map.Entry mEntry = (Map.Entry)iterator.next();
	         //xx.put(mentry.getKey().toString(), mentry.getValue().toString());	
	         //this is used temporary to separate filtered question into their category
	         if (mEntry.getValue().toString().equals("QALD8_Train_Multilingual")) {
	        	 id_new++;
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
	 					//item.setDatasetVersion(mEntry.getValue().toString());
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
	 	         
	         }	         
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
