package app.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DatasetSuggestionModel {
	String answerTypeSugg;
	String aggregationSugg;
	String onlyDboSugg;
	String hybridSugg;
	String outOfScopeSugg;
	String resultStatus;
	boolean sparqlCorrectionStatus;
	String query;
	
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public Boolean getSparqlCorrectionStatus() {
		return sparqlCorrectionStatus;
	}
	public void setSparqlCorrectionStatus(Boolean sparqlCorrectionStatus) {
		this.sparqlCorrectionStatus = sparqlCorrectionStatus;
	}
	Map<String, List<String>> sparqlAndCaseList;
	Set<String> answerFromVirtuosoList;	
	
	public Set<String> getAnswerFromVirtuosoList() {
		return answerFromVirtuosoList;
	}
	public void setAnswerFromVirtuosoList(Set<String> answerFromVirtuosoList) {
		this.answerFromVirtuosoList = answerFromVirtuosoList;
	}
	public Map<String, List<String>> getSparqlAndCaseList() {
		return sparqlAndCaseList;
	}
	public void setSparqlAndCaseList(Map<String, List<String>> sparqlAndCaseList) {
		this.sparqlAndCaseList = sparqlAndCaseList;
	}		
	
	public String getResultStatus() {
		return resultStatus;
	}
	public void setResultStatus(String resultStatus) {
		this.resultStatus = resultStatus;
	}
	
		
	public String getAnswerTypeSugg() {
		return answerTypeSugg;
	}
	public void setAnswerTypeSugg(String answerTypeSugg) {
		this.answerTypeSugg = answerTypeSugg;
	}
	
	public String getAggregationSugg() {
		return aggregationSugg;
	}
	public void setAggregationSugg(String aggregationSugg) {
		this.aggregationSugg = aggregationSugg;
	}
	public String getOnlyDboSugg() {
		return onlyDboSugg;
	}
	public void setOnlyDboSugg(String onlyDboSugg) {
		this.onlyDboSugg = onlyDboSugg;
	}
	public String getHybridSugg() {
		return hybridSugg;
	}
	public void setHybridSugg(String hybridSugg) {
		this.hybridSugg = hybridSugg;
	}
	public String getOutOfScopeSugg() {
		return outOfScopeSugg;
	}
	public void setOutOfScopeSugg(String outOfScopeSugg) {
		this.outOfScopeSugg = outOfScopeSugg;
	}

	
}
