package app.model;

public class QueryList {
	String sparql;
	
	public QueryList(){
		
	}
	
	public QueryList(String sparql){
		this.sparql = sparql;
	}
	
	public String getSparql(){
		return sparql;
	}
	
	public void setSparql(String sparql){
		this.sparql = sparql;
	}
}
