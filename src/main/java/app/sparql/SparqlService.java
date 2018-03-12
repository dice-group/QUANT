package app.sparql;

import java.util.Iterator;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;

public class SparqlService {
	String service="http://dbpedia.org/sparql"; // sparql endpoint URL
	String strQuery;
	public SparqlService() {
		
	}
	/**
	 * 
	 * @param strQuery
	 * @return
	 * 
	 * desc : function to do online query
	 * 
	 */
	public String getQuery(String strQuery) {
		String result = null;
		try {
			QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); //put query to jena sparql library
			ResultSet rs = qe.execSelect(); //execute query
			String szVal="";
			while(rs.hasNext()) {
				QuerySolution s = rs.nextSolution(); //get record value
				Iterator<String> itVars = s.varNames(); //get all variable of query
	            /*** explore result answer for each variable of query **/
	            while (itVars.hasNext()) {
	                String szVar = itVars.next().toString(); //get variable of query
	                
	                if (s.get(szVar).asNode().isURI()) { //determine is URI or Literal
	                	szVal = s.get(szVar).asNode().getURI(); // get URI value
	                }else {
	                	szVal = s.get(szVar).asNode().getLiteralValue().toString(); //get literal value
	                }
	            }
	            
	            /*** only support for 1 variable query result. for multiple need to modify **/
	            result =szVal;
			}
		} catch (Exception e) {
			result = "-";
		}
		return result;
	}
	/***
	 * 
	 * @param strQuery
	 * @return
	 * 
	 * Desc : function to display sparql query nicely
	 * 
	 */
	public String getQueryFormated(String strQuery) {
		//put query to jena sparql library
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); 
		return qe.getQuery().toString();
	}
}
