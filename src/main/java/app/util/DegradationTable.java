/**
 * 
 */
package app.util;

import java.io.File;
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
	
	static String endpoint = "http://dbpedia.org/sparql";
	
	public void runQALD(String endpoint) throws FileNotFoundException, IOException, ParseException, HttpException {
		
		JSONParser parser = new JSONParser();
		File myDir = new File("src/resources/QALD-Datasets/");
		PrintWriter out = new PrintWriter(new FileWriter("src/resources/output.txt", true), true);
		File[] dirContent = myDir .listFiles();
		for (File f : dirContent)
		{	
		    if (!f.isDirectory() && f.getName().contains("Train"))
		    {
		        // process file.
		    	int countUnanswered =0; 
				int totalQuestions = 0;
				int correctAnswers = 0;
		    	System.out.println(f.getName());
		    	JSONArray a = (JSONArray) parser.parse(new FileReader("src/resources/QALD-Datasets/" + f.getName()));
				for(Object o : a) {
					JSONObject jsonObject = (JSONObject) o;
					String sparql = (String) jsonObject.get("sparqlQuery"); 
					String question = (String) jsonObject.get("languageToQuestion");
					
					System.out.println(f.getName()+ " " + question + "\n" + sparql);
					totalQuestions++;
					if (sparql != null) {
						QueryEngineHTTP qe = new QueryEngineHTTP(endpoint, sparql);
						
						if (sparql.contains("ASK")) {
							boolean rs = qe.execAsk();
							if (String.valueOf(rs).isEmpty()) {
								countUnanswered++;
								//out.write(sparql);
							}
							else {
								System.out.println("ASK: " + String.valueOf(rs));
							}
						}
						else {
							ResultSet rs = qe.execSelect();
							if (!rs.hasNext()) { //no result returned
								countUnanswered++;
								//out.write(sparql);
								}
							}qe.close();
						}
					}
				//out.close();
				out.append("File: "+ f.getName()+ " Total Ques: " + totalQuestions + " Unanswered Questions: " + countUnanswered + "\n");
				//System.out.println("File: "+ f.getName()+ " Total Ques: " + totalQuestions + "Unanswered Questions: " + countUnanswered);
		    }
		    
		}
		out.close();
		
	}
	
	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException, HttpException {
		DegradationTable obj = new DegradationTable();
		obj.runQALD(endpoint); 
	}

}
