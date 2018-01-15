package org.zkoss.mongodb.model;

import java.util.List;

public class HeadList {
	List vars;
	
	public HeadList(){
		
	}
	public HeadList(List vars){
		this.vars = vars;
	}
	
	public List getVars(){
		return vars;
	}
	
	public void setVars(List vars){
		this.vars = vars;
	}
}
