package app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import app.dao.UserDAO;
import app.model.Login;
import app.model.User;


@Controller
public class LoginController {
  @Autowired
  
  @RequestMapping(value = "/login", method = RequestMethod.GET)
  public ModelAndView showLogin(HttpServletRequest request, HttpServletResponse response) {
    ModelAndView mav = new ModelAndView("login");
    mav.addObject("login", new Login());
    return mav;
  }

  @RequestMapping(value = "/loginProcess", method = RequestMethod.POST)
  public ModelAndView loginProcess(HttpServletRequest request, HttpServletResponse response,
  @ModelAttribute("login") Login login) {
	UserDAO  userDao = new UserDAO();
	Boolean isUser = userDao.validateUser(login);
	    if (isUser) {
		    ModelAndView mav = new ModelAndView("dashboard");
		    return mav;
	    } else {
	    	ModelAndView mav = new ModelAndView("login");
	    	mav.addObject("message", "Username or Password is wrong!!");
	    	return mav;
	    }
    
  }
  
}
