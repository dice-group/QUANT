package suggestion;

import org.apache.jena.query.*;
import suggestion.query.QuerySuggestions;
import suggestion.query.QuerySuggestor;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.SocketTimeoutException;
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
                qe.setTimeout(5000);
                if(query.isAskType()) {
                    suggestions.setBooleanAnswer(qe.execAsk());
                    qe.close();
                    return false;
                }
                else{
                    ResultSet rs = ResultSetFactory.copyResults(qe.execSelect());
                    qe.close();
                    if(rs.hasNext()){
                        suggestions.setAnswers(rs);
                        return false;
                    }
                    qe.close();
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
            }catch (Exception e){
                e.printStackTrace();
                suggestions.setEndpointReachable(false);
                return false;
            }
        }while (correctionFound);
        return false;
    }
    //public QuerySuggestions generateQuerySuggestions(String queryString, String endpoint, String result)
    public QuerySuggestions generateQuerySuggestions(String queryString, String endpoint, String result) {
        QuerySuggestions suggestions = new QuerySuggestions();
        if(needsCorrections(suggestions,queryString,endpoint)) {
            QuerySuggestor querySuggestor = new QuerySuggestor();
            suggestions.setIs_correct(false);
            querySuggestor.correct(suggestions,queryString,endpoint,result);
            if(suggestions.getCorrectedQuery()!=null)
                needsCorrections(suggestions,suggestions.getCorrectedQuery(),endpoint);
        }

        return suggestions;

    }

}
