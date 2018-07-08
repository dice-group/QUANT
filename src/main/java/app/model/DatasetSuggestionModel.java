package app.model;

import java.util.List;
import java.util.Map;

public class DatasetSuggestionModel {
	String answerTypeSugg;
	String aggregationSugg;
	String onlyDboSugg;
	String hybridSugg;
	String outOfScopeSugg;
	String resultStatus;	
	Map<String, List<String>> sparqlAndCaseList;
	List<String> answerFromVirtuosoList;	
	public List<String> getAnswerFromVirtuosoList() {
		return answerFromVirtuosoList;
	}
	public void setAnswerFromVirtuosoList(List<String> answerFromVirtuosoList) {
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
