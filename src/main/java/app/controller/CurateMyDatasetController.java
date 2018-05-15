package app.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonParser;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.dao.CookieDAO;
import app.model.AnswersList;
import app.model.QueryList;
import app.model.UserDatasetCollection;
import app.model.UserQuestionDataset;
import app.model.UserQuestionsDataset;

@Controller
public class CurateMyDatasetController {
	@Autowired
	
	
	private static final Logger logger = LoggerFactory
			.getLogger(CurateMyDatasetController.class);
	
	@RequestMapping(value = "/curate-my-dataset", method = RequestMethod.GET)
	public ModelAndView showCurateMyDataset(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		ModelAndView mav = new ModelAndView("curate-my-dataset");
		return mav;  
	} 
	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public @ResponseBody String uploadFileHandler(
			HttpServletRequest request, 
			@RequestParam("file") MultipartFile file,
			HttpServletResponse response) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		String databaseVersion = request.getParameter("databaseVersion");
		String sparqlEndpoint = request.getParameter("sparqlEndpoint");
		if (!file.isEmpty()) {
			try {
				byte[] bytes = file.getBytes();

				// Creating the directory to store file
				String rootPath = System.getProperty("catalina.home");
				File dir = new File(rootPath + File.separator + "tmpFiles");
				if (!dir.exists())
					dir.mkdirs();

				// Create the file on server
				File serverFile = new File(dir.getAbsolutePath()
						+ File.separator + databaseVersion);
				BufferedOutputStream stream = new BufferedOutputStream(
						new FileOutputStream(serverFile));
				stream.write(bytes);
				stream.close();

				logger.info("Server File Location="
						+ serverFile.getAbsolutePath());
				
				JSONParser parser = new JSONParser();
				try {
					 Object obj = parser.parse(new FileReader(serverFile.getAbsolutePath()));
					 JSONObject jsonObject = (JSONObject) obj;
					 JSONArray questions = (JSONArray) jsonObject.get("questions");
					 for (int x=0; x<questions.size(); x++){
							String objString =questions.get(x).toString();
							Gson gson = new GsonBuilder().create();
							UserQuestionsDataset datasetObj = gson.fromJson(objString, UserQuestionsDataset.class);
							UserQuestionsDataset task = new UserQuestionsDataset(
								(String) datasetObj.getId(),
								(String) datasetObj.getAnswertype(),
								(String) datasetObj.getAggregation(),
								(String) datasetObj.getOnlydbo(),
								(String) datasetObj.getHybrid(),
								(List<UserQuestionDataset>) datasetObj.getQuestion(),
								(QueryList) datasetObj.getQuery(),
								(List<AnswersList>) datasetObj.getAnswers()
								);
							System.out.println(task.getId());
							System.out.println(task.getQuery().getSparql());
							
							//saving process
							UserDatasetCollection userDatasetCollection = new UserDatasetCollection();
							userDatasetCollection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
							userDatasetCollection.setId(task.getId());
							userDatasetCollection.setDatasetVersion(null);
							userDatasetCollection.setAggregation(task.getAggregation());
							userDatasetCollection.setAnswerType(task.getAnswertype());
							userDatasetCollection.setOnlydbo(task.getOnlydbo());
							userDatasetCollection.setHybrid(task.getHybrid());
							userDatasetCollection.setSparqlQuery(task.getQuery().getSparql());
							HashMap<String,String> hmQuestion = new HashMap<String,String>(); //define Map<String, String> languageToQuestion 
							HashMap<String,List<String>> hmKeywords = new HashMap<String,List<String>>(); //define Map<String, List<String>> languageTOKeyword
							if (task.getQuestion().size()>0) {
								for(int y=0; y<=task.getQuestion().size(); y++) {
								
									hmQuestion.put(task.getQuestion().get(y).getLanguage(), task.getQuestion().get(y).getString());
									
									String keyword = task.getQuestion().get(y).getKeywords();
									String[] stringKeyword = keyword.split(",");
									List<String> listKeywords = new ArrayList<String>();
									for (String a : stringKeyword)
										listKeywords.add(a);
									
									hmKeywords.put(task.getQuestion().get(y).getLanguage(), listKeywords);
								}
							}
							userDatasetCollection.setLanguageToQuestion(hmQuestion);
							userDatasetCollection.setLanguageToKeyword(hmKeywords);
							//define Set<String>goldenAnswer
							Set<String> setGoldenAnswers = new HashSet<String>();
							String vars = task.getAnswers().get(0).getHead().getVars().get(0).toString();
							if (task.getAnswers().get(0).getResults().getBindings().size()>0) {//element 0 only
								for(int y=0; y<=task.getAnswers().get(0).getResults().getBindings().size(); y++) {
									if (vars.equals("uri"))
										setGoldenAnswers.add(task.getAnswers().get(0).getResults().getBindings().get(y).getUri().getValue());
									
									
								}
							}
							userDatasetCollection.setGoldenAnswer(setGoldenAnswers);
							
					 }
					 
					
				} catch (Exception e) {
		            e.printStackTrace();
		        }

				return "You successfully uploaded file=" + databaseVersion;
			} catch (Exception e) {
				return "You failed to upload " + databaseVersion + " => " + e.getMessage();
			}
		} else {
			return "You failed to upload " + databaseVersion
					+ " because the file was empty.";
		}
		
	}
}
