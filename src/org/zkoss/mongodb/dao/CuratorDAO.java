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
				DB db = MongoDBManager.getDB("qald");
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
				
			} catch (Exception e) {}	
			
		return tasks;
		
	}
	private BasicDBObject toBasicDBObject(Questions task) {
		BasicDBObject newdbobj = new BasicDBObject();

		newdbobj.put("id", task.getId());
		newdbobj.put("answertype", task.getAnswertype());
		newdbobj.put("aggregation", task.getAggregation());
		newdbobj.put("onlydbo", task.getOnlydbo());
		newdbobj.put("hybrid", task.getHybrid());
		return newdbobj;
	}
}
