package app.controller;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
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
import org.springframework.web.bind.annotation.PathVariable;
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
import com.mongodb.BasicDBObject;

import app.dao.CookieDAO;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCollectionDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.dao.UserLogDAO;
import app.model.AnswersList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.QueryList;
import app.model.Question;
import app.model.User;
import app.model.UserDatasetCollection;
import app.model.UserDatasetCorrection;
import app.model.UserLog;
import app.model.UserQuestionDataset;
import app.model.UserQuestionsDataset;
import app.sparql.SparqlService;

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
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
				
		UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();
		List<UserDatasetCollection> documentList = userDatasetCollectionDao.getAllDatasets(user.getId());
		ModelAndView mav = new ModelAndView("curate-my-dataset");
		mav.addObject("datasets", documentList);
		mav.addObject("userId", user.getId());
		return mav;  
	} 
	/**
	 * Upload single file using Spring Controller
	 */
	@RequestMapping(value = "/uploadFile", method = RequestMethod.POST)
	public ModelAndView uploadFileHandler(
			HttpServletRequest request, 
			@RequestParam("file") MultipartFile file,
			HttpServletResponse response,
			RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		
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
					 UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();
					 userDatasetCollectionDao.removeAllDocument(user.getId());
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
							userDatasetCollection.setUserId(user.getId());
							userDatasetCollection.setDatasetVersion(databaseVersion);
							userDatasetCollection.setSparqlEndpoint(sparqlEndpoint);
							userDatasetCollection.setAggregation(task.getAggregation());
							userDatasetCollection.setAnswerType(task.getAnswertype());
							userDatasetCollection.setOnlydbo(task.getOnlydbo());
							userDatasetCollection.setHybrid(task.getHybrid());
							userDatasetCollection.setSparqlQuery(task.getQuery().getSparql());
							userDatasetCollection.setPseudoSparqlQuery(null);
							userDatasetCollection.setOutOfScope(null);
							
							Map<String,String> hmQuestion = new HashMap<String,String>(); //define Map<String, String> languageToQuestion 
							Map<String,List<String>> hmKeywords = new HashMap<String,List<String>>(); //define Map<String, List<String>> languageTOKeyword
							if (task.getQuestion().size()>0) {
								for(int y=0; y<task.getQuestion().size(); y++) {
								
									hmQuestion.put(task.getQuestion().get(y).getLanguage(), task.getQuestion().get(y).getString());
									
									String keyword = task.getQuestion().get(y).getKeywords();
									String[] stringKeyword = keyword.split(",");
									List<String> listKeywords = new ArrayList<String>();
									//if (stringKeyword.length > 0) {
									for (String a : stringKeyword)
										listKeywords.add(a);
									//}
									hmKeywords.put(task.getQuestion().get(y).getLanguage(), listKeywords);
								}
							}
							
							userDatasetCollection.setLanguageToQuestion(hmQuestion);
							userDatasetCollection.setLanguageToKeyword(hmKeywords);
							
							//define Set<String>goldenAnswer
							Set<String> setGoldenAnswers = new HashSet<String>();
							String vars = task.getAnswers().get(0).getHead().getVars().get(0).toString();
							if (task.getAnswers().get(0).getResults().getBindings().size()>0) {//element 0 only
								for(int y=0; y<task.getAnswers().get(0).getResults().getBindings().size(); y++) {
									if (vars.equals("uri"))
										setGoldenAnswers.add(task.getAnswers().get(0).getResults().getBindings().get(y).getUri().getValue());
									if (vars.equals("c"))
										setGoldenAnswers.add(task.getAnswers().get(0).getResults().getBindings().get(y).getC().getValue());
									if (vars.equals("string"))
										setGoldenAnswers.add(task.getAnswers().get(0).getResults().getBindings().get(y).getString().getValue());
									if (vars.equals("date"))
										setGoldenAnswers.add(task.getAnswers().get(0).getResults().getBindings().get(y).getDate().getValue());
									
								}
							}
							userDatasetCollection.setGoldenAnswer(setGoldenAnswers);
							userDatasetCollectionDao.addDocument(userDatasetCollection);
							
					 }
					 redirectAttributes.addFlashAttribute("message","You successfully uploaded file");
						ModelAndView mav = new ModelAndView("redirect:/curate-my-dataset");
						return mav;
					
				} catch (Exception e) {
					System.out.println(e.getMessage());
					//redirectAttributes.addFlashAttribute("message","You failed to upload " + databaseVersion + " => " + e.getMessage());
					//ModelAndView mav = new ModelAndView("redirect:/curate-my-dataset");
					//return mav;
					return null;
		        }
				
			} catch (Exception e) {
				//return "You failed to upload " + databaseVersion + " => " + e.getMessage();
				redirectAttributes.addFlashAttribute("message","You failed to upload " + databaseVersion + " => " + e.getMessage());
				ModelAndView mav = new ModelAndView("redirect:/curate-my-dataset");
				return mav;
			}
		} else {
			//return "You failed to upload " + databaseVersion + " because the file was empty.";
			redirectAttributes.addFlashAttribute("message","You failed to upload " + databaseVersion + " because the file was empty.");
			ModelAndView mav = new ModelAndView("redirect:/curate-my-dataset");
			return mav;
		}
	}
	@RequestMapping(value = "/curate-my-dataset/detail-collection/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentDetailCollection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();		
		ModelAndView mav = new ModelAndView("curate-my-dataset-detail");
		
		
		UserDatasetCollection documentItem = userDatasetCollectionDao.getDocument(user.getId(), id, datasetVersion); //get documents
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";
		String datasetVersionPrevious;
		String datasetVersionNext;
		String idPrevious;
		String idNext;
		UserDatasetCollection documentNext = userDatasetCollectionDao.getNextDocument(user.getId(), id, datasetVersion); 
		UserDatasetCollection documentPrevious = userDatasetCollectionDao.getPreviousDocument(user.getId(), id, datasetVersion);
		String pageName = "detail-collection";
		
		
		if (documentPrevious.equals(null)) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			datasetVersionPrevious = datasetVersion;
		}else {
			idPrevious = documentPrevious.getId();
			datasetVersionPrevious = documentPrevious.getDatasetVersion();
			if (idPrevious==null) {
				previousStatus = "disabled=\"disabled\"";
				idPrevious = id;
				datasetVersionPrevious = datasetVersion;
			}
		}
		
		if (documentNext.equals(null)) {
			nextStatus = "disabled=\"disabled\"";
			idNext=id;
			datasetVersionNext = datasetVersion;
		}else {
			idNext = documentNext.getId();
			datasetVersionNext = documentNext.getDatasetVersion();
			if (idNext==null) {
				nextStatus = "disabled=\"disabled\"";
				idNext=id;
				datasetVersionNext = datasetVersion;
			}
		}
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", pageName);
		mav.addObject("datasetVersionPrevious", datasetVersionPrevious);
		mav.addObject("datasetVersionNext", datasetVersionNext);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		/** end setting previous and next record **/
		
		// initial is curated = false
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;
		
		if (documentItem.getId()!=null) {
			String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItem.getSparqlQuery();
			String goldenAnswer = documentItem.getGoldenAnswer().toString();
			String aggregation = documentItem.getAggregation();
			String answerType = documentItem.getAnswerType();
			String onlydbo = documentItem.getOnlydbo();
			String hybrid = documentItem.getHybrid();
			String outOfScope = documentItem.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
			
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			Set<String> results = new HashSet();
			/** Retrieve answer from Virtuoso current endpoint **/
			if (ss.isASKQuery(languageToQuestionEn)) {
				String result = ss.getResultAskQuery(sprqlQuery);
				mav.addObject("onlineAnswer", result);
			}else {				
				results = ss.getQuery(sprqlQuery);
				mav.addObject("onlineAnswer", results);
			}				
			
			
			mav.addObject("languageToQuestionEn", languageToQuestionEn);
			mav.addObject("sparqlQuery", formatedSparqlQuery);
			mav.addObject("goldenAnswer", goldenAnswer);
			mav.addObject("aggregation", aggregation);
			mav.addObject("answerType", answerType);
			mav.addObject("onlydbo", onlydbo);
			mav.addObject("hybrid", hybrid);
			mav.addObject("languageToKeyword", languageToKeyword);
			mav.addObject("languageToQuestion", languageToQuestion);
			mav.addObject("outOfScope", outOfScope);
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);			
			
			
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			mav.addObject("SPARQL",sprqlQuery);
			mav.addObject("isExist", "yes");
			
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	@RequestMapping(value = "/curate-my-dataset/start-correction/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListStartCorrection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		
		UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();
		ModelAndView mav = new ModelAndView("curate-my-dataset-detail-curate");
		mav.addObject("disabledForm", "");
		mav.addObject("startButton", "On Process");
		mav.addObject("startButtonDisabled", "disabled");
		mav.addObject("displayStatus", "");
		UserDatasetCollection documentItem = userDatasetCollectionDao.getDocument(user.getId(), id, datasetVersion);
		/** Setting previous and next record **/
		int idCurrent = Integer.parseInt(id);
		String previousStatus = "";
		int idNext = idCurrent+1;
		int idPrevious;
		if (idCurrent==1) {
			previousStatus = "disabled";
			idPrevious = 1;
		}else {
			idPrevious = idCurrent - 1;
		}
		String statusNoChangeChk="";
		
		// initial is curated = false
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;
		
		if (documentItem.getId()!=null) {
			statusNoChangeChk = "style='display:none'";
			String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItem.getSparqlQuery();
			String goldenAnswer = documentItem.getGoldenAnswer().toString();
			String aggregation = documentItem.getAggregation();
			String answerType = documentItem.getAnswerType();
			String onlydbo = documentItem.getOnlydbo();
			String hybrid = documentItem.getHybrid();
			String outOfScope = documentItem.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
					
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			Set<String> results = new HashSet();
			/** Retrieve answer from Virtuoso current endpoint **/
			if (ss.isASKQuery(languageToQuestionEn)) {
				String result = ss.getResultAskQuery(sprqlQuery);
				mav.addObject("onlineAnswer", result);
			}else {				
				results = ss.getQuery(sprqlQuery);
				mav.addObject("onlineAnswer", results);
			}			
			mav.addObject("sparqlQuery", sprqlQuery);		
			mav.addObject("languageToQuestionEn", languageToQuestionEn);
			mav.addObject("sparqlQuery", formatedSparqlQuery);
			mav.addObject("goldenAnswer", goldenAnswer);
			mav.addObject("aggregation", aggregation);
			mav.addObject("answerType", answerType);
			mav.addObject("onlydbo", onlydbo);
			mav.addObject("hybrid", hybrid);
			mav.addObject("languageToKeyword", languageToKeyword);
			mav.addObject("languageToQuestion", languageToQuestion);
			mav.addObject("outOfScope", outOfScope);
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);			
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
		
			/** is Curated ? **/
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			mav.addObject("isExist", "yes");
			
		}else { //document has not been curated 
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
		}
		mav.addObject("statusNoChangeChk", statusNoChangeChk);
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);		
		return mav;
	}
	/*
	 * Autosave Document
	 */
	@RequestMapping(value = "/curate-my-dataset/document/save", method = RequestMethod.POST)
	public @ResponseBody UserDatasetCollection save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();		
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		
		UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();
		//get all data from the view part
		String datasetVersion = request.getParameter("datasetVersion");
		String id = request.getParameter("id");
		String answerType = request.getParameter("answerType");
		String aggregation = request.getParameter("aggregation");
		String onlydbo = request.getParameter("onlydbo");
		String hybrid = request.getParameter("hybrid");
		String sparqlQuery = request.getParameter("sparqlQuery");
		String pseudoSparqlQuery = request.getParameter("pseudoSparqlQuery");
		String outOfScope = request.getParameter("outOfScope");
		
		
		//find document
		UserDatasetCollection documentCorrection = userDatasetCollectionDao.getDocument(user.getId(), id, datasetVersion);
		
		if (documentCorrection.getId()!=null) { //Document exist in userdatasetcorrection or update
			documentCorrection.setAnswerType(answerType);
			documentCorrection.setAggregation(aggregation);
			documentCorrection.setOnlydbo(onlydbo);
			documentCorrection.setHybrid(hybrid);
			documentCorrection.setSparqlQuery(sparqlQuery);
			documentCorrection.setPseudoSparqlQuery(pseudoSparqlQuery);
			documentCorrection.setOutOfScope(outOfScope);
			userDatasetCollectionDao.updateDocument(documentCorrection); //update UserDatasetCorrection
		}
		
		return documentCorrection;
	}
	@RequestMapping(value = "/curate-my-dataset/document/edit-question/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String editQuestion(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		//Retrieve change value (if any)
		String id =request.getParameter("id"); 
		String value =request.getParameter("value");
		
		UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();
		UserDatasetCollection documentCorrection = userDatasetCollectionDao.getDocument(userId, id, datasetVersion);
		if (documentCorrection.getId()!=null) {
			// build languageToQuestion parameter
			Map<String, String> languageToQuestion = documentCorrection.getLanguageToQuestion();
			Map<String, String> mapLanguageToQuestion = new HashMap<String, String>();
			for (Map.Entry<String, String> mapEntry : languageToQuestion.entrySet()) {
				if (mapEntry.getKey().equals(id)) {
					mapLanguageToQuestion.put(mapEntry.getKey(), value);
				}else {
					mapLanguageToQuestion.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
			
			documentCorrection.setLanguageToQuestion(mapLanguageToQuestion);//change value of languageToQuestion
			userDatasetCollectionDao.updateDocument(documentCorrection);//update document
			
		}
		
		return "Dataset has been updated";
		
	}
	
	//Edit function of Question keyword 
	@RequestMapping(value = "/curate-my-dataset/document/edit-keyword/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String editKeyword(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		String id =request.getParameter("id");
		String value =request.getParameter("value");
		UserDatasetCollectionDAO userDatasetCollectionDao = new UserDatasetCollectionDAO();
		UserDatasetCollection documentCorrection = userDatasetCollectionDao.getDocument(user.getId(), datasetId, datasetVersion); //get document from dataset correction
		if (documentCorrection.getId()!=null) {
			//Build languageToKeyword Parameter
			Map<String, List<String>> languageToKeyword = documentCorrection.getLanguageToKeyword();
			Map<String, List<String>> mapLanguageToKeyword = new HashMap<String, List<String>>();
			for (Map.Entry<String, List<String>> mapEntry : languageToKeyword.entrySet()) {
				if (mapEntry.getKey().equals(id)) {
					List<String> listValue = new ArrayList<String>();
					org.json.JSONArray jsonArrayValue = new org.json.JSONArray(value);
					for (int x=0; x<jsonArrayValue.length();x++) {
						listValue.add(x, jsonArrayValue.getString(x));
					}
					mapLanguageToKeyword.put(mapEntry.getKey(), listValue);
				}else {
					mapLanguageToKeyword.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
			
			documentCorrection.setLanguageToKeyword(mapLanguageToKeyword);//change value of languageToKeyword
			userDatasetCollectionDao.updateDocument(documentCorrection); //update correction document
			
			
		}	
		return "Dataset has been updated";
	}
}
