package suggestion.metadata;

import org.apache.jena.query.*;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Logger;

public class MetadataSuggestor {
    private boolean onlyDbo(Query query) {
        //Find easier solution
        List<String> notDboURIs=new ArrayList<>();
        ElementWalker.walk(query.getQueryPattern(),
                new ElementVisitorBase() {
                    public void visit(ElementPathBlock el) {
                        Iterator<TriplePath> triples = el.patternElts();
                        while (triples.hasNext()) {
                            TriplePath triple = triples.next();
                            if(triple.getSubject().isURI()&&triple.getSubject().toString().startsWith("http://dbpedia.org/")){
                                notDboURIs.add(triple.getSubject().toString());
                                return;
                            }
                            if(triple.getObject().isURI()&&triple.getObject().toString().startsWith("http://dbpedia.org/")){
                                notDboURIs.add(triple.getObject().toString());
                                return;
                            }
                        }

                    }
                }
        );
        return notDboURIs.size()>0;
    }
    public MetadataSuggestions getMetadataSuggestions(String queryString, String endpoint,boolean endpointReachable ){
        MetadataSuggestions metadataSuggestions = new MetadataSuggestions();
        if(queryString.contains("text:query")||queryString.contains("bif:contains"))
            metadataSuggestions.setHybrid(true);
        try {
            Query query = QueryFactory.create(queryString);
            if (query.getAggregators().size() > 0)
                metadataSuggestions.setAggregation(true);
            metadataSuggestions.setOnlyDbo(onlyDbo(query));
            if (query.isAskType())
                metadataSuggestions.setAnswerType("boolean");
            else {
                //Assumes, that all answers have the same type, and there is only one uri to be matched
                ResultSet rs=null;
                if(endpointReachable) {
                    try {
                        QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
                        qe.setTimeout(5000);
                        rs = ResultSetFactory.copyResults(qe.execSelect());
                        qe.close();
                    } catch (Exception e) {
                        endpointReachable=false;
                        e.printStackTrace();
                    }
                }


                if (endpointReachable&&rs.hasNext()) {
                    metadataSuggestions.setOutOfScope(false);
                    QuerySolution solution = rs.next();
                    if (solution.get(solution.varNames().next()).isResource())
                        metadataSuggestions.setAnswerType("resource");
                    else {
                        if (solution.get(solution.varNames().next()).isLiteral()) {
                            String l = solution.get(solution.varNames().next()).asLiteral().getDatatypeURI();
                            if (l.equals("http://www.w3.org/2001/XMLSchema#date"))
                                metadataSuggestions.setAnswerType("date");
                            else if ("http://www.w3.org/2001/XMLSchema#decimal".equals(l) ||
                                    "http://www.w3.org/2001/XMLSchema#int".equals(l) ||
                                    "http://www.w3.org/2001/XMLSchema#integer".equals(l) ||
                                    "http://www.w3.org/2001/XMLSchema#long".equals(l))
                                metadataSuggestions.setAnswerType("number");
                            else metadataSuggestions.setAnswerType("string");
                        } else metadataSuggestions.setAnswerType("unknown");
                    }
                } else {
                    metadataSuggestions.setAnswerType("unknown");
                    metadataSuggestions.setOutOfScope(true);
                }
            }
            return metadataSuggestions;
        }catch  (QueryParseException e){
            metadataSuggestions.setOutOfScope(true);
            return metadataSuggestions;
        }
    }
}
