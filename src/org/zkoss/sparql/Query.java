package org.zkoss.sparql;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class Query {
	String service="http://dbpedia.org/sparql";
	String strQuery;
	public Query() {
		
	}
	public String getQuery(String strQuery) {
		String result = null;
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery);
		ResultSet rs = qe.execSelect();
		while(rs.hasNext()) {
			QuerySolution s = rs.nextSolution();
			result = s.toString();
		}
		return result;
	}
}
