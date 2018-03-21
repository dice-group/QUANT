package app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.dao.CookieDAO;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.User;
import app.model.UserDatasetCorrection;
import app.response.QuestionResponse;
import app.sparql.SparqlService;

@Controller
public class DocumentController {
	
	//@Autowired
	
	/**
	 * 
	 * Display all filtered document
	 * 
	 * 
	 */
	@RequestMapping(value = "/document-list", method = RequestMethod.GET)
	public ModelAndView showDocumentList(HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		DocumentDAO documentDao = new DocumentDAO();
		
		ModelAndView mav = new ModelAndView("document-list");
		mav.addObject("datasets", documentDao.getAllDatasets());
		mav.addObject("userId", userId);
	    return mav;  
	} 
	@RequestMapping(value = "/document-list/collections/{qald-test}/{qald-train}", method = RequestMethod.GET)
	public ModelAndView showCollectionList(@PathVariable("qald-test") String qaldTest,@PathVariable("qald-train") String qaldTrain,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		DocumentDAO documentDao = new DocumentDAO();
		List<DatasetList> datasetVersionList = new ArrayList<DatasetList>();
		datasetVersionList.add(new DatasetList(1, qaldTest));
		datasetVersionList.add(new DatasetList(2, qaldTrain));
		
		ModelAndView mav = new ModelAndView("document-list");
		mav.addObject("datasets", documentDao.getCollections(datasetVersionList));
	    return mav;  
	}
	/**
	 * 
	 * @param id
	 * @param datasetVersion
	 * 
	 * This method use to display detail of document
	 * 
	 * 
	 */
	@RequestMapping(value = "/document-list/detail/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDetail(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);
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
		if (documentItem.getId()!=null) {
			String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItem.getSparqlQuery();
			String goldenAnswer = documentItem.getGoldenAnswer().toString();
			Boolean aggregation = documentItem.getAggregation();
			String answerType = documentItem.getAnswerType();
			Boolean onlydbo = documentItem.getOnlydbo();
			Boolean hybrid = documentItem.getHybrid();
			Boolean outOfScope = documentItem.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
			
			/** Pretty display Query Sparql **/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);
			
			/** Retrieve online answer from current endpoint **/
			String onlineAnswer = ss.getQuery(sprqlQuery);
			
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
			mav.addObject("onlineAnswer", onlineAnswer);
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
					
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
		}
		return mav;  
	}
	
	@RequestMapping(value = "/document-list/detail-correction/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDetailCorrection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		DocumentDAO documentDao = new DocumentDAO();		
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion);
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
		if (documentItem.getId()!=null) {
			String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItem.getSparqlQuery();
			String goldenAnswer = documentItem.getGoldenAnswer().toString();
			Boolean aggregation = documentItem.getAggregation();
			String answerType = documentItem.getAnswerType();
			Boolean onlydbo = documentItem.getOnlydbo();
			Boolean hybrid = documentItem.getHybrid();
			Boolean outOfScope = documentItem.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
			
			/** Pretty display Query Sparql **/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);
			
			/** Retrieve online answer from current endpoint **/
			String onlineAnswer = ss.getQuery(sprqlQuery);
			
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
			mav.addObject("onlineAnswer", onlineAnswer);
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, "UserDatasetCorrection");
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
					
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
		}
		return mav;  
	}
	/*
	 * Autosave Document
	 */
	@RequestMapping(value = "/document-list/document/save", method = RequestMethod.POST)
	public @ResponseBody DatasetModel save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		
		DocumentDAO documentDao = new DocumentDAO();
		
		int userId=user.getId();
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
		DatasetModel document = documentDao.getDocument(id, datasetVersion);
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		
		if (udcDao.isDocumentExist(userId, id, datasetVersion)) {
			UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, id, datasetVersion);
			documentCorrection.setAnswerType(answerType);
			documentCorrection.setAggregation(Boolean.parseBoolean(aggregation));
			documentCorrection.setOnlydbo(Boolean.parseBoolean(onlydbo));
			documentCorrection.setHybrid(Boolean.parseBoolean(hybrid));
			documentCorrection.setSparqlQuery(sparqlQuery);
			documentCorrection.setPseudoSparqlQuery(pseudoSparqlQuery);
			documentCorrection.setOutOfScope(Boolean.parseBoolean(outOfScope));
			
			udcDao.updateDocument(documentCorrection);
		}else {
			
			UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setId(id);
			documentCorrection.setTransId("1");
			documentCorrection.setAnswerType(answerType);
			documentCorrection.setAggregation(Boolean.parseBoolean(aggregation));
			documentCorrection.setOnlydbo(Boolean.parseBoolean(onlydbo));
			documentCorrection.setHybrid(Boolean.parseBoolean(hybrid));
			documentCorrection.setSparqlQuery(sparqlQuery);
			documentCorrection.setPseudoSparqlQuery(pseudoSparqlQuery);
			documentCorrection.setOutOfScope(Boolean.parseBoolean(outOfScope));
			documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
			documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
			documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
			documentCorrection.setUserId(userId);
			documentCorrection.setRevision("1");
			documentCorrection.setLastRevision("111");
			
			udcDao.addDocument(documentCorrection);
		}
		return document;
	}
	@RequestMapping(value = "/document-list/document/edit-question/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String editQuestion(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel documentItem = documentDao.getDocument(datasetId, datasetVersion);
		
		String id =request.getParameter("id");
		String value =request.getParameter("value");
		
		Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
		Map<String, String> mapLanguagetToQuestion = new HashMap<String, String>();
		for (Map.Entry<String, String> mapEntry : languageToQuestion.entrySet()) {
			if (mapEntry.getKey().equals(id)) {
				mapLanguagetToQuestion.put(mapEntry.getKey(), value);
			}else {
				mapLanguagetToQuestion.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		documentItem.setLanguageToQuestion(mapLanguagetToQuestion);
		documentDao.updateDocument(documentItem, datasetVersion);
		return "Dataset has been updated";
	}
	
	//Edit function of Question keyword 
	@RequestMapping(value = "/document-list/document/edit-keyword/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String ediKeyword(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel documentItem = documentDao.getDocument(datasetId, datasetVersion);
		
		String id =request.getParameter("id");
		String value =request.getParameter("value");
		
		Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
		Map<String, List<String>> mapLanguageToKeyword = new HashMap<String, List<String>>();
		for (Map.Entry<String, List<String>> mapEntry : languageToKeyword.entrySet()) {
			if (mapEntry.getKey().equals(id)) {
				List<String> listValue = new ArrayList<String>();
				JSONArray jsonArrayValue = new JSONArray(value);
				for (int x=0; x<jsonArrayValue.length();x++) {
					listValue.add(x, jsonArrayValue.getString(x));
				}
				mapLanguageToKeyword.put(mapEntry.getKey(), listValue);
			}else {
				mapLanguageToKeyword.put(mapEntry.getKey(), mapEntry.getValue());
			}
		}
		documentItem.setLanguageToKeyword(mapLanguageToKeyword);
		documentDao.updateDocument(documentItem, datasetVersion);
		return "Dataset has been updated";
	}
	@RequestMapping(value = "/document-list/document/change-answer-from-file/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String ediAnswerFile(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "Dataset has been updated";
	}	
}
