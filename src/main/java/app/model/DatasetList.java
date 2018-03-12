package app.model;

public class DatasetList {
	public int id;
	public String name;
	
	public DatasetList() {
		
	}
	public DatasetList(int id, String name) {
		this.id = id;
		this.name = name;
	}
	public String getName() {
		return name;
	}
	public int getId() {
		return id;
	}
	
}
