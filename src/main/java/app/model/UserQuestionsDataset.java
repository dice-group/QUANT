package app.model;

import java.util.List;
import java.util.Arrays;

import com.google.gson.annotations.SerializedName;

public class UserQuestionsDataset {
	String id;
	String answertype;
	String aggregation;
	String onlydbo;
	String hybrid;
	//@SerializedName("Question")
	List<UserQuestionDataset> question;
	QueryList query;
	List<AnswersList> answers;
	
	public UserQuestionsDataset(){}
	
	public UserQuestionsDataset(
			String id, 
			String answertype, 
			String aggregation, 
			String onlydbo,
			String hybrid,
			List<UserQuestionDataset> question,
			QueryList query,
			List<AnswersList> answers
			){
		this.id = id;
		this.answertype = answertype;
		this.aggregation = aggregation;
		this.onlydbo = onlydbo;
		this.hybrid = hybrid;
		this.question = question;
		this.query = query;
		this.answers = answers;
		
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
	
	public String getAnswertype(){
		return answertype;
	}
	
	public void setAnswertype (String answertype){
		this.answertype = answertype;
	}
	
	public String getAggregation(){
		return aggregation;
	}
	
	public void setAggregation(String aggregation){
		this.aggregation = aggregation;
	}
	
	public String getOnlydbo(){
		return onlydbo;
	}
	
	public void setOnlydbo(String onlydbo){
		this.onlydbo = onlydbo;
	}
	
	public String getHybrid(){
		return hybrid;
	}
	
	public void setHybrid(String hybrid){
		this.hybrid = hybrid;
	}
	
	public List<UserQuestionDataset> getQuestion(){
		return question;
	}
	
	public void setQuestion(List<UserQuestionDataset> question){
		this.question = question;
	}
	
	public QueryList getQuery(){
		return query;
	}
	
	public void setQuery(QueryList query){
		this.query = query;
	}
	
	public List<AnswersList> getAnswers(){
		return answers;
	}
	
	public void setAnswers(List<AnswersList> answers){
		this.answers = answers;
	}
}
