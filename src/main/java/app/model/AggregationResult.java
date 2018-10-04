package app.model;

import java.util.List;

public class AggregationResult {
	Object _id;
	public Object get_id() {
		return _id;
	}
	public void set_id(Object _id) {
		this._id = _id;
	}
	
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	List<String> uniqueIds;
	public List<String> getUniqueIds() {
		return uniqueIds;
	}
	public void setUniqueIds(List<String> uniqueIds) {
		this.uniqueIds = uniqueIds;
	}
	int count;
	
}
