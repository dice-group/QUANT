package org.zkoss.mongodb.model;

import java.util.List;

public class Datasets {
	Oid _id;
	Dataset dataset;
	List<Questions> questions;
	
	public Datasets(){
		
	}
	
	public Datasets(Oid _id, Dataset dataset, List<Questions> questions){
		this._id = _id;
		this.dataset = dataset;
		this.questions = questions;
	}
	
	public Dataset getDataset(){
		return dataset;
	}
	
	public void setDataset(Dataset dataset){
		this.dataset = dataset;
	}
	
	public List<Questions> getQuestions(){
		return questions;
	}
	
	public void setQuestions(List<Questions> questions){
		this.questions = questions;
	}
}
