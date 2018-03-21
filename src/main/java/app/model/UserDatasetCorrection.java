package app.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDatasetCorrection {
	String transId;
	int userId;
	String revision;
	String lastRevision;
	String id;
	String datasetVersion;	
	String answerType;
	Boolean aggregation;
	Boolean hybrid;
	Boolean onlydbo;
	String sparqlQuery;
	String pseudoSparqlQuery;
	Boolean outOfScope;
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeyword;
	Set<String> goldenAnswer;
	public UserDatasetCorrection() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getTransId() {
		return transId;
	}
	public void setTransId(String transId) {
		this.transId = transId;
	}
	public int getUserId() {
		return userId;
	}
	public void setUserId(int userId) {
		this.userId = userId;
	}
	public String getRevision() {
		return revision;
	}
	public void setRevision(String revision) {
		this.revision = revision;
	}
	public String getLastRevision() {
		return lastRevision;
	}
	public void setLastRevision(String lastRevision) {
		this.lastRevision = lastRevision;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDatasetVersion() {
		return datasetVersion;
	}
	public void setDatasetVersion(String datasetVersion) {
		this.datasetVersion = datasetVersion;
	}
	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	public Boolean getAggregation() {
		return aggregation;
	}
	public void setAggregation(Boolean aggregation) {
		this.aggregation = aggregation;
	}
	public Boolean getHybrid() {
		return hybrid;
	}
	public void setHybrid(Boolean hybrid) {
		this.hybrid = hybrid;
	}
	public Boolean getOnlydbo() {
		return onlydbo;
	}
	public void setOnlydbo(Boolean onlydbo) {
		this.onlydbo = onlydbo;
	}
	public String getSparqlQuery() {
		return sparqlQuery;
	}
	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}
	public String getPseudoSparqlQuery() {
		return pseudoSparqlQuery;
	}
	public void setPseudoSparqlQuery(String pseudoSparqlQuery) {
		this.pseudoSparqlQuery = pseudoSparqlQuery;
	}
	public Boolean getOutOfScope() {
		return outOfScope;
	}
	public void setOutOfScope(Boolean outOfScope) {
		this.outOfScope = outOfScope;
	}
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
	public Set<String> getGoldenAnswer() {
		return goldenAnswer;
	}
	public void setGoldenAnswer(Set<String> goldenAnswer) {
		this.goldenAnswer = goldenAnswer;
	}
		
}
