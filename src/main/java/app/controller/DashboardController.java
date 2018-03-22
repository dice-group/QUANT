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
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.model.User;


@Controller
public class DashboardController {
	//@Autowired
	  
	  @RequestMapping(value = "/dashboard", method = RequestMethod.GET)
	  public ModelAndView showLogin(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		ModelAndView mav = new ModelAndView("dashboard");
		//mav.addObject("isValidate", cookieDao.isValidate(cks));
		//Get name of the user
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		//String Name = user.getName();
		//mav.addObject("cokie", cookieDao.getAuth(cks));
		mav.addObject("name", user.getName());
		mav.addObject("role", user.getRole());
		if (cookieDao.isValidate(cks)) {
			DocumentDAO documentDao = new DocumentDAO();
			int qald1 = documentDao.countQaldDataset("QALD1_Test_dbpedia") + documentDao.countQaldDataset("QALD1_Train_dbpedia");
			int qald2 = documentDao.countQaldDataset("QALD2_Test_dbpedia") + documentDao.countQaldDataset("QALD2_Train_dbpedia");
			int qald3 = documentDao.countQaldDataset("QALD3_Test_dbpedia") + documentDao.countQaldDataset("QALD3_Train_dbpedia");
			int qald4 = documentDao.countQaldDataset("QALD4_Test_Multilingual") + documentDao.countQaldDataset("QALD4_Train_Multilingual");
			int qald5 = documentDao.countQaldDataset("QALD5_Test_Multilingual") + documentDao.countQaldDataset("QALD5_Train_Multilingual");
			int qald6 = documentDao.countQaldDataset("QALD6_Test_Multilingual") + documentDao.countQaldDataset("QALD6_Train_Multilingual");
			int qald7 = documentDao.countQaldDataset("QALD7_Test_Multilingual") + documentDao.countQaldDataset("QALD7_Train_Multilingual");
			int qald8 = documentDao.countQaldDataset("QALD8_Test_Multilingual") + documentDao.countQaldDataset("QALD8_Train_Multilingual");
			mav.addObject("qald1", qald1);
			mav.addObject("qald2", qald2);
			mav.addObject("qald3", qald3);
			mav.addObject("qald4", qald4);
			mav.addObject("qald5", qald5);
			mav.addObject("qald6", qald6);
			mav.addObject("qald7", qald7);
			mav.addObject("qald8", qald8);
		}else {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			mav = new ModelAndView("redirect:/login");
		}
	    return mav;
	  }
}
