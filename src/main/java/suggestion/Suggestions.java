package suggestion;

import org.apache.jena.query.*;
import org.apache.jena.sparql.core.Var;
import org.apache.jena.sparql.engine.binding.Binding;
import suggestion.query.QuerySuggestions;
import suggestion.query.QuerySuggestor;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class Suggestions {
    private Map<String,String> defaultPrefixes=new HashMap<>();
    public Suggestions(){
        Properties prop = new Properties();
        try {
            prop.load(new FileInputStream("src/main/resources/prefix.properties"));
            for(String prefix:prop.stringPropertyNames())
                defaultPrefixes.put(prefix,prop.getProperty(prefix));
        }
        catch (IOException ex) {
            System.out.println("Properties file not found no Prefixes loaded");
        }
    }
    private boolean needsCorrections (QuerySuggestions suggestions, String queryString, String endpoint){
        boolean correctionFound=false;
        do {
            try {
                Query query = QueryFactory.create(queryString);
                QueryExecution qe = QueryExecutionFactory.sparqlService(endpoint, query);
                if(query.isAskType()) {
                    suggestions.setBooleanAnswer(qe.execAsk());
                    return false;
                }
                else{
                    ResultSet rs = qe.execSelect();
                    if(rs.hasNext()){
                        suggestions.setAnswers(rs);
                        return false;
                    }
                    return true;
                }
            }catch(QueryParseException e){
                if (e.getMessage().contains("Unresolved prefixed name:")) {
                    int startIndex = e.getMessage().indexOf("Unresolved prefixed name:") + "Unresolved prefixed name:".length();
                    String missingPrefix = e.getMessage().substring(startIndex, e.getMessage().lastIndexOf(":")).trim();
                    if (defaultPrefixes.containsKey(missingPrefix)) {
                        suggestions.addPrefixSuggestions(defaultPrefixes.get(missingPrefix));
                        queryString = defaultPrefixes.get(missingPrefix) + "\n" + queryString;
                        correctionFound=true;
                    } else {
                        suggestions.setError("Missing prefix: " + missingPrefix);
                    }
                }
                else suggestions.setError(e.getMessage());
            }
        }while (correctionFound);
        return false;
    }
    public QuerySuggestions gernerateQuerySuggestions(String queryString, String endpoint, List<Var>vars, List<Binding>bindings) {
        QuerySuggestions suggestions = new QuerySuggestions();
        if(needsCorrections(suggestions,queryString,endpoint)) {
            QuerySuggestor querySuggestor = new QuerySuggestor();
            suggestions.setIs_correct(false);
            querySuggestor.correct(suggestions,queryString,endpoint,vars,bindings);
            if(suggestions.getCorrectedQuery()!=null)
                needsCorrections(suggestions,suggestions.getCorrectedQuery(),endpoint);
        }

        return suggestions;

    }

}
