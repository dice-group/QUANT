package app.model;

import java.util.ArrayList;
import java.util.List;

public class TargetLanguagesList {
	List<TargetLanguages> theList = new ArrayList<TargetLanguages>();
	public List<TargetLanguages> getTheList() {
		return theList;
	}
	public void setTheList(List<TargetLanguages> theList) {
		this.theList = theList;
	}
	public TargetLanguagesList() {
        initData();
    }
	private void initData() {
		theList.add(new TargetLanguages("de","Deutsch"));
		theList.add(new TargetLanguages("es","Spanish"));
		theList.add(new TargetLanguages("it","Italian"));
		theList.add(new TargetLanguages("fr","French"));
		theList.add(new TargetLanguages("nl","Dutch"));
		theList.add(new TargetLanguages("ro","Romanian"));
		theList.add(new TargetLanguages("fa","Persian"));
		theList.add(new TargetLanguages("pt","Portuguese"));
		theList.add(new TargetLanguages("ru","Russian"));
		theList.add(new TargetLanguages("hi","Hindi"));	
		theList.add(new TargetLanguages("id","Bahasa Indonesia"));
	}
	
}
