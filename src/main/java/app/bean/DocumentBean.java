package app.bean;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class DocumentBean {
String id;
	
	String answerType;
	Boolean aggregation;
	Boolean hybrid;
	Boolean onlydbo;
	String sparqlQuery;
	Boolean outOfScope;
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeywords;
	Set<String> goldenAnswer;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id=id;
	}
	public String getAnswerType() {
		return answerType;
	}
	public void setAnswerType(String answerType) {
		this.answerType = answerType;
	}
	public Boolean getAggrgation() {
		return aggregation;
	}
	public void setAggregation(Boolean aggregation) {
		this.aggregation=aggregation;
	}
	public Boolean getHybrid() {
		return hybrid;
	}
	public void setHybdrid(Boolean hybrid) {
		this.hybrid=hybrid;
	}
	public Boolean getOnlydbo() {
		return onlydbo;
	}
	public void setOnlydbo(Boolean onlydbo) {
		this.onlydbo=onlydbo;
	}
	public String getSparqlQuery() {
		return sparqlQuery;
	}
	public void setSparqlQuery(String sparqlQuery) {
		this.sparqlQuery=sparqlQuery;
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
	public Map<String, List<String>> getLanguageToKeywords() {
		return languageToKeywords;
	}
	public void setLanguageToKeywords(Map<String, List<String>> languageToKeywords) {
		this.languageToKeywords = languageToKeywords;
	}
	public Set<String> getGolderAnswer() {
		return goldenAnswer;
	}
	public void setGoldenAnswer(Set<String> goldenAnswer) {
		this.goldenAnswer = goldenAnswer;
	}
}
