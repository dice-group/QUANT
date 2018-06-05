/**
 * 
 */
package app.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.http.HttpException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

/**
 * Tests QALD Datasets against the DBpedia versions
 * @author rricha
 *
 */
public class DegradationTable {
	
	static String endpoint = "http://131.234.28.180:3030/ds/sparql";
	
	public int runQALD(String endpoint) throws FileNotFoundException, IOException, ParseException, HttpException {
		int countUnanswered =0; 
		int totalQuestions = 0;
		int correctAnswers = 0;
		JSONParser parser = new JSONParser();
		PrintWriter out = new PrintWriter(new FileWriter("src/resources/output.txt", true), true);
		JSONArray a = (JSONArray) parser.parse(new FileReader("src/resources/QALD-Datasets/JSON_QALD6_Train_dbpedia.json"));
		for(Object o : a) {
			JSONObject jsonObject = (JSONObject) o;
			String sparql = (String) jsonObject.get("sparqlQuery"); 
			String question = (String) jsonObject.get("languageToQuestion");
			
			System.out.println(question + "\n" + sparql);
			totalQuestions++;
			if (sparql != null) {
				QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, sparql);
				
				if (sparql.contains("ASK")) {
					boolean rs = qe.execAsk();
					if (String.valueOf(rs).isEmpty()) {
						countUnanswered++;
						out.write(sparql);
					}
					else {
						System.out.println("ASK: " + String.valueOf(rs));
					}
				}
				else {
					ResultSet rs = qe.execSelect();
					if (!rs.hasNext()) { //no result returned
						countUnanswered++;
						out.write(sparql);
						}
					}qe.close();
				}
			}
		out.close();
		System.out.println("Total Ques: " + totalQuestions);
		return countUnanswered;
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, HttpException {
		DegradationTable obj = new DegradationTable();
		System.out.println(obj.runQALD(endpoint)); 
	}

}
