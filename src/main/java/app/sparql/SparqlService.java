package app.sparql;

import java.awt.List;
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

import com.github.andrewoma.dexx.collection.ArrayList;

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
		return question.startsWith("Can ") || question.startsWith("Are ") || question.startsWith("Did ") || question.startsWith("Do ") || question.startsWith("Does ") || question.startsWith("Is ") || question.startsWith("Was ");
		//return false;
	}
	
	//Get results from current endpoint
	public Set<String> getResultsFromCurrentEndpoint(String sparqlQuery) {
		Set<String> answers;
		answers = getQuery(sparqlQuery);		
		return answers;
	}
	
	
	
	/** Retrieve answer from Virtuoso current endpoint **/
	
	
	//get  type of result gathered from Virtuoso endpoint when the question has a SelectQuery
	public String getResultType(String strQuery) {		 
		 String resultType = null;
		 try {				
			 	QueryEngineHTTP qe = new QueryEngineHTTP(service, strQuery);
				qe.setTimeout(30000, TimeUnit.MILLISECONDS);
				ResultSet rs = qe.execSelect(); 
				
		        Set<String> setResult = new HashSet();
		        String szVal="";
		        while(rs.hasNext()) {
					QuerySolution s = rs.next(); 
					Iterator<String> itVars = s.varNames(); 
					while (itVars.hasNext()) {
		                resultType = itVars.next().toString(); 		                
					}
		        }		        	        
			} catch (Exception e) {
				resultType = null;
			}	
		 return resultType;
	}
	
	//get result from Virtuoso endpoint when the question has a SelectQuery
	public Set<String> getQuery(String strQuery) {
		 //String result = null;
		 Set<String> setResult = new HashSet();
		 try {
				//QueryExecution qe = QueryExecutionFactory.sparqlService(service, strQuery); //put query to jena sparql library
			 	QueryEngineHTTP qe = new QueryEngineHTTP(service, strQuery);
				qe.setTimeout(30000, TimeUnit.MILLISECONDS);
				ResultSet rs = qe.execSelect(); //execute query			
		       
		        String szVal="";
		        while(rs.hasNext()) {
					QuerySolution s = rs.next(); //get record value
					Iterator<String> itVars = s.varNames(); //get all variable of query
					while (itVars.hasNext()) {
		                String szVar = itVars.next(); //get variable of query
		                if (s.get(szVar).asNode().isURI()) { //check whether it is a URI 
		                	szVal = s.get(szVar).asNode().getURI(); // get URI value
		                }else {
		                	//szVal = s.get(szVar).asNode().getLiteralValue().toString(); //get literal value
		                	if (!s.get(szVar).isLiteral())
		    					szVal = s.get(szVar).toString();
		    				else
		    					szVal= s.getLiteral(szVar).getString();
		                }
		                //System.out.println("Answer is "+szVal);
		                setResult.add(szVal);
		            }		            
		        }
		        //result  = setResult.toString();	        
			} catch (Exception e) {
				//result = null;
				setResult.add(null);
			}	
		 return setResult;
	}
	
	//Check whether result of select query is null from current endpoint 
	public Boolean isNullAnswerFromEndpoint(String strQuery) {
		try {				
			 	QueryEngineHTTP qe = new QueryEngineHTTP(service, strQuery);
				qe.setTimeout(30000, TimeUnit.MILLISECONDS);
				ResultSet rs = qe.execSelect(); //execute query				
		        while(rs.hasNext()) {					
					 return false;
		        }		        	        
			} catch (Exception e) {
				
		}	
		return true;
	}	
	
	//get result from Virtuoso endpoint when the question has an AskQuery
	public String getResultAskQuery(String strQuery) {		
		try {
			QueryEngineHTTP qe = new QueryEngineHTTP(service, strQuery);
			qe.setTimeout(30000, TimeUnit.MILLISECONDS);
			Boolean rs = qe.execAsk(); //execute query
			String result =String.valueOf(rs);            
			qe.close();
			return result;
		}catch (Exception e) {}
		return null;
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
	
}
