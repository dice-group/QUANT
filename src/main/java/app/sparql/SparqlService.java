package app.sparql;

import java.util.HashSet;
import java.util.Iterator;

import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QuerySolution;
import org.apache.jena.query.ResultSet;
import org.apache.jena.query.ResultSetFormatter;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;

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
	 * desc : function to retrieve answer from Virtuoso current endpoint
	 * 
	 */
	
	public Boolean isASKQuery(String question) {
		// Compare to source from:
		// src/main/java/org/aksw/hawk/controller/Cardinality.java

		// From train query set: (better to use keyword list!)
		// (Root [-> first child])
		// VBG -> VBZ (Does)
		// VBZ (Is)
		// ADD -> VB (Do)
		// VBP (Are)
		// VBD (Was)
		// VB -> VBD (Did)
		// VBN -> VBD (Was)
		// VB -> VBZ (Does)
		// VBN -> VBZ (Is)

		// regex: ^(Are|D(id|o(es)?)|Is|Was)( .*)$
		return question.startsWith("Are ") || question.startsWith("Did ") || question.startsWith("Do ") || question.startsWith("Does ") || question.startsWith("Is ") || question.startsWith("Was ");
		//return false;
	}
	
	//get result from Virtuoso endpoint when the question has a SelectQuery
	public String getQuery(String strQuery) {
		 String result = null;
		 try {
				//QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); //put query to jena sparql library
			 	QueryEngineHTTP qe = new QueryEngineHTTP(service, strQuery);
				qe.setTimeout(30000, TimeUnit.MILLISECONDS);
				ResultSet rs = qe.execSelect(); //execute query
				//System.out.println("This is "+qe.getQuery());
		        //ResultSetFormatter.out(rs);
		        //System.out.println(rs.toString());
		        Set<String> setResult = new HashSet();
		        String szVal="";
		        while(rs.hasNext()) {
					QuerySolution s = rs.next(); //get record value
					Iterator<String> itVars = s.varNames(); //get all variable of query
					while (itVars.hasNext()) {
		                String szVar = itVars.next(); //get variable of query
		                if (s.get(szVar).asNode().isURI()) { //determine is URI or Literal
		                	szVal = s.get(szVar).asNode().getURI(); // get URI value
		                }else {
		                	//szVal = s.get(szVar).asNode().getLiteralValue().toString(); //get literal value
		                	if (!s.get(szVar).isLiteral())
		    					szVal = s.get(szVar).toString();
		    				else
		    					szVal= s.get(szVar).asLiteral().getValue().toString();
		                }
		                setResult.add(szVal);
		            }		            
		        }
		        result  = setResult.toString();
				/*String szVal="";
				Set<String> setResult = new HashSet();;
				while(rs.hasNext()) {
					QuerySolution s = rs.nextSolution(); //get record value
					Iterator<String> itVars = s.varNames(); //get all variable of query
		            //*** explore result answer for each variable of query **//*
		            while (itVars.hasNext()) {
		                String szVar = itVars.next().toString(); //get variable of query
		                
		                if (s.get(szVar).asNode().isURI()) { //determine is URI or Literal
		                	szVal = s.get(szVar).asNode().getURI(); // get URI value
		                }else {
		                	szVal = s.get(szVar).asNode().getLiteralValue().toString(); //get literal value
		                }
		                setResult.add(szVal);
		            }		            
		            //*** only support for 1 variable query result. for multiple need to modify **//*
		            result =setResult.toString();
				}*/
		        
			} catch (Exception e) {
				result = null;
			}	
		 return result;
	}
	
	//get result from Virtuoso endpoint when the question has an AskQuery
	public Boolean getResultAskQuery(String strQuery) {			
	
		/*try {
				QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); //put query to jena sparql library
				qe.setTimeout(30000, TimeUnit.MILLISECONDS);
				Boolean result = qe.execAsk(); //execute query	
				qe.close();			
				return result;			
			} catch (Exception e) {}*/
		try {
			QueryEngineHTTP qe = new QueryEngineHTTP(service, strQuery);
			qe.setTimeout(30000, TimeUnit.MILLISECONDS);
			Boolean result = qe.execAsk(); //execute query
			ResultSetFormatter.out(result);
			qe.close();
			return result;
		}catch (Exception e) {}
		return false;
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
		try {
		QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); 
		return qe.getQuery().toString();
		} catch (Exception e) {
			return strQuery;
		}
	}
	
	public void getAllPossibleProperty(String StrQuery) {
		
	}
}
