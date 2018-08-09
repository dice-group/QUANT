package app.controller;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.jena.atlas.logging.Log;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Vector;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryParseException;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

import app.controller.SparqlSuggestion;


/**
 * Reasons for Sparql Query to not work:
 * 1) Property changes: Returns a new predicate if there is a change in 'prefix' part of the predicate. Suggestion: new sparql query
 * 2) Property not present anymore: (reason could be)  Suggestion : Remove the question
 * 		a) Entity Missing : Checks if the corresponding entity is present
 * 		b) Property removed : Informs about the same
 * 3) Entity's renamed : Checks for redirects and returns the new name. Suggestion: new sparql query
 * 4) In case of hybrid queries: use of homophones/wrong spellings!    
 * @author rricha
 * 
 */

public class SparqlCorrection {
	private static final Logger logger = LoggerFactory.getLogger(SparqlCorrection.class);
	
	String endpoint="http://dbpedia.org/sparql"; // sparql endpoint URL
	static boolean insidePermutation = false;
	private SparqlSuggestion suggest;
	
	public SparqlCorrection() {
		suggest = new SparqlSuggestion();
	}
	
	private static final String[] BLACKLIST = { "UNION", "}"};
	private static final Map<String, String> standardPrefixes;
	static {
		Map<String, String> aMap = new HashMap<>();
        aMap.put("rdfs", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        aMap.put("rdf", "<http://www.w3.org/1999/02/22-rdf-syntax-ns#>");
        aMap.put("dbo", "<http://dbpedia.org/ontology/>");
        aMap.put("dbp", "<http://dbpedia.org/property/>");
        aMap.put("xsd", "<http://www.w3.org/2001/XMLSchema#>");
        aMap.put("foaf", "<http://xmlns.com/foaf/0.1/>");
        aMap.put("yago", "<http://dbpedia.org/class/yago/>");
        aMap.put("res", "<http://dbpedia.org/resource/>");
        standardPrefixes = Collections.unmodifiableMap(aMap);
	}
	
	
	public String checkMissingPrefix(String queryString) {
		String missingPrefix = null; 
		boolean exceptionCaught = false; 
		do {
			try {
				QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, queryString);
				if(queryString.contains("ASK") || queryString.contains("ask")) {
					Boolean rs = qe.execAsk();
					exceptionCaught = false;
				}
				else {
					ResultSet rs = qe.execSelect();
					exceptionCaught = false;
				}
			}
			catch(QueryParseException e) {
				exceptionCaught = true;
				logger.info(e.getMessage());
				if (e.getMessage().contains("Unresolved prefixed name:")) {
					int startIndex = e.getMessage().indexOf("Unresolved prefixed name:") + "Unresolved prefixed name:".length();
					missingPrefix = e.getMessage().substring(startIndex, e.getMessage().lastIndexOf(":")).trim();
					System.out.println(missingPrefix);
					if(standardPrefixes.containsKey(missingPrefix)) {
						String prefixString = "PREFIX " + missingPrefix + ": " + standardPrefixes.get(missingPrefix) + " ";
						queryString = prefixString + queryString;
					}
				} else {
					//TODO there can be a also a @Non-group key variable in SELECT and this case needs to be handled 
					/*
					 * SELECT ?uri WHERE {  ?airline <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Airline> .  ?airline <http://dbpedia.org/property/frequentFlyer> ?uri.  ?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/class/yago/FrequentFlyerPrograms> . } ORDER BY DESC(COUNT(DISTINCT ?airline)) OFFSET 0 LIMIT 1  
					 */
					
					exceptionCaught = false;
				}
			}
		}while(exceptionCaught == true);
		System.out.println(queryString);
		return queryString;
	}
	
	public Boolean doesQueryWork(String query) {
		try {
			QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
			if(query.contains("ASK") || query.contains("ask")) {
				Boolean rs = qe.execAsk();
				if (rs != null) {
					return true;
				}
			}
			else {
				ResultSet rs = qe.execSelect();
				if(rs.hasNext())
					return true;
			}
		}
		catch(QueryParseException e) {
			logger.info(e.getMessage());;
		}
		return false;
		
	}
	
	public Boolean doesQueryWorkWithNewTriple(String oldTriple, String newTriple, String sparqlQuery, int numOfEntitiesInOldTriple) {
		int startOfTriple = sparqlQuery.indexOf(oldTriple);
		int endOfTriple = sparqlQuery.indexOf(oldTriple) + oldTriple.length();
		
		//The newTriple will always have 3 entities. if oldTriple has 2, it doesn't have a subject.
		if (numOfEntitiesInOldTriple == 2) {
			String[] entities = newTriple.split(" ");
			newTriple = entities[1] + " " + entities[2];
		}
		
		String newSparqlQuery = sparqlQuery.substring(0, startOfTriple) + " " + newTriple + " " + sparqlQuery.substring(endOfTriple);
		return doesQueryWork(newSparqlQuery);
	}
	
	/**
	 * Permutes the order of triples and runs the pipeline again.
	 * @param allTriples
	 * @param queryString
	 * @return sparqlSuggestion from permuted sparqlQuery
	 * @throws ParseException
	 */
	
	public Map<String, List<String>> queryPermutation(String[] allTriples, String queryString) throws ParseException {
		List<String> primaryTriples = new ArrayList<>();
		String prefixString = queryString.substring(0, queryString.indexOf("{")+1);
		Map<String, List<String>> result = new HashMap<>();
		Map<String, List<String>> tempResult = new HashMap<>();
		List<String> correctionCase = new ArrayList<>();
		
		for (int i =0; i < allTriples.length ; i++) {
			if(!allTriples[i].startsWith("{")) {
				primaryTriples.add(allTriples[i]);
			}
		}
		
		PermutationIterator<String> perm = new PermutationIterator<>(primaryTriples);
		Collection<String> permutedQuery = new ArrayList<String>();
		
		while (perm.hasNext()) {
			String newSparqlQuery = prefixString;
			permutedQuery= perm.next();
			if (permutedQuery.size() == primaryTriples.size() && !permutedQuery.equals(primaryTriples)) {
				for (int i =0; i <permutedQuery.size() ; i++) {
					newSparqlQuery += permutedQuery.toArray()[i] + " . ";
				}
				newSparqlQuery += "} ";
				
				tempResult = findNewProperty(newSparqlQuery);
				if (!tempResult.isEmpty()) {
					for (String key : tempResult.keySet()) {
						correctionCase = tempResult.get(key);
						correctionCase.add("query permutation");
						result.put(key, correctionCase);
					}
					return result;
				}
			}
		}
		
		if(queryString.contains("yago")) {
			correctionCase.add("Unhandled Yago Class");
		}
		else {
			correctionCase.add("unhandled case");
		}
		result.put("No suggestion", correctionCase);
		return result;
	}
	
	/**
	 * This function is called if property is missing 
	 * @param prefixString
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return entity if it's not present, else null. 
	 */
	public String isEntityPresent(String prefixString, String subject, String predicate, String object) {
		String entityToBeChecked = subject; 
		String query = prefixString;
		if (subject.startsWith("?") && !object.startsWith("?")) {
			entityToBeChecked = object;
			query += "ASK { VALUES (?r) { ("+ entityToBeChecked+ " ) } { ?r ?p ?o } UNION { ?s ?r ?o } UNION { ?s ?p ?r }} ";
		}
		else if (object.startsWith("?") && !subject.startsWith("?")){
		 query +=  "ASK { VALUES (?r) { ("+ entityToBeChecked+ " ) } { ?r ?p ?o } UNION { ?s ?r ?o } UNION { ?s ?p ?r }} ";
		}
		else 
			return null;
		try {
			QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query); //put query to jena sparql library
			boolean rs = qe.execAsk(); //execute query
			
			if (rs == true) {
				return null;
			}
		}
		catch(QueryParseException e) {
			logger.info(e.getMessage());;
		}
		return entityToBeChecked;
	}
	
	/**
	 * Handles redirects i.e. checks if the entity name has got changed and handles yago classes.
	 * @param prefixString
	 * @param subject
	 * @param predicate
	 * @param object
	 * @return new triple if a redirect is found, else null. 
	 */
	public String checkEntityName(String prefixString, String subject, String predicate, String object) {
		String newYagoClass = null; 
		if (subject.contains("yago")) {
			subject = subject.substring(0, subject.indexOf(':')+1) + "Wikicat" + subject.substring(subject.indexOf(':')+1);
			newYagoClass = isEntityPresent(prefixString, subject, predicate, object);
			if (newYagoClass == null)	//entity present 
				return subject + " " + predicate + " " + object;

		}
		else if (object.contains("yago")) {
			object = object.substring(0, object.indexOf(':')+1) + "Wikicat" + object.substring(object.indexOf(':')+1);
			newYagoClass = isEntityPresent(prefixString, subject, predicate, object);
			if (newYagoClass == null) //entity present  
				return subject + " " + predicate + " " + object;
		}
		
		// if both subject and object are not yago classes, check for redirection
		String query = prefixString;
		String entityToBeChecked = subject;
		if (subject.startsWith("?") && !object.startsWith("?")) {
			entityToBeChecked = object;
			query += "select ?redirect where { " + entityToBeChecked + " <http://dbpedia.org/ontology/wikiPageRedirects> ?redirect. }";
		}
		else if (object.startsWith("?") && !subject.startsWith("?")) {
			query += "select ?redirect where { " + entityToBeChecked + " <http://dbpedia.org/ontology/wikiPageRedirects> ?redirect. }";
		}
		else 
			return null;
		
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet rs = qe.execSelect(); //execute query
		String changedEntityName = new String();
		
		while (rs.hasNext()) {
             QuerySolution s = rs.nextSolution();     
             Iterator<String> varNames = s.varNames();
             for (Iterator<String> it = varNames; it.hasNext(); ) {
                 String varName = it.next();
                 changedEntityName = s.get(varName).toString();
             }
		}
		if(!changedEntityName.isEmpty()) {
			if (entityToBeChecked == subject) {
				return changedEntityName + " " + predicate + " " + object;
			}
			return subject + " " + predicate + " " + changedEntityName;
		}
		else return null;
	}
	
	/**
	 * Handles change in predicate. 
	 * If there's no similar predicate found, checks if there's a change in any of the entities, 
	 * This func. runs the entire sparql correction pipeline. 
	 * @param queryString
	 * @return sparql suggestion (if any) and its corresponding correction cases
	 * @throws ParseException
	 */
	public Map<String, List<String>> findNewProperty(String queryString) throws ParseException{
		Map<String, List<String>> result = new HashMap<>();
		List<String> correctionCase = new ArrayList<>();
		
		//checks for missing prefix
		queryString = checkMissingPrefix(queryString);
		if (doesQueryWork(queryString)==true) {
			correctionCase.add("Prefix missing");
			result.put(queryString, correctionCase);
			return result;
		}
		
		String suggestionQuery = queryString;
		List<String> suggestionList = new ArrayList<>();
		int prefixEnd = 0;
		
		String queryStringLowercased = queryString.toLowerCase();
		
		if (queryStringLowercased.contains("select"))
			prefixEnd = queryStringLowercased.indexOf("select");
		else 
			prefixEnd = queryStringLowercased.indexOf("ask");
			
		String prefixString = queryString.substring(0, prefixEnd);
		String alternatePrefixString = new String(); //in case the query has no prefix 
		
		//extract triples from the SPARQL query
		String triples = queryString.substring(queryString.indexOf("{")+1, queryString.lastIndexOf("}"));
		
		//remove everything that can't be spo
		for (String blacklisted : BLACKLIST) { 
			triples = triples.replaceAll(" " + blacklisted, " ").trim();
			}
		
		if (triples.contains("FILTER")) {
			int removeFrom = triples.indexOf("FILTER");
			triples = triples.substring(0, removeFrom);
		}
		
		String[] splitTriples = triples.split("\\s+[\\.]\\s+|\\s+[\\.]|\\; |\\s+[\\{]\\s+|OPTIONAL ");
		String subject = new String();
		String predicate = new String(), object = new String();
		int numOfEntitiesInOldTriple = 0; //for sparql suggestion function 
		String previousTriple = null; // holds the current value of the previous triple 
		
		//extract subject, predicate and object from the triple 
		for (int i= 0; i < splitTriples.length ; i++) {
			splitTriples[i] = splitTriples[i].replace("{", "").trim();
			System.out.println(i + " " + splitTriples[i]);
			String modifiedQuery = prefixString + " select distinct ?p where { ";
			String[] words = splitTriples[i].split("[ ]+(?=([^\"]*\"[^\"]*\")*[^\"]*$)");
			numOfEntitiesInOldTriple = words.length;
			
			//the triple just has s,p,o and either s or o is unknown
			if (words.length == 3 && ((words[0].startsWith("?") && !words[2].startsWith("?")) || (!words[0].startsWith("?") && words[2].startsWith("?")))  ) {  
				subject = words[0];
				predicate = words[1];
				object = words[2];
				modifiedQuery = modifiedQuery + subject + " ?p " + object + " }";
				//System.out.println(modifiedQuery);
			}
			
			//subject remains the same as before, so add it in the triple as well.
			else if (words.length ==2 && ((subject.startsWith("?") && !words[1].startsWith("?") || (!subject.startsWith("?") && words[1].startsWith("?")))) ) { 
				predicate = words[0];
				object = words[1];
				modifiedQuery = modifiedQuery + subject + " ?p " + object + " }";
				splitTriples[i] = subject + " " + splitTriples[i];
				//System.out.println(modifiedQuery + "\n" + splitTriples[i]);
			}
			
			//if both s and o unknown, include the previous splitTriple
			else if (words.length == 3 && i > 0) {
				subject = words[0];
				predicate = words[1];
				object = words[2];
				modifiedQuery = modifiedQuery + previousTriple + " . " + subject + " ?p " + object + " }";
				//System.out.println(modifiedQuery);
			}
			else if (words.length == 2 && i > 0) {
				predicate = words[0];
				object = words[1];
				modifiedQuery = modifiedQuery + previousTriple + " . " + subject + " ?p " + object + " }";
				splitTriples[i] = subject + " " + splitTriples[i];
				//System.out.println(modifiedQuery + "\n" + splitTriples[i]);
			}
			else 
				return result;
			
			
			previousTriple = splitTriples[i]; 
			
			//extract that string from the predicate which has to be matched against the results from sparql endpoint
			int propertyBeginsAt = 0;
			String predicateTobeMatched = new String();
			String origPrefixOfPredicate = new String();
			
			// for preds like <http://dbpedia.org/ontology/ground>
			if (predicate.startsWith("<")) {
				propertyBeginsAt = predicate.lastIndexOf("/");
				predicateTobeMatched = predicate.substring(propertyBeginsAt+1, predicate.length()-1);
				alternatePrefixString = predicate.substring(1, propertyBeginsAt+1);
			}
			//for preds with abbrev. (rdf:type)
			else {
				propertyBeginsAt = predicate.indexOf(":");
				predicateTobeMatched = predicate.substring(propertyBeginsAt+1);
				origPrefixOfPredicate = predicate.substring(0, propertyBeginsAt);
			}
			
			QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, modifiedQuery); 
			ResultSet rs = qe.execSelect(); 
			List<String> predicatesMatched = new ArrayList<>();
			List<String> possiblePredicates = new ArrayList<>();
			
 			while (rs.hasNext()) {
                 QuerySolution s = rs.nextSolution();     
                 Iterator<String> varNames = s.varNames();
                 for (Iterator<String> it = varNames; it.hasNext(); ) {
                     String varName = it.next();
                     //adds to result only if the value contains predMatch string
                     if (s.get(varName).toString().contains(predicateTobeMatched)) {
                    	 predicatesMatched.add(s.get(varName).toString());
                     }
                     //if there is a change in the predicate itself!
                     if (s.get(varName).toString().toLowerCase().contains(predicateTobeMatched.toLowerCase())){
                    	 possiblePredicates.add(s.get(varName).toString());
                     }
                 }
			}
			
 			String currentTriple = splitTriples[i]; //holds current value of the triple to be changed
			Boolean prefixFound = false;
			
			//analyze the result obtained
            if(predicatesMatched.isEmpty()) { 
            	//property missing case
        		
	            	//check if entity is missing
	            	String entityMissing = isEntityPresent(prefixString, subject, predicate, object); 
	            	
	            	if(entityMissing != null) {
	            		//If entity is suspected to be missing, check if it is present with another name; check if entity's name has changed using 'redirect'
	            		
	            		String returnedTriple = checkEntityName(prefixString, subject, predicate, object);
	            		
	            		if (returnedTriple != null) {
	            			previousTriple = returnedTriple;
	            			if (!suggestionQuery.contains(currentTriple)) 
	            				currentTriple = currentTriple.substring(currentTriple.indexOf(" ")+1);
	            				
	            			suggestionList= suggest.reframeSparql(currentTriple, returnedTriple, suggestionQuery, numOfEntitiesInOldTriple);
	            			
	            			suggestionQuery = suggestionList.get(0);
            				currentTriple = suggestionList.get(1);
            				correctionCase.add("Entity change");
            				
            				if(doesQueryWork(suggestionQuery) == true) {
            					result.put(suggestionQuery, correctionCase);
            					return result;
            				}
	            		}	
	            	
	            		else {
	            			correctionCase.add("Entity Missing");
	            			result.put("The entity " + entityMissing + " is missing in " + splitTriples[i], correctionCase);
	            			return result;
	            		}
	            	}
            		else { 
            			//check for the possible predicates
            			for (String pred : possiblePredicates) {
            				String changedTriple = subject + " <" + pred + "> " + object;
            				previousTriple = changedTriple;
            				if (!suggestionQuery.contains(currentTriple)) {
            					currentTriple = currentTriple.substring(currentTriple.indexOf(" ")+1);
            				}
                			 
                			suggestionList= suggest.reframeSparql(currentTriple, changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
            				
                			suggestionQuery = suggestionList.get(0);
            				currentTriple = suggestionList.get(1);
            				correctionCase.add("Property change");
            				
            				if(doesQueryWork(suggestionQuery) == true) {
            					result.put(suggestionQuery, correctionCase);
            					return result;
            				}
            			}
            			if (possiblePredicates.isEmpty()) {
            				correctionCase.add("Property missing");
            				result.put("The predicate " + predicate + " is missing in " + splitTriples[i], correctionCase);
            				return result;
            			}
            		}
	            	
            }
            else { //if  !predicatesMatched.isEmpty
            	Boolean queryWorks = false;
            	
            	for (String str : predicatesMatched) {
            		int predAt = str.indexOf(predicateTobeMatched);
            		String newPrefixForPredicate = str.substring(0, predAt);
            		
            		if (!prefixString.isEmpty() && prefixString.contains(newPrefixForPredicate)) {
            			
            			//check if this prefix in prefixString is associated with the predicate phrase 
            			if(prefixString.contains(newPrefixForPredicate)) {
            				int expandedPrefixAt = prefixString.indexOf("PREFIX " + origPrefixOfPredicate);
            				String tempString = prefixString.substring(expandedPrefixAt); //contains string from the desired point to the end of prefixString
            				String expandedPrefix = tempString.substring(tempString.indexOf("<")+1, tempString.indexOf(">"));
            				
            				if (!expandedPrefix.equals(newPrefixForPredicate)) {
            					if (!suggestionQuery.contains(currentTriple)) 
            						currentTriple = currentTriple.substring(currentTriple.indexOf(" ")+1);
                    				
            					suggestionList = suggest.reframeSparql(currentTriple, subject + " <" + str + "> " + object , suggestionQuery, numOfEntitiesInOldTriple);
            					suggestionQuery = suggestionList.get(0);
            					currentTriple = suggestionList.get(1);
                				correctionCase.add("Property change");
                				
                				if(doesQueryWork(suggestionQuery)==true) {
                    				result.put(suggestionQuery, correctionCase);
                    				return result;
                    			}
            				}
            				prefixFound = false;
            				
            			}
            		}
            		else if (!alternatePrefixString.isEmpty() && alternatePrefixString.equals(newPrefixForPredicate)){
            			prefixFound = true;
            			//check if placing it in the query makes the query work because it might not work with the latter part of the query
            			if (!suggestionQuery.contains(currentTriple)) 
            				currentTriple = currentTriple.substring(currentTriple.indexOf(" ")+1);
        
            			queryWorks = doesQueryWorkWithNewTriple(currentTriple, subject + " <" + str + "> " + object , suggestionQuery, numOfEntitiesInOldTriple);
            			
            			if (queryWorks==false)
            				prefixFound = false; //consider the prefix to be missing
            		}
            	
            	}
            	if (prefixFound == false) {
            		
            		for (String str: predicatesMatched) {
            			int predAt = str.indexOf(predicateTobeMatched);
                		String prefix = str.substring(0, predAt);
                		
            			if  (!prefixString.isEmpty() && !prefixString.contains(prefix) || !alternatePrefixString.isEmpty() && !alternatePrefixString.equals(prefix)) {
            				String changedTriple = subject + " <" + str + "> " + object;
            				previousTriple = changedTriple;
            				if (!suggestionQuery.contains(currentTriple)) 
            					currentTriple = currentTriple.substring(currentTriple.indexOf(" ")+1); //entire string except the first word  
                				
                			suggestionList= suggest.reframeSparql(currentTriple, changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
                			
            				suggestionQuery = suggestionList.get(0);
            				currentTriple = suggestionList.get(1);
            				correctionCase.add("Property change");
                		}
            			if(doesQueryWork(suggestionQuery)==true) {
            				result.put(suggestionQuery, correctionCase);
            				return result;
            			}
            			
            		}
            	}
                	
            }
             
		}
		//if there's a suggestion to make
//		if (!suggestionQuery.equals(queryString))
//			return result;
			
		
		//if there's no suggestion, then permute the order of triples and check. 
		if (result.isEmpty() && insidePermutation == false) {
			insidePermutation = true;
			result = queryPermutation(splitTriples, queryString);
		}
		
		return result;
	}
	
	public static void main(String[] args) throws ParseException {
		SparqlCorrection sc = new SparqlCorrection();
		//String queryString = "SELECT DISTINCT ?n WHERE { <http://dbpedia.org/resource/FC_Porto> <http://dbpedia.org/ontology/ground> ?x . ?x <http://dbpedia.org/ontology/seatingCapacity> ?n .}";
		//String queryString = "SELECT DISTINCT ?uri WHERE {  ?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Film> .  ?uri <http://dbpedia.org/ontology/director> <http://dbpedia.org/resource/Steven_Spielberg> .  ?uri <http://dbpedia.org/ontology/budget> ?b .  FILTER( xsd:double(?b) >= 8.0E7 ) } ";
		//String queryString = "SELECT DISTINCT ?uri WHERE {  ?uri foaf:surname 'Baldwin'@en . { ?uri dbo:occupation <http://dbpedia.org/resource/Actor> . } UNION { ?uri <http://www.w3.org/1999/02/22-rdf-syntax-ns#type> <http://dbpedia.org/ontology/Actor> . } }";
		//result to be checked/discussed!
		//String queryString ="SELECT DISTINCT ?uri WHERE {  <http://dbpedia.org/resource/Ganges> <http://dbpedia.org/property/sourceCountry> ?l . ?uri <http://www.w3.org/2000/01/rdf-schema#label> ?l . ?uri rdf:type <http://dbpedia.org/ontology/Country> . }";
		//String queryString= "select distinct ?s ?x where {  res:New_Delhi dbp:country ?s ; dbo:areaCode ?x .}";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX  onto: <http://dbpedia.org/ontology/> SELECT DISTINCT  ?uri ?string WHERE { ?states  rdf:type      yago:StatesOfTheUnitedStates ; onto:capital  ?uri OPTIONAL { ?uri  rdfs:label  ?string FILTER ( lang(?string) = \"en\" ) }}";
		String queryString = "PREFIX yago: <http://dbpedia.org/class/yago/> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX dbpedia2: <http://dbpedia.org/property/> SELECT ?uri ?string WHERE {?uri rdf:type yago:StatesOfTheUnitedStates . ?uri dbpedia2:densityrank ?density OPTIONAL {?uri rdfs:label ?string. FILTER (lang(?string) = 'en') }} ORDER BY ASC(?density) LIMIT 1";
		
		//String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/> PREFIX dbp: <http://dbpedia.org/property/>PREFIX res: <http://dbpedia.org/resource/>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>SELECT DISTINCT ?uriWHERE {        ?uri rdf:type dbo:Film .        ?uri dbo:director res:Akira_Kurosawa .      { ?uri dbo:releaseDate ?x . }       UNION       { ?uri dbp:released ?x . }        res:Rashomon dbo:releaseDate ?y .        FILTER (?y > ?x)}";
		//String queryString = "PREFIX  dbpedia2: <http://dbpedia.org/property/> PREFIX  res:  <http://dbpedia.org/resource/> SELECT  ?date WHERE { res:Germany  dbpedia2:accessioneudate  ?date }";
		//String queryString = "PREFIX yago: <http://dbpedia.org/class/yago/> PREFIX res: <http://dbpedia.org/resource/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX onto: <http://dbpedia.org/ontology/> SELECT DISTINCT ?uri ?string WHERE { ?uri rdf:type yago:EuropeanCountries ; onto:governmentType res:Constitutional_monarchy OPTIONAL { ?uri rdfs:label ?string FILTER ( lang(?string) = 'en' ) } }";
		//property change example 
		//String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX prop: <http://dbpedia.org/property/> SELECT DISTINCT ?uri ?string WHERE { ?person rdfs:label \"Tom Hanks\"@en ; prop:spouse ?string OPTIONAL { ?uri rdfs:label ?string }}";  
		//triple-order example 
		//String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX onto: <http://dbpedia.org/ontology/> SELECT ?date WHERE { ?website rdf:type onto:Software ; onto:releaseDate ?date; rdfs:label \"DBpedia\"@en . }";
		//String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX foaf: <http://xmlns.com/foaf/0.1/> SELECT  ?uri WHERE  { ?subject  rdfs:label     \"Tom Hanks\"@en ;          foaf:homepage  ?uri }";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?uri ?string WHERE { ?uri rdf:type yago:CapitalsInEurope OPTIONAL { ?uri rdfs:label ?string FILTER ( lang(?string) = \"en\" ) }  }";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT  ?uri ?string WHERE { ?uri  rdf:type  yago:BirdsOfTheUnitedStates  OPTIONAL { ?uri  rdfs:label  ?string FILTER ( lang(?string) = \"en\" ) } }";
		//String queryString = "PREFIX yago: <http://dbpedia.org/class/yago/> PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX prop: <http://dbpedia.org/property/>  SELECT  ?uri ?string WHERE { ?uri rdf:type yago:FemaleHeadsOfGovernment ; prop:office ?office FILTER regex(?office, \"Chancellor of Germany\")OPTIONAL{ ?uri rdfs:label ?string FILTER ( lang(?string) = \"en\" )}}";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  dbpedia2: <http://dbpedia.org/property/> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT  ?uri ?string WHERE { ?uri  rdf:type  yago:StatesOfTheUnitedStates ; dbpedia2:densityrank ?density OPTIONAL { ?uri rdfs:label ?string FILTER ( lang(?string) = \"en\" )}} ORDER BY ASC(?density) LIMIT 1";
		System.out.println(sc.findNewProperty(queryString));
	}
}