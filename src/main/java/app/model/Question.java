package app.model;

import java.util.List;
import java.util.Map;

public class Question {
	String id;
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeyword;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	
}
