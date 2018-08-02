package app.util;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import arq.sparql;

/**
 * For checking outdated queries between dbpedia 2016-04 and 2016-10 versions. 
 **/

public class EvaluateLCQuAD {
	
	static String endpoint1="http://dbpedia.org/sparql"; // for 2016-10
	static String endpoint2="http://131.234.28.180:3030/ds/sparql"; // for 2016-04
	
	public int runLcquadQueries(String endpoint) throws FileNotFoundException, IOException, ParseException {
		int countUnanswered =0; 
		int totalQuestions = 0;
		JSONParser parser = new JSONParser();
		JSONArray a = (JSONArray) parser.parse(new FileReader("src/resources/lcquad.json"));
		for(Object o : a) {
			JSONObject jsonObject = (JSONObject) o;
			String question = (String) jsonObject.get("question/0/string");
			
			String sparql = (String) jsonObject.get("query/sparql"); 
			//System.out.println(question + "\n" + sparql);
			totalQuestions++;
			QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, sparql);
			//QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, sparql); //put query to jena sparql library
			//qe.setTimeout(20, TimeUnit.SECONDS);
	
			
			if (sparql.contains("ASK")) {
				boolean rs = qe.execAsk();
				//System.out.println(String.valueOf(rs).isEmpty());
				if (String.valueOf(rs).isEmpty()) {
					//System.out.println("Ask");
					countUnanswered++;
				}
			}
			else {
				ResultSet rs = qe.execSelect();
				if (!rs.hasNext()) { //no result returned
					//System.out.println("no answer!");
					countUnanswered++;
				}
//				while (rs.hasNext()) {
//	                 QuerySolution s = rs.nextSolution();     
//	                 Iterator<String> varNames = s.varNames();
//	                 for (Iterator<String> it = varNames; it.hasNext(); ) {
//	                     String varName = it.next();
//	                     System.out.println(s.get(varName).toString());
//	                 }

//					}
				}qe.close();
			}
		System.out.println("Total Ques: " + totalQuestions);System.gc();
		return countUnanswered;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		EvaluateLCQuAD obj = new EvaluateLCQuAD();
		System.out.println(obj.runLcquadQueries(endpoint1)); // 2570 unanswered out of 5000 queries
		System.out.println(obj.runLcquadQueries(endpoint2)); // 456 unanswered
	}
 }
