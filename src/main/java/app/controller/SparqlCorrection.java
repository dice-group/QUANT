package app.controller;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class SparqlCorrection {
	String endpoint="http://dbpedia.org/sparql"; // sparql endpoint URL
	
	private static final String[] BLACKLIST = { "UNION", "OPTIONAL", "{", "}"};
	
	//case1: property change 
	public String findNewProperty(String queryString) throws ParseException{
		
		String resultantSparql = new String();
		int prefixEnd = 0;
		String queryStringLowercased = queryString.toLowerCase();
		
		if (queryStringLowercased.contains("select"))
			prefixEnd = queryStringLowercased.indexOf("select");
		else 
			prefixEnd = queryStringLowercased.indexOf("ask");
			
		String prefixString = queryString.substring(0, prefixEnd);
		
		
		
		//extract triples from the SPARQL query
		String triples = queryString.substring(queryString.indexOf("{")+1, queryString.lastIndexOf("}"));
		
		//remove everything that can't be spo
		for (String blacklisted : BLACKLIST) { 
			triples = triples.replace(" " + blacklisted + " ", " ").trim();
			}
		if (triples.contains("FILTER")) {
			int removeFrom = triples.indexOf("FILTER");
			triples = triples.substring(0, removeFrom);
		}
		
		String[] splitTriples = triples.split("\\s+[\\.]\\s+|\\s+[\\.]|\\; ");
		String subject = new String();
		String predicate = new String(), object = new String();
		
		//extract subject, predicate and object from the triple 
		for (int i= 0; i < splitTriples.length ; i++) {
			String modifiedQuery = prefixString + " select distinct ?p where { ";
			String[] words = splitTriples[i].split("\\s+");
			
			//the triple just has s,p,o and either s or o is unknown
			if (words.length == 3 && ((words[0].startsWith("?") && !words[2].startsWith("?")) || (!words[0].startsWith("?") && words[2].startsWith("?")))  ) {  
				subject = words[0];
				predicate = words[1];
				object = words[2];
				modifiedQuery = modifiedQuery + subject + " ?p " + object + " }";
				System.out.println(modifiedQuery);
			}
			
			//subject remains the same as before
			else if (words.length ==2 && ((subject.startsWith("?") && !words[1].startsWith("?") || (!subject.startsWith("?") && words[1].startsWith("?")))) ) { 
				predicate = words[0];
				object = words[1];
				modifiedQuery = modifiedQuery + subject + " ?p " + object + " }";
				System.out.println(modifiedQuery);
			}
			
			//if both s and o unknown, include the previous splitTriple
			else if (words.length == 3 && i > 0) {
				subject = words[0];
				predicate = words[1];
				object = words[2];
				modifiedQuery = modifiedQuery + splitTriples[i-1] + " . " + subject + " ?p " + object + " }";
				System.out.println(modifiedQuery);
			}
			else if (words.length == 2 && i > 0) {
				predicate = words[0];
				object = words[1];
				modifiedQuery = modifiedQuery + splitTriples[i-1] + " . " + subject + " ?p " + object + " }";
				System.out.println(modifiedQuery);
			}
			//extract that string from the predicate which has to be matched against the results from sparql endpoint
			int indexOfColon = predicate.indexOf(":");
			String predMatch = predicate.substring(indexOfColon+1);
			System.out.println(predMatch);
			QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, modifiedQuery); //put query to jena sparql library
			ResultSet rs = qe.execSelect(); //execute query
			List<String> result = new ArrayList<>();
			while (rs.hasNext()) {
                 QuerySolution s = rs.nextSolution();
                 //String message;     
                 Iterator<String> varNames = s.varNames();
                 for (Iterator<String> it = varNames; it.hasNext(); ) {
                     String varName = it.next();
                     if (s.get(varName).toString().contains(predMatch)) {
                    	 result.add(s.get(varName).toString());
                     }
                 }
			}
			System.out.println(result);
            if(result.isEmpty()) { //property missing case
            	resultantSparql = "The predicate " + predicate + " is missing in " + splitTriples[i];
            	return resultantSparql;
            }
            else {
            	for (String str : result) {
            		int predAt = str.indexOf(predMatch);
            		String prefix = str.substring(0, predAt);
            		//if the prefixes don't match, there is a change in the property
            		if  (!prefixString.contains(prefix)) {
            			return (subject + str + object);
            		}
            	}
            }
		}
//		for (String split : splits)
//			System.out.println(split);
		
//		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, modifiedQuery); //put query to jena sparql library
//		ResultSet rs = qe.execSelect(); //execute query
//		//QueryExecution qe = qef.createQueryExecution(queryString); 
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		
		return resultantSparql;
	}
	
	public static void main(String[] args) throws ParseException {
		SparqlCorrection sc = new SparqlCorrection();
		//String queryString = "SELECT DISTINCT ?n WHERE { <http://dbpedia.org/resource/FC_Porto> <http://dbpedia.org/ontology/ground> ?x . ?x <http://dbpedia.org/ontology/seatingCapacity> ?n .}";
		//String queryString = "PREFIX res: <http://dbpedia.org/resource/> select distinct ?s ?x where {  res:New_Delhi dbo:country ?s ; dbo:areaCode ?x .}";
		String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>PREFIX dbp: <http://dbpedia.org/property/>PREFIX res: <http://dbpedia.org/resource/>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>SELECT DISTINCT ?uriWHERE {        ?uri rdf:type dbo:Film .        ?uri dbo:director res:Akira_Kurosawa .      { ?uri dbo:releaseDate ?x . }       UNION       { ?uri dbp:released ?x . }        res:Rashomon dbo:releaseDate ?y .        FILTER (?y > ?x)}";
		System.out.println(sc.findNewProperty(queryString));
	}
}
