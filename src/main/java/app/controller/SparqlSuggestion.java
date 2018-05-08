/**
 * 
 */
package app.controller;

/**
 * @author rricha
 *
 */
public class SparqlSuggestion {
	
	public String reframeSparql(String oldTriple, String newTriple, String sparqlQuery) {
		int startOfTriple = sparqlQuery.indexOf(oldTriple);
		int endOfTriple = sparqlQuery.indexOf(oldTriple) + oldTriple.length();
		
		String newSparqlQuery = sparqlQuery.substring(0, startOfTriple) + " " + newTriple + " " + sparqlQuery.substring(endOfTriple);
		return newSparqlQuery;
	}
}
