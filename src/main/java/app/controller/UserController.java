package app.controller;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import app.dao.CookieDAO;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.model.DatasetModel;
import app.model.User;

@Controller
public class UserController {
	//@Autowired
	/**
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	@RequestMapping(value = "/user-list", method = RequestMethod.GET)
	public ModelAndView showUserList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		UserDAO userDao = new UserDAO();
		
		ModelAndView mav = new ModelAndView("user-list");
		mav.addObject("users", userDao.getAll());
	    return mav;  
	}
	/**
	 * Method used  to add User
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user-list/user/insert-user", method = RequestMethod.POST)
	public String add(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) throws Exception {
		UserDAO userDao = new UserDAO();
		String id = request.getParameter("id-input");
		String name = request.getParameter("name-input");
		String username = request.getParameter("username-input");
		String password = request.getParameter("password-user-input");
		String email = request.getParameter("email-input");
		String role = request.getParameter("role-user-input");
		
		User user = new User();
		
		user.setName(name);
		user.setUsername(username);
		user.setPassword(password);
		user.setEmail(email);
		user.setRole(role);
		if (id.isEmpty()) {
			user.setId(userDao.getUserId());
			userDao.addUser(user);
		}else {
			user.setId(Integer.parseInt(id));
			userDao.updateUser(user);
		}
		
		return "redirect:/user-list";
		
	}
	/**
	 * Method used  to delete User
	 * @param request
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value = "/user-list/user/delete-user", method = RequestMethod.POST)
	public String remove(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttrs) throws Exception {
		UserDAO userDao = new UserDAO();
		String id = request.getParameter("id-input-delete");
		
		if (id.isEmpty()) {
			
		}else {
			userDao.deleteUser(Integer.parseInt(id));
		}
		
		return "redirect:/user-list";
		
	}
	@RequestMapping(value = "/user-dataset-correction", method = RequestMethod.GET)
	public ModelAndView showUserCorrection(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {	
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		ModelAndView mav = new ModelAndView("document-curate-list");
		mav.addObject("datasets", udcDao.getAllDatasets());
	    return mav;  
	}
}
