package app.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.springframework.stereotype.Controller;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
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
public class DashboardController {
	//@Autowired
	  
	private static final String APPLICATION_JSON = "application/json";
	
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
			//check whether there is an unfinished curation process
			udcDao.checkUnfinishedCuration(user.getId());
			
			int qald1Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD1_Train_dbpedia", "QALD1_Test_dbpedia")).size();
			int qald2Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD2_Train_dbpedia", "QALD2_Test_dbpedia")).size();
			int qald3Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD3_Train_dbpedia", "QALD3_Test_dbpedia")).size();
			int qald4Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD4_Train_dbpedia", "QALD4_Test_dbpedia")).size();
			int qald5Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD5_Train_dbpedia", "QALD5_Test_dbpedia")).size();
			int qald6Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD6_Train_dbpedia", "QALD6_Test_dbpedia")).size();
			int qald7Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD7_Train_dbpedia", "QALD7_Test_dbpedia")).size();
			int qald8Correction = (udcDao.getAllDatasetsInParticularVersion(user.getId(), "QALD8_Train_dbpedia", "QALD8_Test_dbpedia")).size();
			
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
	  
	  //function to export master dataset
	  @SuppressWarnings("unchecked")
	@RequestMapping(value = "/download-master-dataset/{qald-test}/{qald-train}", method = RequestMethod.GET)
		public ModelAndView downloadMasterDataset(@PathVariable("qald-test") String qaldTest,@PathVariable("qald-train") String qaldTrain, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {
			Cookie[] cks = request.getCookies();
			CookieDAO cookieDao = new CookieDAO();
			if (!cookieDao.isValidate(cks)) {
				redirectAttributes.addFlashAttribute("message","Session Expired.");
				ModelAndView mav = new ModelAndView("redirect:/login");
				return mav;
			}
			JSONArray list = new JSONArray();			
	        BasicDBObject searchObj = new BasicDBObject();
			searchObj.put("id",1);	
			String[] qaldName = qaldTrain.split("_");		
				try {
					//call mongoDb
					DB db = MongoDBManager.getDB("QaldCuratorFiltered"); //Database Name
					DBCollection coll = db.getCollection(qaldTrain); //Collection
					DBCursor cursor = coll.find().sort(searchObj); //Find All
					while (cursor.hasNext()) {
						DBObject dbobj = cursor.next();
						Gson gson = new GsonBuilder().create();
						DatasetModel q = gson.fromJson(dbobj.toString(), DatasetModel.class);	
						String formatedSparqlQuery = q.getSparqlQuery().replaceAll("\r\n|\r|\n|\t", " ");
						q.setDatasetVersion(qaldTrain);
						q.setSparqlQuery(formatedSparqlQuery);
						list.add(q);
					}
					cursor.close();
					//get QALD Test dataset
					DBCollection coll1 = db.getCollection(qaldTest); //Collection
					DBCursor cursor1 = coll1.find().sort(searchObj); //Find All
					while (cursor1.hasNext()) {
						DBObject dbobj1 = cursor1.next();
						Gson gson1 = new GsonBuilder().create();
						DatasetModel q1 = gson1.fromJson(dbobj1.toString(), DatasetModel.class);	
						String formatedSparqlQuery = q1.getSparqlQuery().replaceAll("\r\n|\r|\n|\t", " ");
						q1.setDatasetVersion(qaldTest);
						q1.setSparqlQuery(formatedSparqlQuery);
						list.add(q1);
					}
					cursor1.close();
								
				} catch (Exception e) {}
				
				//contruct dataset information
				JSONObject newdbobj = new JSONObject();
				newdbobj.put("id", qaldName[0]+" dataset");
				
				JSONObject obj = new JSONObject();
				obj.put("dataset", newdbobj);
		        obj.put("questions", list);
		        JSONArray objFinal = new JSONArray();
		        objFinal.add(obj);
		    
		        /*//write dataset into a file in json format
		        ObjectMapper mapper = new ObjectMapper();
				ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
				writer.writeValue(new File("C:\\exportMasterDataset\\"+qaldName[0]+".json"), objFinal);*/		
		        
		        String path = request.getSession().getServletContext().getRealPath("/resources/reports/")+qaldName[0]+".json";
		        
		        //write dataset into a file in json format
		        ObjectMapper mapper = new ObjectMapper();
				ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
				// if this project will move to the production server, the path should change to src/main/webapp/resources/reports/
				writer.writeValue(new File(path), objFinal);
				
				File file = getFile(path);
			    InputStream in = new FileInputStream(file);
			    response.setContentType(APPLICATION_JSON);
			    response.setHeader("Content-Disposition", "attachment; filename=" + file.getName());
			    response.setHeader("Content-Length", String.valueOf(file.length()));
			    FileCopyUtils.copy(in, response.getOutputStream());
	        
			ModelAndView mav = new ModelAndView("redirect:/document-list/collections/{qald-test}/{qald-train}");
			return mav;
		}
	  //function to export curated dataset
	  @SuppressWarnings("unchecked")
	@RequestMapping(value = "/download-curated-dataset/{qald-test}/{qald-train}", method = RequestMethod.GET)
		public ModelAndView downloadCuratedDataset(@PathVariable("qald-test") String qaldTest,@PathVariable("qald-train") String qaldTrain, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {
			Cookie[] cks = request.getCookies();
			CookieDAO cookieDao = new CookieDAO();
			if (!cookieDao.isValidate(cks)) {
				redirectAttributes.addFlashAttribute("message","Session Expired.");
				ModelAndView mav = new ModelAndView("redirect:/login");
				return mav;
			}
			//get the user
			UserDAO userDao = new UserDAO();
			User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
			int userId = user.getId();
			
			//get curated questions
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			//Check whether there is an unfinishfed curation process			
			udcDao.checkUnfinishedCuration(userId);
			//get curated questions
			List<UserDatasetCorrection> listCuratedQuestions = udcDao.getAllDatasetsInParticularVersion(userId, qaldTrain, qaldTest);
			
			//write the curated questions into a json format file
			//contruct dataset information
			String[] qaldName = qaldTrain.split("_");
			String qaldNameNew = "curated_"+qaldName[0];
			JSONArray list = new JSONArray();
			for (UserDatasetCorrection data : listCuratedQuestions) {
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
			JSONObject newdbobj = new JSONObject();
			newdbobj.put("id", qaldName[0]+" dataset");
			
			JSONObject obj = new JSONObject();
			obj.put("dataset", newdbobj);
	        obj.put("questions", list);
	        JSONArray objFinal = new JSONArray();
	        objFinal.add(obj);
	    
	      //write dataset into a file in json format
	        ObjectMapper mapper = new ObjectMapper();
			ObjectWriter writer = mapper.writer(new DefaultPrettyPrinter());
			// if this project will move to the production server, the path should change to src/main/webapp/resources/reports/
			writer.writeValue(new File("C:\\Users\\riagu\\Documents\\new-repo\\QALDCurator\\src\\main\\webapp\\resources\\reports\\"+qaldNameNew+".json"), objFinal);		        
        
		ModelAndView mav = new ModelAndView("redirect:/document-list/curated-question/{qald-test}/{qald-train}");
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
	  private File getFile(String filePath) throws FileNotFoundException {
	        File file = new File(filePath);
	        if (!file.exists()){
	            throw new FileNotFoundException("file with path: " +filePath + " was not found.");
	        }
	        return file;
	    }
}
