package app.controller;

import org.apache.commons.collections4.iterators.PermutationIterator;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
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
	
	String endpoint="http://dbpedia.org/sparql"; // sparql endpoint URL
	
	private SparqlSuggestion suggest;
	
	public SparqlCorrection() {
		suggest = new SparqlSuggestion();
	}
	
	private static final String[] BLACKLIST = { "UNION", "}"};
	
	
	public Boolean doesQueryWork(String oldTriple, String newTriple, String sparqlQuery, int numOfEntitiesInOldTriple) {
		int startOfTriple = sparqlQuery.indexOf(oldTriple);
		int endOfTriple = sparqlQuery.indexOf(oldTriple) + oldTriple.length();
		
		//The newTriple will always have 3 entities. if oldTriple has 2, it doesn't have a subject.
		if (numOfEntitiesInOldTriple == 2) {
			String[] entities = newTriple.split(" ");
			newTriple = entities[1] + " " + entities[2];
		}
		
		String newSparqlQuery = sparqlQuery.substring(0, startOfTriple) + " " + newTriple + " " + sparqlQuery.substring(endOfTriple);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, newSparqlQuery); //put query to jena sparql library
		ResultSet rs = qe.execSelect(); // only for SELECT queries TODO: handle ASK queries
		
		if(rs.hasNext())
			return true;
		
		return false;
	}
	
	/**
	 * Permutes the order of triples and runs the pipeline again.
	 * @param allTriples
	 * @param queryString
	 * @return sparqlSuggestion from permuted sparqlQuery
	 * @throws ParseException
	 */
	
	public List<String> queryPermutation(String[] allTriples, String queryString) throws ParseException {
		List<String> primaryTriples = new ArrayList<>();
		String prefixString = queryString.substring(0, queryString.indexOf("{")+1);
		String newSparqlQuery = prefixString;
		for (int i =0; i < allTriples.length ; i++) {
			if(!allTriples[i].startsWith("{")) {
				primaryTriples.add(allTriples[i]);
			}
		}
		PermutationIterator<String> perm = new PermutationIterator<>(primaryTriples);
		Collection<String> permutedQuery = new ArrayList<String>();
		while (perm.hasNext()) {
			permutedQuery= perm.next();
			if (permutedQuery.size() == primaryTriples.size() && !permutedQuery.equals(primaryTriples)) {
				for (int i =0; i <permutedQuery.size() ; i++) {
					newSparqlQuery += permutedQuery.toArray()[i] + " . ";
				}
				newSparqlQuery += "} ";
				List<String> result = findNewProperty(newSparqlQuery);
				if (!result.isEmpty())
					return result;
			}
		}
		return null;
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
		//System.out.println(query);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query); //put query to jena sparql library
		boolean rs = qe.execAsk(); //execute query
		
		if (rs == true) {
			return null;
		}
		
		return entityToBeChecked;
	}
	
	/**
	 * Handles redirects i.e. checks if the entity name has got changed.
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
		//System.out.println(query);
		QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
		ResultSet rs = qe.execSelect(); //execute query
		String changedName = new String();
		
		while (rs.hasNext()) {
             QuerySolution s = rs.nextSolution();     
             Iterator<String> varNames = s.varNames();
             for (Iterator<String> it = varNames; it.hasNext(); ) {
                 String varName = it.next();
                 changedName = s.get(varName).toString();
             }
		}
		if(!changedName.isEmpty()) {
			if (entityToBeChecked == subject) {
				return changedName + " " + predicate + " " + object;
			}
			return subject + " " + predicate + " " + changedName;
		}
		else return null;
	}
	
	/**
	 * Handles change in predicate. 
	 * If there's no similar predicate found, checks if there's a change in any of the entities, 
	 * 
	 * @param queryString
	 * @return
	 * @throws ParseException
	 */
	public List<String> findNewProperty(String queryString) throws ParseException{
		List<String> changedTriples = new ArrayList<>();
		String newTriple = new String();
		int prefixEnd = 0;
		String queryStringLowercased = queryString.toLowerCase();
		String suggestionQuery = queryString;
		
		if (queryStringLowercased.contains("select"))
			prefixEnd = queryStringLowercased.indexOf("select");
		else 
			prefixEnd = queryStringLowercased.indexOf("ask");
			
		String prefixString = queryString.substring(0, prefixEnd);
		String alternatePrefixString = new String(); //in case the query has no prefix 
		//System.out.println(prefixString);
		
		//extract triples from the SPARQL query
		String triples = queryString.substring(queryString.indexOf("{")+1, queryString.lastIndexOf("}"));
		
		//remove everything that can't be spo
		for (String blacklisted : BLACKLIST) { 
			triples = triples.replaceAll(" " + blacklisted, " ").trim();
			//System.out.println(triples);
			}
		if (triples.contains("FILTER")) {
			int removeFrom = triples.indexOf("FILTER");
			triples = triples.substring(0, removeFrom);
			//System.out.println(triples);
		}
		
		String[] splitTriples = triples.split("\\s+[\\.]\\s+|\\s+[\\.]|\\; |\\s+[\\{]\\s+|OPTIONAL ");
		String subject = new String();
		String predicate = new String(), object = new String();
		int numOfEntitiesInOldTriple = 0; //for sparql suggestion function 
		String previousTriple = null;
		
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
				//modifiedQuery = modifiedQuery + splitTriples[i-1] + " . " + subject + " ?p " + object + " }";
				modifiedQuery = modifiedQuery + previousTriple + " . " + subject + " ?p " + object + " }";
				//System.out.println(modifiedQuery);
			}
			else if (words.length == 2 && i > 0) {
				predicate = words[0];
				object = words[1];
				//modifiedQuery = modifiedQuery + splitTriples[i-1] + " . " + subject + " ?p " + object + " }";
				modifiedQuery = modifiedQuery + previousTriple + " . " + subject + " ?p " + object + " }";
				splitTriples[i] = subject + " " + splitTriples[i];
				System.out.println(modifiedQuery + "\n" + splitTriples[i]);
			}
			
			previousTriple = splitTriples[i];
			//extract that string from the predicate which has to be matched against the results from sparql endpoint
			int propertyBeginsAt = 0;
			String predMatch = new String();
			// for preds like <http://dbpedia.org/ontology/ground>
			if (predicate.startsWith("<")) {
				propertyBeginsAt = predicate.lastIndexOf("/");
				predMatch = predicate.substring(propertyBeginsAt+1, predicate.length()-1);
				alternatePrefixString = predicate.substring(1, propertyBeginsAt+1);
				//System.out.println(alternatePrefixString);
			}
			//for preds with abbrev. (rdf:type)
			else {
				propertyBeginsAt = predicate.indexOf(":");
				predMatch = predicate.substring(propertyBeginsAt+1);
			}
			
			QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, modifiedQuery); //put query to jena sparql library
			ResultSet rs = qe.execSelect(); //execute query
			List<String> predicatesMatched = new ArrayList<>();
			List<String> possiblePredicates = new ArrayList<>();
			
 			while (rs.hasNext()) {
                 QuerySolution s = rs.nextSolution();     
                 Iterator<String> varNames = s.varNames();
                 for (Iterator<String> it = varNames; it.hasNext(); ) {
                     String varName = it.next();
                     //adds to result only if the value contains predMatch string
                     if (s.get(varName).toString().contains(predMatch)) {
                    	 predicatesMatched.add(s.get(varName).toString());
                     }
                     //if there is a change in the predicate itself!
                     if (s.get(varName).toString().toLowerCase().contains(predMatch.toLowerCase())){
                    	 possiblePredicates.add(s.get(varName).toString());
                     }
                 }
			}
			//System.out.println(result);
			
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
	            			if (!queryString.contains(splitTriples[i])) {
	            				suggestionQuery = suggest.reframeSparql(predicate + " " + object, returnedTriple, suggestionQuery, numOfEntitiesInOldTriple);
	            			}
	            			else 
	            				suggestionQuery= suggest.reframeSparql(splitTriples[i], returnedTriple, suggestionQuery, numOfEntitiesInOldTriple);
	            		}
	            		else {
	            			changedTriples.add("The entity " + entityMissing + " is missing in " + splitTriples[i]);
	            			break;
	            		}
	            	}
//	            	else {
//	            		//check if entity's name has changed
//	            		String returnedTriple = checkEntityName(prefixString, subject, predicate, object);
//	            		if (returnedTriple != null) {
//	                		//changedTriples.add(returnedTriple);
//	            			if (!queryString.contains(splitTriples[i])) {
//	            				suggestionQuery = suggest.reframeSparql(predicate + " " + object, returnedTriple, suggestionQuery, numOfEntitiesInOldTriple);
//	            			}
//	            			else 
//	            				suggestionQuery= suggest.reframeSparql(splitTriples[i], returnedTriple, suggestionQuery, numOfEntitiesInOldTriple);
//	            	}
            	
            		else { 
            			//property missing; return the possible predicates
            			for (String pred : possiblePredicates) {
            				//changedTriples.add(subject + " <" + pred + "> " + object);
            				String changedTriple = subject + " <" + pred + "> " + object;
            				previousTriple = changedTriple;
            				if (!queryString.contains(splitTriples[i])) {
                				suggestionQuery= suggest.reframeSparql(predicate + " " + object, changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
                			}
                			else 
                				suggestionQuery= suggest.reframeSparql(splitTriples[i], changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
            			}
            			if (possiblePredicates.isEmpty())
            				changedTriples.add("The predicate " + predicate + " is missing in " + splitTriples[i]);
            		}
            	
            }
            else {
            	Boolean queryWorks = false;
            	for (String str : predicatesMatched) {
            		int predAt = str.indexOf(predMatch);
            		String prefix = str.substring(0, predAt);
            		
            		if (!prefixString.isEmpty() && prefixString.contains(prefix) || !alternatePrefixString.isEmpty() && alternatePrefixString.equals(prefix)) {
            			//prefix found!!
            			prefixFound = true;
            			//check if placing it in the query makes the query work because it might not work with the latter part of the query
            			if (!queryString.contains(splitTriples[i])) {
            				queryWorks = doesQueryWork(predicate + " " + object, subject + " <" + str + "> " + object , suggestionQuery, numOfEntitiesInOldTriple);
            			}
            			else 
            				queryWorks = doesQueryWork(splitTriples[i], subject + " <" + str + "> " + object , suggestionQuery, numOfEntitiesInOldTriple);
            			
            			if (queryWorks==false)
            				prefixFound= false; //consider the prefix to be missing
            		}
//            		//if the prefixes don't match, there is a change in the property
//            		else if  (!prefixString.isEmpty() && !prefixString.contains(prefix)) {
//            			changedTriples.add(subject + " " + str + " " + object);
//            		}
//            		else if (!alternatePrefixString.isEmpty() && !alternatePrefixString.equals(prefix)) {
//            			changedTriples.add(subject + " " + str + " " + object);
//            		}
            	}
            	if (prefixFound == false) {
            		for (String str: predicatesMatched) {
            			int predAt = str.indexOf(predMatch);
                		String prefix = str.substring(0, predAt);
                		
            			if  (!prefixString.isEmpty() && !prefixString.contains(prefix)) {
            				String changedTriple = subject + " <" + str + "> " + object;
            				previousTriple = changedTriple;
            				if (!queryString.contains(splitTriples[i])) {
                				suggestionQuery= suggest.reframeSparql(predicate + " " + object, changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
                			}
                			else 
                				suggestionQuery= suggest.reframeSparql(splitTriples[i], changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
                		}
                		else if (!alternatePrefixString.isEmpty() && !alternatePrefixString.equals(prefix)) {
                			String changedTriple = subject + " <" + str + "> " + object;
                			previousTriple = changedTriple;
                			if (!queryString.contains(splitTriples[i])) {
                				suggestionQuery = suggest.reframeSparql(predicate + " " + object, changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
                			}
                			else 
                				suggestionQuery = suggest.reframeSparql(splitTriples[i], changedTriple, suggestionQuery, numOfEntitiesInOldTriple);
                		}
            		}
            	}
                	
            }
            
		}
		//if there's a suggestion to make
		if (!suggestionQuery.equals(queryString))
			changedTriples.add(suggestionQuery);
		
		//if there's no suggestion, then permute the order of triples and check. 
		if (changedTriples.isEmpty()) {
			changedTriples= queryPermutation(splitTriples, queryString);
		}
		
		return changedTriples;
	}
	
	public static void main(String[] args) throws ParseException {
		SparqlCorrection sc = new SparqlCorrection();
		//String queryString = "SELECT DISTINCT ?n WHERE { <http://dbpedia.org/resource/FC_Porto> <http://dbpedia.org/ontology/ground> ?x . ?x <http://dbpedia.org/ontology/seatingCapacity> ?n .}";
		//String queryString = "PREFIX res: <http://dbpedia.org/resource/> PREFIX dbp: <http://dbpedia.org/property/> select distinct ?s ?x where {  res:New_Delhi dbp:country ?s ; dbo:areaCode ?x .}";
		//String queryString = "PREFIX dbo: <http://dbpedia.org/ontology/>PREFIX dbp: <http://dbpedia.org/property/>PREFIX res: <http://dbpedia.org/resource/>PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#>SELECT DISTINCT ?uriWHERE {        ?uri rdf:type dbo:Film .        ?uri dbo:director res:Akira_Kurosawa .      { ?uri dbo:releaseDate ?x . }       UNION       { ?uri dbp:released ?x . }        res:Rashomon dbo:releaseDate ?y .        FILTER (?y > ?x)}";
		//String queryString = "PREFIX  dbpedia2: <http://dbpedia.org/property/> PREFIX  res:  <http://dbpedia.org/resource/> SELECT  ?date WHERE { res:Germany  dbpedia2:accessioneudate  ?date }";
		String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  res:  <http://dbpedia.org/resource/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX  onto: <http://dbpedia.org/ontology/> SELECT DISTINCT ?uri ?string WHERE { ?uri rdf:type yago:EuropeanCountries ; onto:governmentType res:Constitutional_monarchy OPTIONAL { ?uri rdfs:label ?string FILTER ( lang(?string) = 'en' ) } }";
		//property change example 
		//String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX prop: <http://dbpedia.org/property/> SELECT DISTINCT ?uri ?string WHERE { ?person rdfs:label \"Tom Hanks\"@en ; prop:spouse ?string OPTIONAL { ?uri rdfs:label ?string }}";  
		//triple-order example 
		//String queryString = "PREFIX rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX rdf: <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX onto: <http://dbpedia.org/ontology/> SELECT ?date WHERE { ?website rdf:type onto:Software ; onto:releaseDate ?date; rdfs:label \"DBpedia\"@en . }";
		//String queryString = "PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  foaf: <http://xmlns.com/foaf/0.1/> SELECT  ?uri WHERE  { ?subject  rdfs:label     \"Tom Hanks\"@en ;          foaf:homepage  ?uri }";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT ?uri ?string WHERE { ?uri rdf:type yago:CapitalsInEurope OPTIONAL { ?uri rdfs:label ?string FILTER ( lang(?string) = \"en\" ) }  }";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT DISTINCT  ?uri ?string WHERE { ?uri  rdf:type  yago:BirdsOfTheUnitedStates  OPTIONAL { ?uri  rdfs:label  ?string FILTER ( lang(?string) = \"en\" ) } }";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#>  PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> PREFIX  prop: <http://dbpedia.org/property/>  SELECT  ?uri ?string WHERE { ?uri rdf:type yago:FemaleHeadsOfGovernment ; prop:office ?office FILTER regex(?office, \"Chancellor of Germany\")OPTIONAL{ ?uri rdfs:label ?string FILTER ( lang(?string) = \"en\" )}}";
		//String queryString = "PREFIX  yago: <http://dbpedia.org/class/yago/> PREFIX  rdfs: <http://www.w3.org/2000/01/rdf-schema#> PREFIX  dbpedia2: <http://dbpedia.org/property/> PREFIX  rdf:  <http://www.w3.org/1999/02/22-rdf-syntax-ns#> SELECT  ?uri ?string WHERE { ?uri  rdf:type  yago:StatesOfTheUnitedStates ; dbpedia2:densityrank ?density OPTIONAL { ?uri rdfs:label ?string FILTER ( lang(?string) = \"en\" )}} ORDER BY ASC(?density) LIMIT 1";
		System.out.println(sc.findNewProperty(queryString));
	}
}