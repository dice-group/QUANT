package app.controller;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.AggregationOptions;
import com.mongodb.BasicDBObject;
import com.mongodb.Cursor;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.dao.CookieDAO;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.model.DatasetModel;
import app.model.User;
import app.model.UserDatasetCorrection;

@Controller
public class UserController {
	//@Autowired	
	
	@RequestMapping (value = "/user-list", method = RequestMethod.GET)
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
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		ModelAndView mav = new ModelAndView("document-curate-list");
		mav.addObject("datasets", udcDao.getAllDatasets(user.getId()));
		// if this project will move to the production server, the path should be changed to src/main/webapp/resources/reports/
		File f = new File("C:\\Users\\riagu\\Documents\\new-repo\\QALDCurator\\src\\main\\webapp\\resources\\reports\\"+user.getName()+".json");
		//File f = new File("src/main/webapp/resources/reports/"+user.getName()+".json");
		Boolean fExist = false;
		if(f.exists() && !f.isDirectory()) { 
		    fExist = true;
		}
		mav.addObject("fExists", fExist);
		mav.addObject("userName", user.getName());
		mav.addObject("status", "allCurated");
	    return mav;  
	}
	
	@RequestMapping(value = "/download-dataset-correction", method = RequestMethod.GET)
	public ModelAndView showDownloadDatasetCorrection(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws JsonGenerationException, JsonMappingException, IOException {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		JSONArray list = new JSONArray();
		//check data correction first
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		BasicDBObject searchObj1 = new BasicDBObject();
		BasicDBObject matchObj = new BasicDBObject();
		//matchObj.put("userId", 2);
		
		BasicDBObject searchObj2 = new BasicDBObject();
		BasicDBObject searchObj3 = new BasicDBObject();
		BasicDBObject dataObj = new BasicDBObject();
		dataObj.put("id", "$id");
		dataObj.put("datasetVersion", "$datasetVersion");
		//dataObj.put("languageToQuestion", "$languageToQuestion");
		dataObj.put("userId", "$userId");
		//dataObj.put("revision", "$revision");
		searchObj2.put("_id", dataObj);
		searchObj3.put("$max", "$revision");
		searchObj2.put("latestData", searchObj3);
		//searchObj1.put("$match", matchObj);
		searchObj1.put("$group", searchObj2);
			try {
				DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
				DBCollection coll = db.getCollection("UserDatasetCorrection"); //Collection
				List<DBObject> flatPipeline = Arrays.asList(searchObj1);
				AggregationOptions aggregationOptions = AggregationOptions.builder()
						                                    .batchSize(100)
						                                    .allowDiskUse(true)
						                                    .build();
				Cursor cursor = coll.aggregate(flatPipeline,aggregationOptions);
				UserDatasetCorrectionDAO userDatasetCorrectionDao = new UserDatasetCorrectionDAO();
				while (cursor.hasNext()) {
					DBObject dbobj = cursor.next();
					Gson gson = new GsonBuilder().create();
					UserDatasetCorrection q = gson.fromJson(dbobj.get("_id").toString(), UserDatasetCorrection.class);
					UserDatasetCorrection data = userDatasetCorrectionDao.getDocumentFromAnyStatus(user.getId(), q.getId(), q.getDatasetVersion());
					if (data.getId()!= null) {				
						UserDatasetCorrection item = new UserDatasetCorrection();
						
						item.setDatasetVersion(data.getDatasetVersion());;
						item.setId(data.getId());
						item.setAnswerType(data.getAnswerType());
						item.setAggregation(data.getAggregation());
						item.setOnlydbo(data.getOnlydbo());
						item.setHybrid(data.getHybrid());
						item.setLanguageToQuestion(data.getLanguageToQuestion());
						item.setLanguageToKeyword(data.getLanguageToKeyword());
						item.setSparqlQuery(data.getSparqlQuery());
						item.setPseudoSparqlQuery(data.getPseudoSparqlQuery());
						item.setGoldenAnswer(data.getGoldenAnswer());
						BasicDBObject newDbObj = toBasicDBObject(item);
						list.add(newDbObj);
					}
				}							
			} catch (Exception e) {}
			
			//contruct dataset information
			JSONObject newdbobj = new JSONObject();
			newdbobj.put("id", user.getName()+" dataset");
			
			JSONObject obj = new JSONObject();
			obj.put("dataset", newdbobj);
	        obj.put("questions", list);
	        JSONArray objFinal = new JSONArray();
	        objFinal.add(obj);
	    
	        //write dataset into a file in json format
	        ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			// if this project will move to the production server, the path should change to src/main/webapp/resources/reports/
			writer.writeValue(new File("C:\\Users\\riagu\\Documents\\new-repo\\QALDCurator\\src\\main\\webapp\\resources\\reports\\"+user.getName()+".json"), objFinal);
			//writer.writeValue(new File("src/main/webapp/resources/reports/"+user.getName()+".json"), objFinal);	
        
		ModelAndView mav = new ModelAndView("redirect:/user-dataset-correction");
		return mav;
	}
	private BasicDBObject toBasicDBObject(UserDatasetCorrection document) {
		BasicDBObject newdbobj = new BasicDBObject();
		newdbobj.put("id", document.getId());
		newdbobj.put("datasetVersion", document.getDatasetVersion());
		newdbobj.put("answerType", document.getAnswerType());
		newdbobj.put("aggregation", document.getAggregation());
		newdbobj.put("onlydbo", document.getOnlydbo());
		newdbobj.put("hybrid", document.getHybrid());
		newdbobj.put("sparqlQuery", document.getSparqlQuery());
		newdbobj.put("pseudoSparqlQuery", document.getPseudoSparqlQuery());
		newdbobj.put("goldenAnswer", document.getGoldenAnswer());
		newdbobj.put("languageToQuestion", document.getLanguageToQuestion());
		newdbobj.put("languageToKeyword", document.getLanguageToKeyword());
		newdbobj.put("outOfScope", document.getOutOfScope());
		
		return newdbobj;
	}
}
