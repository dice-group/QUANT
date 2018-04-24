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
import app.dao.UserDatasetCorrectionDAO;
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
		mav.addObject("role", user.getRole());//define role
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
			
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			int qald1Correction = udcDao.countQaldDataset(user.getId(), "QALD1_Test_dbpedia") +  udcDao.countQaldDataset(user.getId(), "QALD1_Train_dbpedia");
			int qald2Correction = udcDao.countQaldDataset(user.getId(), "QALD2_Test_dbpedia") +  udcDao.countQaldDataset(user.getId(), "QALD2_Train_dbpedia");
			int qald3Correction = udcDao.countQaldDataset(user.getId(), "QALD3_Test_dbpedia") +  udcDao.countQaldDataset(user.getId(), "QALD3_Train_dbpedia");
			int qald4Correction = udcDao.countQaldDataset(user.getId(), "QALD4_Test_Multilingual") +  udcDao.countQaldDataset(user.getId(), "QALD4_Train_Multilingual");
			int qald5Correction = udcDao.countQaldDataset(user.getId(), "QALD5_Test_Multilingual") +  udcDao.countQaldDataset(user.getId(), "QALD5_Train_Multilingual");
			int qald6Correction = udcDao.countQaldDataset(user.getId(), "QALD6_Test_Multilingual") +  udcDao.countQaldDataset(user.getId(), "QALD6_Train_Multilingual");
			int qald7Correction = udcDao.countQaldDataset(user.getId(), "QALD7_Test_Multilingual") +  udcDao.countQaldDataset(user.getId(), "QALD7_Train_Multilingual");
			int qald8Correction = udcDao.countQaldDataset(user.getId(), "QALD8_Test_Multilingual") +  udcDao.countQaldDataset(user.getId(), "QALD8_Train_Multilingual");
			
			mav.addObject("qald1Correction", qald1Correction);
			mav.addObject("qald2Correction", qald2Correction);
			mav.addObject("qald3Correction", qald3Correction);
			mav.addObject("qald4Correction", qald4Correction);
			mav.addObject("qald5Correction", qald5Correction);
			mav.addObject("qald6Correction", qald6Correction);
			mav.addObject("qald7Correction", qald7Correction);
			mav.addObject("qald8Correction", qald8Correction);
			
			mav.addObject("csList", udcDao.getCorrectionSummary());
			
		}else {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			mav = new ModelAndView("redirect:/login");
		}
	    return mav;
	  }
}
