package app.model;

import java.util.List;
import java.util.Set;

public class NormalizedAggregation {
	String id;
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
	public boolean isHybrid() {
		return hybrid;
	}
	public void setHybrid(boolean hybrid) {
		this.hybrid = hybrid;
	}
	public boolean isOnlydbo() {
		return onlydbo;
	}
	public void setOnlydbo(boolean onlydbo) {
		this.onlydbo = onlydbo;
	}
	public String getSparqlQuery() {
		return sparqlQuery;
	}
	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery = sparqlQuery;
	}
	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	public boolean isAggregation() {
		return aggregation;
	}
	public void setAggregation(boolean aggregation) {
		this.aggregation = aggregation;
	}
	public Set<String> getGoldenAnswer() {
		return goldenAnswer;
	}
	public void setGoldenAnswer(Set<String> goldenAnswer) {
		this.goldenAnswer = goldenAnswer;
	}
	public boolean isOutOfScope() {
		return outOfScope;
	}
	public void setOutOfScope(boolean outOfScope) {
		this.outOfScope = outOfScope;
	}
	String datasetVersion;
	boolean hybrid;
	boolean onlydbo;
	String sparqlQuery;
	String answerType;
	boolean aggregation;
	Set<String> goldenAnswer;
	boolean outOfScope;
	
}
