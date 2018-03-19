package app.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

public class DatasetSuggestionModel {
	String answerTypeSugg;
	String aggregationSugg;
	String onlyDboSugg;
	String hybridSugg;
	String outOfScopeSugg;
	
	
	public DatasetSuggestionModel() {
		super();
		// TODO Auto-generated constructor stub
	}
	
	public String getAnswerTypeSugg() {
		return answerTypeSugg;
	}
	public void setAnswerTypeSugg(String answerTypeSugg) {
		this.answerTypeSugg = answerTypeSugg;
	}
	public String getAggregationSugg() {
		return aggregationSugg;
	}
	public void setAggregationSugg(String aggregationSugg) {
		this.aggregationSugg = aggregationSugg;
	}
	public String getOnlyDboSugg() {
		return onlyDboSugg;
	}
	public void setOnlyDboSugg(String onlyDboSugg) {
		this.onlyDboSugg = onlyDboSugg;
	}
	public String getHybridSugg() {
		return hybridSugg;
	}
	public void setHybridSugg(String hybridSugg) {
		this.hybridSugg = hybridSugg;
	}
	public String getOutOfScopeSugg() {
		return outOfScopeSugg;
	}
	public void setOutOfScopeSugg(String outOfScopeSugg) {
		this.outOfScopeSugg = outOfScopeSugg;
	}

	
}
