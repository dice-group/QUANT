package org.zkoss.mongodb.dao;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;
import java.util.Date;

import com.google.gson.*;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;


import org.zkoss.json.JSONArray;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.mongodb.MongoDBManager;
import org.zkoss.mongodb.model.Datasets;
import org.zkoss.mongodb.model.Dataset;
import org.zkoss.mongodb.model.AnswersList;
import org.zkoss.mongodb.model.Questions;
import org.zkoss.mongodb.model.QueryList;
import org.zkoss.mongodb.model.Question;

public class CuratorDAO {
	public List<Questions> findAll(){
		List<Questions> tasks = new ArrayList<Questions>();
			try {
				/*DB db = MongoDBManager.getDB("qald");
				DBCollection coll = db.getCollection("datasets");
				DBCursor cursor = coll.find();
				
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
										
					JSONParser parser = new JSONParser();
					JSONArray datasetArray = (JSONArray)parser.parse(dbobj.get("questions").toString());
				
					for (int x=0; x<datasetArray.size(); x++){
						String objString = datasetArray.get(x).toString();
						Gson gson = new GsonBuilder().create();
						Questions datasetObj = gson.fromJson(objString, Questions.class);
						Questions task = new Questions(
							(String) datasetObj.getId(),
							(String) datasetObj.getAnswertype(),
							(String) datasetObj.getAggregation(),
							(String) datasetObj.getOnlydbo(),
							(String) datasetObj.getHybrid(),
							(List<Question>) datasetObj.getQuestion(),
							(QueryList) datasetObj.getQuery(),
							(List<AnswersList>) datasetObj.getAnswers()
							);
						tasks.add(task);
					}
				}
				
			} catch (Exception e) {}	*/
				DB db = MongoDBManager.getDB("qald");
				DBCollection coll = db.getCollection("dataset");
				DBCursor cursor = coll.find();
				
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
										
						Gson gson = new GsonBuilder().create();
						Questions datasetObj = gson.fromJson(dbobj.toString(), Questions.class);
						Questions task = new Questions(
							(String) datasetObj.getId(),
							(String) datasetObj.getAnswertype(),
							(String) datasetObj.getAggregation(),
							(String) datasetObj.getOnlydbo(),
							(String) datasetObj.getHybrid(),
							(List<Question>) datasetObj.getQuestion(),
							(QueryList) datasetObj.getQuery(),
							(List<AnswersList>) datasetObj.getAnswers()
							);
						tasks.add(task);
					
				}
				
			} catch (Exception e) {}	
			
		return tasks;
		
	}
	private BasicDBObject toBasicDBObject(Questions questions) {
		
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("id", questions.getId());
		newdbobj.put("answertype", questions.getAnswertype());
		newdbobj.put("aggregation", questions.getAggregation());
		newdbobj.put("onlydbo", questions.getOnlydbo());
		newdbobj.put("hybrid", questions.getHybrid());
		
		//definition of obj structure of question obj
		List<BasicDBObject> arrayQuestion = new ArrayList<>();
		BasicDBObject elementQuestion = new BasicDBObject();
		elementQuestion.put("language", questions.getQuestion().get(0).getLanguage());
		elementQuestion.put("string", questions.getQuestion().get(0).getString());
		elementQuestion.put("keywords", questions.getQuestion().get(0).getKeywords());
		arrayQuestion.add(elementQuestion);
		BasicDBObject queryObj = new BasicDBObject();
		newdbobj.put("question", arrayQuestion);
		
		//definition of obj structure of query obj
		queryObj.put("sparql", questions.getQuery().getSparql());
		newdbobj.put("query", queryObj);
		
		//definition of obj structure of answers obj
		//head obj
		BasicDBObject ansElementObj = new BasicDBObject();
		// array vars of head obj
		BasicDBObject varsObj = new BasicDBObject();
		List arrayVars = new ArrayList<>();
		arrayVars.add(questions.getAnswers().get(0).getHead().getVars().get(0).toString());
		varsObj.put("vars", arrayVars);
		ansElementObj.put("head", varsObj);
		
		//results obj
		//ans obj of bindings obj
		String vars = questions.getAnswers().get(0).getHead().getVars().get(0).toString();
		String ansValue = "";
		String ansType = "";
		String strUri = "uri";
		String strC = "c";
		String strString = "string";
		String strDate = "date";
		if (vars.equals(strUri)) {
			ansValue = questions.getAnswers().get(0).getResults().getBindings().get(0).getUri().getValue().toString();
			ansType = questions.getAnswers().get(0).getResults().getBindings().get(0).getUri().getValue().toString();
		}
		if (vars.equals(strC)) {
			ansValue = questions.getAnswers().get(0).getResults().getBindings().get(0).getC().getValue().toString();
			ansType = questions.getAnswers().get(0).getResults().getBindings().get(0).getC().getValue().toString();
	    }
		if (vars.equals(strString)) {
			ansValue = questions.getAnswers().get(0).getResults().getBindings().get(0).getString().getValue().toString();
			ansType = questions.getAnswers().get(0).getResults().getBindings().get(0).getString().getValue().toString();
		}
		if (vars.equals(strDate)) {
			ansValue = questions.getAnswers().get(0).getResults().getBindings().get(0).getDate().getValue().toString();
			ansType = questions.getAnswers().get(0).getResults().getBindings().get(0).getDate().getValue().toString();
		}
		BasicDBObject ansObj = new BasicDBObject();
		BasicDBObject elementAns = new BasicDBObject();
		elementAns.put("type", ansType);
		elementAns.put("value", ansValue);
		ansObj.put(vars, elementAns);
		
		//bindings obj of results obj
		BasicDBObject bindingsObj = new BasicDBObject();
		List<BasicDBObject> arrayBindings = new ArrayList<>();
		arrayBindings.add(ansObj);
		bindingsObj.put("bindings", arrayBindings);
		ansElementObj.put("results", bindingsObj);
		
		//answers obj
		List arrayAnswers = new ArrayList<>();
		arrayAnswers.add(ansElementObj);
		newdbobj.put("answers", arrayAnswers);
		
		return newdbobj;
	}
	public void update(Questions questions) {
		try {
			BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id", questions.getId());
			BasicDBObject newDbObj = toBasicDBObject(questions);
			
			DB db = MongoDBManager.getDB("qald");
			DBCollection coll = db.getCollection("dataset");
			
			coll.update(searchObj, newDbObj);
			
		}catch (Exception e) {}
	}
	public void delete(Questions questions) {
		try {
			BasicDBObject newdbobj = toBasicDBObject(questions);
			DB db = MongoDBManager.getDB("qald");
			DBCollection coll = db.getCollection("dataset");
			coll.remove(newdbobj);
		}catch (Exception e) {}
	}
}
