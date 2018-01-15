package org.zkoss.mongodb.model;

public class Oid {
	String $oid;
	
	public Oid(){
		
	}
	
	public Oid(String $oid){
		this.$oid = $oid;
	}
	
	public String get$oid(){
		return $oid;
	}
	
	public void set$od(String $oid){
		this.$oid = $oid;
	}
}
