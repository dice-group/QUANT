package app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.util.Iterator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.json.JSONArray;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

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
import app.model.DatasetModelShortVersion;
import app.model.FailedQuestionModel;
import app.response.BaseResponse;
import app.response.QuestionResponse;
import app.sparql.SparqlService;

@RestController
@RequestMapping("/document/datasets")
public class DocumentRestController {
	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	/**
	 * test
	 * @return
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	 public BaseResponse test() {
	  BaseResponse response = new BaseResponse();
	 
	   // Return success response to the client.
	   response.setStatus(SUCCESS_STATUS);
	   response.setCode(CODE_SUCCESS);
	   return response;
	 }
	
	@RequestMapping(value="/exploreFailedQuestions/{questionId}/{databaseVersion}", method = RequestMethod.GET)
	public void exploreFailedQuestions(@PathVariable("questionId") String questionId, @PathVariable("databaseVersion") String databaseVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		String service="http://dbpedia.org/sparql";
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel documentItem = documentDao.getDocument(questionId, databaseVersion);
		String sprqlQuery = documentItem.getSparqlQuery();
		String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
		SparqlService ss = new SparqlService();		
		String onlineAnswer;
		try {            
            File statText = new File("C:/Users/riagu/Documents/PhD Study/First Task/ExploredFailedQuestions.txt");
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            
            /** Retrieve answer from Virtuoso current endpoint **/
			if (ss.isASKQuery(languageToQuestionEn)) {
				onlineAnswer = Boolean.toString(ss.getResultAskQuery(sprqlQuery));			
			}else {
				//onlineAnswer = ss.getQuery(sprqlQuery);
				String result = null;
				 try {
						//QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); //put query to jena sparql library
					 	QueryEngineHTTP qe = new QueryEngineHTTP(service, sprqlQuery);
						qe.setTimeout(30000, TimeUnit.MILLISECONDS);
						ResultSet rs = qe.execSelect(); //execute query
						//System.out.println("This is "+qe.getQuery());
				        ResultSetFormatter.out(rs);
				        Set<String> setResult = new HashSet();
				        while(rs.hasNext()) {
							QuerySolution s = rs.nextSolution(); //get record value
							Iterator<String> itVars = s.varNames(); //get all variable of query
							while (itVars.hasNext()) {
				                String szVar = itVars.next().toString(); //get variable of query	
				                String szVal = s.get(szVar).asNode().getLiteralValue().toString();
				                setResult.add(szVal);
				            }		            
				        }
				        result =setResult.toString();
				        w.write("question ID: "+questionId+"\n");
						w.write("Sparql: "+sprqlQuery+"\n");
						w.write("Answer from : "+result+"\n");
						w.close();				 
				} catch (Exception e) {
					result = null;}	
			}
		}catch (IOException e) {
        	System.err.println("Problem writing to the file statsTest.txt");
    		}	
	}
	
	@RequestMapping(value= "/countFailedQuestions", method = RequestMethod.GET)
	public void countFailedQuestions() {
		BaseResponse response = new BaseResponse();
		List<DatasetModel> tasks = new ArrayList<DatasetModel>();
	 	Dataset dataset = new Dataset();
	 	List<DatasetList> listDataset = dataset.getDatasetVersionLists();
	 	BasicDBObject sortObj = new BasicDBObject();
		sortObj.put("id",1);		 		
		
		try {            
            File statText = new File("C:/Users/riagu/Documents/PhD Study/First Task/ListFailedQuestionsWithSparql.txt");
            FileOutputStream is = new FileOutputStream(statText);
            OutputStreamWriter osw = new OutputStreamWriter(is);    
            Writer w = new BufferedWriter(osw);
            
            int numFailedQuestions = 1;
    		int xx=0;
    	 	for (int x=0; x<listDataset.size(); x++) {
    		try {    			
    			//call mongoDb
    			DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
    			DBCollection coll = db.getCollection(listDataset.get(x).getName()); //Collection
    			DBCursor cursor = coll.find().sort(sortObj); //Find All sort by id ascending
    			while (cursor.hasNext()) {
    				xx++;
    				DBObject dbobj = cursor.next();
    				Gson gson = new GsonBuilder().create();
    				DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);			
    				String languageToQuestionEn = q.getLanguageToQuestion().get("en").toString();    				
    				
    				//count how many questions failed returning answer from current endpoint
    				SparqlService ss = new SparqlService();
    				String sprqlQuery = q.getSparqlQuery();	
    				System.out.println("This is Sparql: "+sprqlQuery);
    				if (ss.isASKQuery(languageToQuestionEn)) {
    					Boolean onlineAnswer = ss.getResultAskQuery(sprqlQuery);
    					System.out.println(onlineAnswer);	
    					if (onlineAnswer == null) {					
    						numFailedQuestions = numFailedQuestions+1;   						
    						w.write("Id: "+q.getId()+"\n");
    						w.write("Question Dataset Version: "+listDataset.get(x).getName()+"\n");
    						w.write("Question: "+ languageToQuestionEn+"\n");
    						w.write("Sparql Query: \n");
    						w.write(sprqlQuery);	
    						w.write("\n\n");
    					}
    				}else {
    						String onlineAnswer = ss.getQuery(sprqlQuery);
    						System.out.println(onlineAnswer);
    						if (onlineAnswer == null) {					
    							numFailedQuestions = numFailedQuestions+1;
    							w.write("Id: "+q.getId()+"\n");
        						w.write("Question Dataset Version: "+listDataset.get(x).getName()+"\n");
        						w.write("Question: "+ languageToQuestionEn+"\n");
        						w.write("Sparql Query: \n");
        						w.write(sprqlQuery);		
        						w.write("\n\n");
    						}
    					  }			
    			}								
    			} catch (Exception e) {}
    	 	}
    	 	w.close();
		}
        catch (IOException e) {
            	System.err.println("Problem writing to the file statsTest.txt");
        		}	
	 	//return numFailedQuestions;	 	
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
	
}
