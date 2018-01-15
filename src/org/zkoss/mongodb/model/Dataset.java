package org.zkoss.mongodb.model;

public class Dataset {
	String id;
	
	public Dataset(){
		
	}
	
	public Dataset(String id){
		this.id = id;
	}
	
	public String getId(){
		return id;
	}
	
	public void setId(String id){
		this.id = id;
	}
}
