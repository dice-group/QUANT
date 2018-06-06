package app.model;

import java.util.List;
import java.util.Map;

public class DocumentList {
	String id;
	String Question;
	Map<String, List<String>> Keywords;
	String datasetVersion;
	Boolean isCurate;
	Boolean isRemoved;
	
	public Boolean getIsRemoved() {
		return isRemoved;
	}
	public void setIsRemoved(Boolean isRemoved) {
		this.isRemoved = isRemoved;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getQuestion() {
		return Question;
	}
	public void setQuestion(String question) {
		Question = question;
	}
	
	public Map<String, List<String>> getKeywords() {
		return Keywords;
	}
	public void setKeywords(Map<String, List<String>> keywords) {
		Keywords = keywords;
	}
	public String getDatasetVersion() {
		return datasetVersion;
	}
	public void setDatasetVersion(String datasetVersion) {
		this.datasetVersion = datasetVersion;
	}
	public Boolean getIsCurate() {
		return isCurate;
	}
	public void setIsCurate(Boolean isCurate) {
		this.isCurate = isCurate;
	}
	
}
