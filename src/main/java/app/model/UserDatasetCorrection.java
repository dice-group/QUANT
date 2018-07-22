package app.model;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class UserDatasetCorrection {
	String transId;
	int userId;
	int revision;
	String startingTimeCuration;	
	String finishingTimeCuration;
	String id;
	String datasetVersion;	
	String answerType;
	String aggregation;
	String hybrid;
	String onlydbo;
	String sparqlQuery;
	String sparqlSuggestion;	
	String pseudoSparqlQuery;
	String outOfScope;
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeyword;
	Set<String> goldenAnswer;
	String removingTime;
	String noNeedChangesTime;
	String status;
	
	/*public void setUserId(Integer userId) {
		this.userId = userId;
	}
	public Integer getUserId() {
		return userId;
	}*/
	public String getSparqlSuggestion() {
		return sparqlSuggestion;
	}

	public void setSparqlSuggestion(String sparqlSuggestion) {
		this.sparqlSuggestion = sparqlSuggestion;
	}
	
	public String getNoNeedChangesTime() {
		return noNeedChangesTime;
	}

	public void setNoNeedChangesTime(String noNeedChangesTime) {
		this.noNeedChangesTime = noNeedChangesTime;
	}

	public String getRemovingTime() {
		return removingTime;
	}

	public void setRemovingTime(String removingTime) {
		this.removingTime = removingTime;
	}
	
	public UserDatasetCorrection() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getStartingTimeCuration() {
		return startingTimeCuration;
	}
	public void setStartingTimeCuration(String startingTimeCuration) {
		this.startingTimeCuration = startingTimeCuration;
	}
	public String getFinishingTimeCuration() {
		return finishingTimeCuration;
	}
	public void setFinishingTimeCuration(String finishingTimeCuration) {
		this.finishingTimeCuration = finishingTimeCuration;
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
	public int getRevision() {
		return revision;
	}
	public void setRevision(int revision) {
		this.revision = revision;
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
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
		
}
