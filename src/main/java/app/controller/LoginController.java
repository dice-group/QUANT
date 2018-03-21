package app.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dao.CookieDAO;
import app.dao.UserDAO;
import app.model.Login;
import app.model.User;


@Controller
public class LoginController {
  //@Autowired
  
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public ModelAndView showLogin(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
	  Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/dashboard");
			return mav;
		}
    ModelAndView mav = new ModelAndView("login");
    mav.addObject("login", new Login());
    return mav;
  }

  @RequestMapping(value = "/loginProcess", method = RequestMethod.POST)
  public String loginProcess(HttpServletRequest request, HttpServletResponse response,
  @ModelAttribute("login") Login login, RedirectAttributes redirectAttributes) {
	UserDAO  userDao = new UserDAO();
	Boolean isUser = userDao.validateUser(login);
	    if (isUser) {
	    	//Cookie username
	    	Cookie ck = new Cookie("auth", login.getUsername());
			ck.setMaxAge(36000);
			response.addCookie(ck);
			return "redirect:/dashboard";
	    } else {
	    	redirectAttributes.addFlashAttribute("message","Incorrect username or password.");
	    	return "redirect:/login";
	    }
    
  }
  @RequestMapping(value = "/logout", method = RequestMethod.GET)
  public ModelAndView showLogout(HttpServletRequest request, HttpServletResponse response) {
	  Cookie ck=new Cookie("auth","");  
      ck.setMaxAge(0);
      response.addCookie(ck);
    ModelAndView mav = new ModelAndView("redirect:/login");
    return mav;
  }
  
}
