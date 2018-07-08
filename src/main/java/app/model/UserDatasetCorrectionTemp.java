package app.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDatasetCorrectionTemp {
	String transId;
	int userId;	
	String id;
	String datasetVersion;	
	String answerType;
	String aggregation;
	String hybrid;
	String onlydbo;
	String sparqlQuery;
	String pseudoSparqlQuery;
	String outOfScope;
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeyword;
	Set<String> goldenAnswer;
	String lastRevision;
	
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
	public String getAggregation() {
		return aggregation;
	}
	public void setAggregation(String aggregation) {
		this.aggregation = aggregation;
	}
	public String getHybrid() {
		return hybrid;
	}
	public void setHybrid(String hybrid) {
		this.hybrid = hybrid;
	}
	public String getOnlydbo() {
		return onlydbo;
	}
	public void setOnlydbo(String onlydbo) {
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
	public String getOutOfScope() {
		return outOfScope;
	}
	public void setOutOfScope(String outOfScope) {
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
	public String getLastRevision() {
		return lastRevision;
	}
	public void setLastRevision(String lastRevision) {
		this.lastRevision = lastRevision;
	}
	
}
