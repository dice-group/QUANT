package suggestion.query;

import org.apache.jena.query.ResultSet;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;;

public class QuerySuggestions {
    private List<String> prefixSuggestions;
    private String error;
    private Boolean endpointReachable;
    private boolean is_correct = true;
    private List<String> missingResources = new ArrayList<String>();
    private List<String> missingPredicates = new ArrayList<String>();
    private String correctedQuery=null;
    private Optional<ResultSet>answers =Optional.empty();
    private Optional<Boolean> booleanAnswer =Optional.empty();
    public QuerySuggestions(){
        prefixSuggestions=new ArrayList<String>();
        endpointReachable=true;
    }
    public void addPrefixSuggestions(String prefix){
        prefixSuggestions.add(prefix);
    }


    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public List<String> getMissingResources() {
        return missingResources;
    }

    public List<String> getMissingPredicates() {
        return missingPredicates;
    }
    public String getCorrectedQuery() {
        return correctedQuery;
    }

    public void setCorrectedQuery(String query) {
        this.correctedQuery = query;
    }

    public Optional<ResultSet> getAnswers() {
        return answers;
    }

    public void setAnswers(ResultSet answers) {
        this.answers =Optional.of(answers);
    }

    public Optional<Boolean> getBooleanAnswer() {
        return booleanAnswer;
    }

    public void setBooleanAnswer(boolean booleanAnswer) {
        this.booleanAnswer = Optional.of(booleanAnswer);
    }


    public boolean is_correct() {
        return is_correct;
    }

    public void setIs_correct(boolean is_correct) {
        this.is_correct = is_correct;
    }

    public Boolean getEndpointReachable() {
        return endpointReachable;
    }

    public void setEndpointReachable(Boolean endpointReachable) {
        this.endpointReachable = endpointReachable;
    }
}
