package app.model;

import java.util.List;

public class ResultList {
	List<BindingsList> bindings;
	
	public ResultList(){
		
	}
	
	public ResultList(List<BindingsList> bindings){
		this.bindings = bindings;
	}
	
	public List<BindingsList> getBindings(){
		return bindings;
	}
	public void setBindings(List<BindingsList> bindings){
		this.bindings = bindings;
	}
}