package app.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class FailedQuestionModel {
	String questionId;
	String databaseVersion;
	String question;
	String virtusoAnswer;
	
	
	public FailedQuestionModel(String questionId, String databaseVersion, String question, String virtusoAnswer) {
		super();
		this.questionId = questionId;
		this.databaseVersion = databaseVersion;
		this.question = question;
		this.virtusoAnswer = virtusoAnswer;
	}
	
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getDatabaseVersion() {
		return databaseVersion;
	}
	public void setDatabaseVersion(String databaseVersion) {
		this.databaseVersion = databaseVersion;
	}
	public String getQuestion() {
		return question;
	}
	public void setQuestion(String question) {
		this.question = question;
	}
	public String getVirtusoAnswer() {
		return virtusoAnswer;
	}
	public void setVirtusoAnswer(String virtusoAnswer) {
		this.virtusoAnswer = virtusoAnswer;
	}
	
	
}
