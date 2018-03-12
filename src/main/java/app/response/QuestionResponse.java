package app.response;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class QuestionResponse {
	//private String datasetVersion;
	private String id;
	private String answerType;
	private Boolean aggregation;
	private Boolean onlydbo;
	private Boolean hybrid;
	private Boolean outOfScope;
	private String sparqlQuery;
	private String pseudoSparqlQuery;
	private Set<String> goldenAnswer;
	private Map<String, String> languageToQuestion;
	private Map<String, List<String>> languageToKeyword;
	
	/*public String getDatasetVersion() {
		return datasetVersion;
	}
	public void setDatasetVersion(String datasetVersion) {
		this.datasetVersion = datasetVersion;
	}*/
	public Map<String, String> getLanguageToQuestion() {
		return languageToQuestion;
	}
	public void setLanguageToQuestion(Map<String, String> languageToQuestion) {
		this.languageToQuestion = languageToQuestion;
	}
	public Map<String, List<String>> getLanguageToKeyword() {
		return languageToKeyword;
	}
	public void setLanguageToKeyword(Map<String, List<String>> languageToKeyword) {
		this.languageToKeyword = languageToKeyword;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType=answerType;
	}
	public Boolean getAggregation() {
		return aggregation;
	}
	public void setAggregation(Boolean aggregation) {
		this.aggregation = aggregation;
	}
	public Boolean getOnlydbo() {
		return onlydbo;
	}
	public void setOnlydbo(Boolean onlydbo) {
		this.onlydbo = onlydbo;
	}
	public Boolean getHybrid() {
		return hybrid;
	}
	public void setHybrid(Boolean hybrid) {
		this.hybrid=hybrid;
	}
	public Boolean getOutIfScope() {
		return outOfScope;
	}
	public void setOutOfScope(Boolean outOfScope) {
		this.outOfScope=outOfScope;
	}
	public String getSparqlQuery() {
		return sparqlQuery;
	}
	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery=sparqlQuery;
	}
	public String getPseudoSparqlQuery() {
		return pseudoSparqlQuery;
	}
	public void setPseudoSparqlQuery(String pseudoSparqlQuery) {
		this.pseudoSparqlQuery=pseudoSparqlQuery;
	}
	public Set<String> getGoldenAnswer(){
		return goldenAnswer;
	}
	public void setGoldenAnswer(Set<String> goldenAnswer) {
		this.goldenAnswer = goldenAnswer;
	}
}
