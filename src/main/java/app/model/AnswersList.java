package app.model;

import java.util.List;

public class AnswersList {
	HeadList head;
	ResultList results;
	
	public AnswersList(){
		
	}
	
	public AnswersList(HeadList head, ResultList results){
		this.head = head;
	}
	
	public HeadList getHead(){
		return head;
	}
	
	public void setHead(HeadList head){
		this.head = head;
	}
	
	public ResultList getResults(){
		return results;
	}
	
	public void setResults(ResultList results){
		this.results = results;
	}
}
	
