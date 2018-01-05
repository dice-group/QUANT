package org.zkoss.mongodb.dao;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import org.bson.Document;
import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.JSONValue;
import org.zkoss.json.parser.JSONParser;
import org.zkoss.mongodb.MongoDBManager;
import org.zkoss.mongodb.model.Question;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;


public class QuestionDAO {
	public List<Question> findAll(){
		List<Question> questions = new ArrayList<Question>();
		try {
				DB db = MongoDBManager.getDB("qald");
				DBCollection coll = db.getCollection("dataset");
				DBCursor cursor = coll.find();
				//System.out.println(cursor.hasNext());
			while (cursor.hasNext()) {
				DBObject dbobj = cursor.next();
				String sparql = (String) ((DBObject) dbobj.get("query")).get("sparql");
				JSONParser parser = new JSONParser();
				JSONArray array = (JSONArray)parser.parse(dbobj.get("question").toString());
				
				String strquestion = (String) ((JSONObject)array.get(0)).get("string");
				String keyword = (String) ((JSONObject)array.get(0)).get("keywords");
				String language = (String) ((JSONObject)array.get(0)).get("language");
				
				JSONArray arrayAnswer = (JSONArray)parser.parse(dbobj.get("answers").toString());
				
				String strResults = (String) ((JSONObject)arrayAnswer.get(0)).get("results").toString();

				Question question = new Question(
						(String) dbobj.get("question").toString(),
						(String) sparql, 
						(String) strResults,
						(String) dbobj.get("answertype").toString(),
						(String) dbobj.get("aggregation").toString(),
						(String) dbobj.get("onlydbo").toString(),
						(String) dbobj.get("hybrid").toString(),
						(String) strquestion,
						(String) keyword,
						(String) language,
						(String) dbobj.get("id").toString()
						);
				questions.add(question);
				
			}
			
		} catch (Exception e) {}
		return questions;
	}

}
