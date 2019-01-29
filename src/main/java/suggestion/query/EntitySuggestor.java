package suggestion.query;

import org.apache.jena.graph.Node;
import org.apache.jena.query.*;

import java.util.Map;

public class EntitySuggestor {
    protected boolean entityExists(Node resource, Map<String,String> prefixes, String endpoint){
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
        queryStr.setNsPrefixes(prefixes);
        queryStr.append("ASK WHERE {{");
        queryStr.appendNode(resource);
        queryStr.append("?p ?o . }UNION{?s ?p ");
        queryStr.appendNode(resource);
        queryStr.append(". }}");
        return QueryExecutionFactory.sparqlService(endpoint, queryStr.asQuery()).execAsk();
    }
    protected boolean isWikicat(Node resource, Map<String,String> prefixes, String endpoint){
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
        if(!prefixes.containsKey("yago"))
            prefixes.put("yago","http://dbpedia.org/class/yago/");
        queryStr.setNsPrefixes(prefixes);
        queryStr.append("ASK WHERE { {");
        queryStr.append("yago:Wikicat"+resource.getLocalName());
        queryStr.append(" ?p ?o . } UNION {?s ?p ");
        queryStr.append("yago:Wikicat"+resource.getLocalName());
        queryStr.append(". } }");
        return QueryExecutionFactory.sparqlService(endpoint, queryStr.asQuery()).execAsk();
    }
    private ResultSet getRedirects(Node resource, Map<String,String> prefixes, String endpoint,String sameAsPredicate){
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
        queryStr.setNsPrefixes(prefixes);
        queryStr.append("select ?redirect where {");
        queryStr.appendNode(resource);
        queryStr.append(" "+sameAsPredicate+" ?redirect . }");
        return QueryExecutionFactory.sparqlService(endpoint, queryStr.asQuery()).execSelect();
    }
    protected String generateCandidateEntitiesNew(Node node, Map<String,String> prefixes, String endpoint,String sameAsPredicate){
        ResultSet candidates=getRedirects(node,prefixes,endpoint,sameAsPredicate);
        if (candidates.hasNext())
            return candidates.next().get("?redirect").toString();
        return null;

    }
}
