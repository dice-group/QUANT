package app.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.qa.commons.datastructure.IQuestion;

public class QALD9TrainData implements IQuestion{
	String id;	
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
		return languageToKeyword;
	}
	public void setLanguageToKeywords(Map<String, List<String>> languageToKeyword) {
		this.languageToKeyword = languageToKeyword;
	}
	public Set<String> getGoldenAnswers() {
		return goldenAnswer;
	}
	public void setGoldenAnswers(Set<String> goldenAnswer) {
		this.goldenAnswer = goldenAnswer;
	}
	String answerType;
	Boolean aggregation;
	Boolean hybrid;
	Boolean onlydbo;
	String sparqlQuery;	
	Boolean outOfScope;	
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeyword;
	Set<String> goldenAnswer;
	@Override
	public void setValue(String valDescriptor, String val) {
		// TODO Auto-generated method stub
		
	}
	@Override
	public String getPseudoSparqlQuery() {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public void setPseudoSparqlQuery(String pseudoSparqlQuery) {
		// TODO Auto-generated method stub
		
	}
}
