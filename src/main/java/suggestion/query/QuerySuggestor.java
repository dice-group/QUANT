package suggestion.query;

import org.apache.jena.graph.Node;
import org.apache.jena.graph.NodeFactory;
import org.apache.jena.query.*;
import org.apache.jena.shared.PrefixMapping;
import org.apache.jena.sparql.core.TriplePath;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import org.apache.jena.sparql.engine.binding.BindingFactory;
import org.apache.jena.sparql.syntax.ElementPathBlock;
import org.apache.jena.sparql.syntax.ElementVisitorBase;
import org.apache.jena.sparql.syntax.ElementWalker;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class QuerySuggestor {
    //default: DBpedia Redirect
    private String sameAsEdge = "<http://dbpedia.org/ontology/wikiPageRedirects>";
    //default
    private Boolean useWikicat = true;
    public QuerySuggestor(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/quant.properties"));
            String envsameAsEdge = System.getenv("QUANT_SAME_AS_EDGE");
            sameAsEdge = envsameAsEdge != null ? envsameAsEdge : prop.getProperty("sameAsEdge");
            String envUseWikicat = System.getenv("QUANT_USE_WIKICAT");
            useWikicat = Boolean.valueOf(envUseWikicat != null ? envUseWikicat : prop.getProperty("useWikikat"));
        }
        catch (IOException ex) {
            System.out.println("QUANT properties not found use default settings");
        }
    }
    private List<TriplePath> getAllTriples(Query query) {
        List<TriplePath>tripleList=new ArrayList<>();
        ElementWalker.walk(query.getQueryPattern(),
                new ElementVisitorBase() {
                    public void visit(ElementPathBlock el) {
                        Iterator<TriplePath> triples = el.patternElts();
                        while (triples.hasNext()) {
                            tripleList.add(triples.next());
                        }

                    }
                }
        );
        return tripleList;
    }
    private boolean tripleExists(TriplePath triple, Map<String,String> prefixes, String endpoint){
        ParameterizedSparqlString queryStr = new ParameterizedSparqlString();
        queryStr.setNsPrefixes(prefixes);
        queryStr.append("ASK WHERE { ");
        queryStr.appendNode(triple.getSubject());
        queryStr.append(" ");
        queryStr.appendNode(triple.getPredicate());
        queryStr.append(" ");
        queryStr.appendNode(triple.getObject());
        queryStr.append(" }");
        return QueryExecutionFactory.sparqlService(endpoint, queryStr.asQuery()).execAsk();
    }
    private String generateQueryResources(String queryString,HashMap<String,String>correctedResources){
        for(String resource:correctedResources.keySet()){
            if(queryString.contains("<"+resource+">"))
                queryString = queryString.replace("<"+resource+">","<"+correctedResources.get(resource)+">");
            else queryString = queryString.replace(resource, "<"+correctedResources.get(resource)+">");
        }
        return queryString;
    }
    private String generateQueryPredicates(String queryString, List<Node>missingPredicates, PrefixMapping prefixMapping, String endpoint,String result /*List<Var>vars, List<Binding>bindings*/){
        String oldQuery = queryString;
        Map<Node,String>missingPredicateMapping = new HashMap<>();
        queryString = queryString.replace("DISTINCT","");
        for(Node predicate:missingPredicates){
            String varname = "?pmissing"+missingPredicates.indexOf(predicate);
            missingPredicateMapping.put(predicate,varname);
            if(queryString.contains("<"+predicate.toString(prefixMapping)+">"))
                queryString = queryString.replace("<"+predicate.toString(prefixMapping)+">",varname);
            else queryString = queryString.replace(predicate.toString(prefixMapping), varname);
        }
        Query q = QueryFactory.create(queryString);
        q.getResultVars().get(0);
        List<Var>vars= new ArrayList<Var>();
        Var uri =Var.alloc(q.getResultVars().get(0));
        vars.add(uri);
        List<Binding> bindings=new ArrayList<Binding>();
        if(result.contains("http://")||result.contains("https://"))
            bindings.add(BindingFactory.binding(uri, NodeFactory.createURI(result)));
        else {
            bindings.add(BindingFactory.binding(uri, NodeFactory.createLiteral(result)));
        }
        q.setValuesDataBlock(vars,bindings);
        for(String var:missingPredicateMapping.values())
            q.addResultVar(var);
        ResultSet rs = QueryExecutionFactory.sparqlService(endpoint, q).execSelect();
        if(rs.hasNext()){
            QuerySolution bestFitting = rs.next();
            int maxscore=getNumberOfFittingPredicates(bestFitting,missingPredicateMapping);
            while (rs.hasNext()) {
                QuerySolution qs = rs.next();
                int score=getNumberOfFittingPredicates(qs,missingPredicateMapping);
                if(score>maxscore){
                    maxscore=score;
                    bestFitting=qs;
                }
            }
            for(Node predicate:missingPredicateMapping.keySet()){
                queryString = queryString.replace(missingPredicateMapping.get(predicate),"<"+bestFitting.get(missingPredicateMapping.get(predicate)).toString()+">");
            }
            return queryString;
        }
        return oldQuery;
    }
    private int getNumberOfFittingPredicates(QuerySolution querySolution,Map<Node,String>missingPredicateMapping){
        int score =0;
        for(Node predicate:missingPredicateMapping.keySet()){
            if(querySolution.get(missingPredicateMapping.get(predicate)).toString().equals(predicate.toString())||
                    querySolution.get(missingPredicateMapping.get(predicate)).toString().contains(predicate.toString())||
                    predicate.toString().contains(querySolution.get(missingPredicateMapping.get(predicate)).toString()))
                score++;
        }
        return score;
    }
    public void correct(QuerySuggestions suggestions, String queryString, String endpoint, String result){
        PredicateSuggestor predicateSuggestor=new PredicateSuggestor();
        EntitySuggestor entitySuggestor=new EntitySuggestor();
        Query query = QueryFactory.create(queryString);
        List<TriplePath>tripleList = getAllTriples(query);
        List<Node>missingPredicates = new ArrayList<>();
        HashMap<String,String>correctedResources = new HashMap<>();
        for(TriplePath triple:tripleList) {
            if (!tripleExists(triple, query.getPrefixMapping().getNsPrefixMap(), endpoint)) {
                boolean correctionPossible=false;
                if (triple.getPredicate().isURI() && !predicateSuggestor.predicateExists(triple.getPredicate(), query.getPrefixMapping().getNsPrefixMap(), endpoint)) {
                    suggestions.getMissingPredicates().add(triple.getPredicate().toString());
                    if (!missingPredicates.contains(triple.getPredicate().toString(query.getPrefixMapping())))
                        missingPredicates.add(triple.getPredicate());
                    correctionPossible =true;
                }
                if (triple.getSubject().isURI()) {
                    if (!entitySuggestor.entityExists(triple.getSubject(), query.getPrefixMapping().getNsPrefixMap(), endpoint)){
                        suggestions.getMissingResources().add(triple.getSubject().toString());
                        if(useWikicat&&entitySuggestor.isWikicat(triple.getSubject(), query.getPrefixMapping().getNsPrefixMap(), endpoint)) {
                            correctedResources.put(triple.getSubject().toString(query.getPrefixMapping()), "http://dbpedia.org/class/yago/Wikicat" + triple.getSubject().getLocalName());
                            correctionPossible =true;
                        }else return;
                    } else if(!correctionPossible){
                        String candidatesResource = entitySuggestor.generateCandidateEntitiesNew(triple.getSubject(), query.getPrefixMapping().getNsPrefixMap(), endpoint, sameAsEdge);
                        if (candidatesResource != null) {
                            correctedResources.put(triple.getSubject().toString(query.getPrefixMapping()), candidatesResource);
                            correctionPossible=true;
                        }
                    }
                }
                if (triple.getObject().isURI()) {
                    if (!entitySuggestor.entityExists(triple.getObject(), query.getPrefixMapping().getNsPrefixMap(), endpoint)) {
                        suggestions.getMissingResources().add(triple.getSubject().toString());
                        if (useWikicat&&entitySuggestor.isWikicat(triple.getObject(), query.getPrefixMapping().getNsPrefixMap(), endpoint)) {
                            correctedResources.put(triple.getObject().toString(query.getPrefixMapping()), "http://dbpedia.org/class/yago/Wikicat" + triple.getObject().getLocalName());
                            correctionPossible = true;
                        } else return;
                    }
                    else if(!correctionPossible) {
                        String candidatesResource = entitySuggestor.generateCandidateEntitiesNew(triple.getObject(), query.getPrefixMapping().getNsPrefixMap(), endpoint, sameAsEdge);
                        if (candidatesResource != null) {
                            correctedResources.put(triple.getObject().toString(query.getPrefixMapping()), candidatesResource);
                            correctionPossible=true;
                        }
                    }
                }
                if(!correctionPossible)
                    return;
            }
        }
        if(correctedResources.size()>0||missingPredicates.size() > 0) {
            String correctedQueryString = generateQueryResources(query.toString(), correctedResources);
            if (missingPredicates.size() > 0)
                suggestions.setCorrectedQuery(generateQueryPredicates(correctedQueryString, missingPredicates, query.getPrefixMapping(), endpoint,result));
            else suggestions.setCorrectedQuery(correctedQueryString);
        }
    }

}
