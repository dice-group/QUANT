package app.model;

public class TargetLanguages {
	String langId;
	String langName;
	
	public TargetLanguages(String langId, String langName) {
		this.langId = langId;
		this.langName = langName;
	}
	public String getLangId() {
		return langId;
	}
	public void setLangId(String langId) {
		this.langId = langId;
	}
	public String getLangName() {
		return langName;
	}
	public void setLangName(String langName) {
		this.langName = langName;
	}	
}
