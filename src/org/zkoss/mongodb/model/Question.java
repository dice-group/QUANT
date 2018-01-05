package org.zkoss.mongodb.model;

import java.util.Date;

import org.zkoss.json.JSONArray;
import org.zkoss.json.JSONObject;
import org.zkoss.json.parser.JSONParser;



public class Question {
	private String id;
	private String sparql;
	private String query;
	private String question;
	private String answertype;
	private String aggregation;
	private String onlydbo;
	private String hybrid;
	private String strquestion;
	private String keyword;
	private String lang;
	
	
	public Question(){ }
	
	public Question(String question, String sparql, String query, String answertype, String aggregation, 
			String onlydbo, String hybrid, String strquestion, String keyword, String lang, String id){
		this.sparql = sparql;
		this.query = query;
		this.question = question;
		this.answertype = answertype;
		this.aggregation = aggregation;
		this.onlydbo = onlydbo;
		this.hybrid = hybrid;
		this.strquestion = strquestion;
		this.keyword = keyword;
		this.lang = lang;
		this.id = id;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuery() {
		return query;
	}
	public void setQuery(String query) {
		this.query = query;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getSparql() {
		return sparql;
	}
	public void setSparql(String sparql) {
		this.sparql =sparql;
	}
	public String getAnswertype() {
		return answertype;
	}
	public void setAnswertype(String answertype) {
		this.answertype =answertype;
	}
	public String getAggregation() {
		return aggregation;
	}
	public void setAggregation(String aggregation) {
		this.aggregation =aggregation;
	}
	public String getOnlydbo() {
		return onlydbo;
	}
	public void setOnlydbo(String onlydbo) {
		this.onlydbo =onlydbo;
	}
	public String getHybrid() {
		return hybrid;
	}
	public void setHybrid(String hybrid) {
		this.hybrid =hybrid;
	}
	public String getStrquestion() {
		return strquestion;
	}
	public void setStrquestion(String strquestion) {
		this.strquestion =strquestion;
	}
	public String getKeyword() {
		return keyword;
	}
	public void setKeyword(String keyword) {
		this.keyword =keyword;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
}
