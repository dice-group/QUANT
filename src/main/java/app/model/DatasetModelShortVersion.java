package app.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class DatasetModelShortVersion {
	String id;
	String datasetVersion;	
	Map<String, String> languageToQuestion;
	Map<String, List<String>> languageToKeyword;
	public DatasetModelShortVersion() {
		
	}
	public DatasetModelShortVersion(
			String datasetVersion,
			String id,			
			Map<String, String> languageToQuestion,
			Map<String, List<String>> languageToKeyword,
			Set<String> goldenAnswer
			) {
		this.id = id;		
		this.languageToQuestion = languageToQuestion;
		this.languageToKeyword = languageToKeyword;	
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id=id;
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
