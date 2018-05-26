package app.model;

public class UserQuestionDataset {
	String language;
	String string;
	String keywords;
	
	public UserQuestionDataset(){
		
	}
	public UserQuestionDataset(String language, String string, String keywords){
		this.language = language;
		this.string = string;
		this.keywords = keywords;
	}
	
	public String getLanguage(){
		return language;
	}
	
	public void setLanguage(String language){
		this.language = language;
	}
	
	public String getString(){
		return string;
	}
	
	public void setString(String string){
		this.string = string;
	}
	
	public String getKeywords(){
		return keywords;
	}
	
	public void setKeywords(String keywords){
		this.keywords = keywords;
	}
}
