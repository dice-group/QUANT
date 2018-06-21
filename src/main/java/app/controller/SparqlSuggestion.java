/**
 * 
 */
package app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author rricha
 *
 */
public class SparqlSuggestion {
	
		public List<String> reframeSparql(String oldTriple, String newTriple, String sparqlQuery, int numOfEntitiesInOldTriple) {
		List<String> newSparql = new ArrayList<>(); //first element in the list -> sparqlQuery ; second element -> changed Triple
		int startOfTriple = sparqlQuery.indexOf(oldTriple);
		int endOfTriple = sparqlQuery.indexOf(oldTriple) + oldTriple.length();
		
		//The newTriple will always have 3 entities. if oldTriple has 2, it doesn't have a subject.
		if (numOfEntitiesInOldTriple == 2) {
			String[] entities = newTriple.split(" ");
			newTriple = entities[1] + " " + entities[2];
		}
		
		String newSparqlQuery = sparqlQuery.substring(0, startOfTriple) + " " + newTriple + " " + sparqlQuery.substring(endOfTriple);
		newSparql.add(newSparqlQuery);
		newSparql.add(newTriple);
		return newSparql;
	}

}
