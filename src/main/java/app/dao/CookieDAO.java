package app.dao;

import javax.servlet.http.Cookie;

public class CookieDAO {
	public Boolean isValidate(Cookie[] cks) {
		Boolean status = null;
		if (cks.length > 0) {
			
			for (int i = 0; i < cks.length; i++) {
				String name = cks[i].getName();
				String value = cks[i].getValue();
				if (name.equals("auth")) {
					status=true;
					break; // exit the loop and continue the page
				}
				if (i == (cks.length - 1)) // if all cookie are not valid redirect to error page
				{
					status=false;
					return status;
				}
				
			}
		} else {
			status=false;
		}
		return status;
		
	}
	public String getAuth(Cookie[] cks) {
		String username = null;
		if (cks.length > 0) {
			for (int i = 0; i < cks.length; i++) {
				String name = cks[i].getName();
				String value = cks[i].getValue();
				
				if (name.equals("auth")) {
					username = value;
					break; // exit the loop and continue the page
				}
				if (i == (cks.length - 1)) // if all cookie are not valid redirect to error page
				{
					return null;
				}
				
			}
		}
		return username;
	}
}
