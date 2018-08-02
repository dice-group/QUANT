package app.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dao.CookieDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.dao.UserLogDAO;
import app.model.User;

@Controller
public class UserLogController {
	
	//@Autowired
	
	@RequestMapping(value = "/user/user-log-list", method = RequestMethod.GET)
	public ModelAndView showUserLogList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId = user.getId();
		
		UserLogDAO userLogDao = new UserLogDAO();
		ModelAndView mav = new ModelAndView("user-log-list");
		
		//Check whether there is an unfinishfed curation process
		UserDatasetCorrectionDAO udcDao =  new UserDatasetCorrectionDAO();
		udcDao.checkUnfinishedCuration(userId);
		
		mav.addObject("userLogs",  userLogDao.getUserLogs(userId));
		return mav; 	
		}
}
