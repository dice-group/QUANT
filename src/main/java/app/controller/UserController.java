package app.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

import app.dao.UserDAO;

@Controller
public class UserController {
	@Autowired
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/user-list", method = RequestMethod.GET)
	public ModelAndView showUserList(HttpServletRequest request, HttpServletResponse response) {
		UserDAO userDao = new UserDAO();
		
		ModelAndView mav = new ModelAndView("user-list");
		mav.addObject("users", userDao.getAll());
	    return mav;  
	}
}
