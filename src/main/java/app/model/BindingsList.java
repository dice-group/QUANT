package app.model;

public class BindingsList {
	UriList uri;
	UriList c;
	UriList string;
	UriList date;
	
	public BindingsList(){
		
	}
	
	public BindingsList(UriList uri){
		this.uri = uri;
	}
	
	public UriList getUri(){
		return uri;
	}
	
	public void setUri(UriList uri){
		this.uri = uri;
	}
	public UriList getC(){
		return c;
	}
	public UriList getString(){
		return string;
	}

	public UriList getDate() {
		return date;
	}

	public void setDate(UriList date) {
		this.date = date;
	}

	public void setC(UriList c) {
		this.c = c;
	}

	public void setString(UriList string) {
		this.string = string;
	}
	
}
