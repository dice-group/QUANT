package suggestion.query;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;

import java.util.*;

public class PredicateSuggestor {

    protected boolean predicateExists(Node predicate, Map<String,String> prefixes, String endpoint){
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
        queryStr.setNsPrefixes(prefixes);
        queryStr.append("ASK WHERE { ?s ");
        queryStr.appendNode(predicate);
        queryStr.append(" ?o . }");
        return QueryExecutionFactory.sparqlService(endpoint, queryStr.asQuery()).execAsk();
    }


}
