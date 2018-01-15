package org.zkoss.mongodb.model;

public class Question {
	String language;
	String string;
	String keywords;
	
	public Question(){
		
	}
	public Question(String language, String string, String keywords){
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
