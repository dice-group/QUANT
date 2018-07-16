package app.controller;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.security.auth.message.callback.PrivateKeyCallback.Request;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.openrdf.sail.rdbms.algebra.StringValue;
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
import com.mongodb.BasicDBObject;

import app.dao.CookieDAO;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.dao.UserLogDAO;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.Question;
import app.model.User;
import app.model.UserDatasetCorrection;
import app.model.UserDatasetCorrectionTemp;
import app.model.UserLog;
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
		mav.addObject("datasets", documentDao.getAllDatasets(userId));
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
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId = user.getId();
		DocumentDAO documentDao = new DocumentDAO();
		List<DatasetList> datasetVersionList = new ArrayList<DatasetList>();
		datasetVersionList.add(new DatasetList(1, qaldTest));
		datasetVersionList.add(new DatasetList(2, qaldTrain));
		
		ModelAndView mav = new ModelAndView("document-list-master");
		mav.addObject("datasets", documentDao.getCollections(userId, datasetVersionList));
	    return mav;  
	}
	
	@RequestMapping(value = "/document-list/curated-question/{qald-test}/{qald-train}", method = RequestMethod.GET)
	public ModelAndView showCuratedQuestionList(@PathVariable("qald-test") String qaldTest,@PathVariable("qald-train") String qaldTrain,HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		UserDatasetCorrectionDAO curatedQuestion = new UserDatasetCorrectionDAO();
		List<DatasetList> datasetVersionList = new ArrayList<DatasetList>();
		datasetVersionList.add(new DatasetList(1, qaldTest));
		datasetVersionList.add(new DatasetList(2, qaldTrain));
		
		ModelAndView mav = new ModelAndView("document-curate-list");
		mav.addObject("datasets", curatedQuestion.getAllDatasetsInParticularVersion(userId, qaldTrain, qaldTest));
	    return mav;  
	}
	/**
	 * 
	 * @param id
	 * @param datasetVersion
	 * 
	 * This method is used to display detail of document
	 * 
	 * 
	 */
	/**
	 * @param id
	 * @param datasetVersion
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
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
		
		/* start check data correction first */
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start initial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button
		/* end */	
		
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";	
		String idNext = documentDao.getNextDocument(id, datasetVersion);
		String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollection(datasetVersion);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollection(previousCollection);
				if (lastRecord != null) {
					idPrevious = lastRecord;
					previousDataset = previousCollection;
					previousStatus="";
				}				
			}
			
		}
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		
		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItemCorrection.getId());
		/** end setting previous and next record **/
		
		// initial is curated = false
		Boolean isSparqlQueryCurated = false;
		Boolean isKeywordCurated = false;
		Boolean isQuestionTranslationCurated = false;
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;				
		Boolean questionRemoveStatus;		
		
		if ((documentItemCorrection.getId()==null)) {
			questionRemoveStatus = true;			
		}else {
			if (documentItemCorrection.getStatus().equals("removed")) {
				questionRemoveStatus = false;
			}else {
				questionRemoveStatus = true;
			}
		}
		mav.addObject("questionRemoveStatus", questionRemoveStatus);
		
		if (documentItemCorrection.getId()==null) { //document has not been curated
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
						
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			
			/** Retrieve answer from Virtuoso current endpoint **/
			Set<String> results = new HashSet();
			
			if (ss.isASKQuery(languageToQuestionEn)) {
				String result = ss.getResultAskQuery(sprqlQuery);
				mav.addObject("onlineAnswer", result);				
			}else {				
				results = ss.getQuery(sprqlQuery);
				mav.addObject("onlineAnswer", results);
			}			
			
			mav.addObject("query",sprqlQuery);
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
			mav.addObject("SPARQL",sprqlQuery);			
			
			
			/** Provide suggestion **/
			String curatedStatus = "not curated";
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			Question translations= documentDao.getQuestionTranslations(languageToQuestionEn);			
			
			//Check whether keywords suggestion has been accepted
			if (documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", false);
				//provide menu to do keywords translations
				mav.addObject("addKeywordsTranslationsStatus", true);
				if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {					
					mav.addObject("keywordsTranslations",translations.getLanguageToKeyword());
				}else {					
					mav.addObject("keywordsTranslations",documentCorrectionDao.generateKeywordsTranslations(user.getId(), id, datasetVersion));
				}					
			}
			
			//Check whether the question needs keywords suggestion
			if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion)))) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("keywordsTranslation", translations.getLanguageToKeyword());
			}	
			
			//check whether the question needs new translations
			if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
				mav.addObject("addQuestionTranslationsStatus", true);
				mav.addObject("questionTranslation", translations.getLanguageToQuestion());
			}
			
		}else {			
			//}
			// document has been curated
			String languageToQuestionEn = documentItemCorrection.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItemCorrection.getSparqlQuery();
			String goldenAnswer = documentItemCorrection.getGoldenAnswer().toString();
			String aggregation = documentItemCorrection.getAggregation();
			String answerType = documentItemCorrection.getAnswerType();
			String onlydbo = documentItemCorrection.getOnlydbo();
			String hybrid = documentItemCorrection.getHybrid();
			String outOfScope = documentItemCorrection.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
			
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
			mav.addObject("SPARQL", sprqlQuery);
			
			/** Provide suggestion **/
			
			mav.addObject("isExist", "yes");			
			//mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItemCorrection.getUserId(), id, datasetVersion, languageToKeyword);
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItemCorrection.getUserId(), id, datasetVersion, languageToQuestion); 
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");
			mav.addObject("isSparqlQueryCurated", isSparqlQueryCurated);
			mav.addObject("isKeywordCurated", isKeywordCurated);
			mav.addObject("isQuestionTranslationCurated", isQuestionTranslationCurated);
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			
			Question questionTranslation= documentDao.getQuestionTranslations(languageToQuestionEn);
			
			//Check whether keywords suggestion has been accepted
			if (documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItemCorrection.getUserId(), id, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", false);
				//provide menu to do keywords translations
				if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {
					mav.addObject("addKeywordsTranslationsStatus", true);
				}else {
					mav.addObject("addKeywordsTranslationsStatus", true);
				}
					
			}
			//Check whether the question needs keywords suggestion
			if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItemCorrection.getUserId(), id, datasetVersion)))) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("keywordsTranslation", questionTranslation.getLanguageToKeyword());
			}	
			
			//check whether the question needs new translations
			if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
				mav.addObject("addQuestionTranslationsStatus", true);
				mav.addObject("questionTranslation", questionTranslation.getLanguageToQuestion());
			}	
		}		
		return mav;  
	}
	//ini fungsi buat nampilin removed question
	@RequestMapping(value = "/document-list/detail/removed/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showRemovedDocument(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		/* start check data correction first */
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getRemovedDocument(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start initial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button
		/* end */	
		
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";	
		String idNext = documentDao.getNextDocument(id, datasetVersion);
		String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollection(datasetVersion);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollection(previousCollection);
				if (lastRecord != null) {
					idPrevious = lastRecord;
					previousDataset = previousCollection;
					previousStatus="";
				}				
			}
			
		}
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		
		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItemCorrection.getId());
		/** end setting previous and next record **/
		
		// initial is curated = false
		Boolean isSparqlQueryCurated = false;
		Boolean isKeywordCurated = false;
		Boolean isQuestionTranslationCurated = false;
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;				
		Boolean questionRemoveStatus = false;		
		mav.addObject("questionRemoveStatus", questionRemoveStatus);
		
		String languageToQuestionEn = documentItemCorrection.getLanguageToQuestion().get("en").toString();
		String sprqlQuery = documentItemCorrection.getSparqlQuery();
		String goldenAnswer = documentItemCorrection.getGoldenAnswer().toString();
		String aggregation = documentItemCorrection.getAggregation();
		String answerType = documentItemCorrection.getAnswerType();
		String onlydbo = documentItemCorrection.getOnlydbo();
		String hybrid = documentItemCorrection.getHybrid();
		String outOfScope = documentItemCorrection.getOutOfScope();
		Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
		Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
		
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
		mav.addObject("SPARQL", sprqlQuery);
		
		/** Provide suggestion **/
		
		mav.addObject("isExist", "yes");			
		//mav.addObject("resultStatus", resultStatus);
		
		/** Provide notification for curated fields **/
		isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "sprqlQuery");
		isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItemCorrection.getUserId(), id, datasetVersion, languageToKeyword);
		isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItemCorrection.getUserId(), id, datasetVersion, languageToQuestion); 
		isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
		isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
		isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
		isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
		isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");
		mav.addObject("isSparqlQueryCurated", isSparqlQueryCurated);
		mav.addObject("isKeywordCurated", isKeywordCurated);
		mav.addObject("isQuestionTranslationCurated", isQuestionTranslationCurated);
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);
		
		Question questionTranslation= documentDao.getQuestionTranslations(languageToQuestionEn);
		
		//Check whether keywords suggestion has been accepted
		if (documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItemCorrection.getUserId(), id, datasetVersion)) {
			mav.addObject("addKeywordsSuggestionStatus", false);
			//provide menu to do keywords translations
			if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {
				mav.addObject("addKeywordsTranslationsStatus", true);
			}else {
				mav.addObject("addKeywordsTranslationsStatus", true);
			}
				
		}
		//Check whether the question needs keywords suggestion
		if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItemCorrection.getUserId(), id, datasetVersion)))) {
			mav.addObject("addKeywordsSuggestionStatus", true);
			mav.addObject("keywordsTranslation", questionTranslation.getLanguageToKeyword());
		}	
		
		//check whether the question needs new translations
		if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
			mav.addObject("addQuestionTranslationsStatus", true);
			mav.addObject("questionTranslation", questionTranslation.getLanguageToQuestion());
		}	
		return mav;  
	}
	/**
	 * @param id
	 * @param datasetVersion
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/document-list/detail/{id}/{datasetVersion}/prev", method = RequestMethod.GET)
	public ModelAndView showPrevDocumentListDetail(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		/* start check data correction first */
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start intial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button		
		/* end */
		
		
		
		
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";	
		String idNext = documentDao.getNextDocument(id, datasetVersion);
		String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollection(datasetVersion);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollection(previousCollection);
				if (lastRecord != null) {
					idPrevious = lastRecord;
					previousDataset = previousCollection;
					previousStatus="";
				}				
			}
			
		}
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		
		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItemCorrection.getId());
		/** end setting previous and next record **/
		
		// initial is curated = false
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;				
		
		boolean questionRemoveStatus;
		if ((documentItemCorrection.getId()==null)) {
			questionRemoveStatus = true;
		}else {
			if (documentItemCorrection.getStatus().equals("removed")) {
				questionRemoveStatus = false;
			}else {
				questionRemoveStatus = true;
			}
		}
		mav.addObject("questionRemoveStatus", questionRemoveStatus);
		if (documentItemCorrection.getId()==null) { //document has not been curated
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
			System.out.println("Sparql is :"+ sprqlQuery);
			
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			
			/** Retrieve answer from Virtuoso current endpoint **/
			Set<String> results = new HashSet();
			/*results.add(null);
			String result = null;*/
			if (ss.isASKQuery(languageToQuestionEn)) {
				String result = ss.getResultAskQuery(sprqlQuery);
				mav.addObject("onlineAnswer", result);				
			}else {				
				results = ss.getQuery(sprqlQuery);
				mav.addObject("onlineAnswer", results);
			}			
			
			mav.addObject("query",sprqlQuery);
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
			mav.addObject("SPARQL",sprqlQuery);
			
			
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, "not curated", user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			//check whether the question needs translations 
			mav.addObject("addKeywordsSuggestionStatus", documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion));
		//	mav.addObject("addKeywordsAndQuestionTranslationsStatus", documentDao.doesNeedTranslations(languageToQuestionEn));
			
		}else {		
			//check whether current question has been removed or not
			//if (!documentItemCorrection.getStatus().equals(null)) {
			//modifikasi disini aja kodenya, gimana?
			if (documentItemCorrection.getStatus().equals("removed")) {			 
				mav = new ModelAndView("redirect:/document-list/detail/"+idPrevious+"/"+nextDataset);				
				//ModelAndView mav = new ModelAndView("redirect:/document-list/detail/removed/"+idPrevious+"/"+datasetVersion);
				return mav;
			}
			// document has been curated			
			String languageToQuestionEn = documentItemCorrection.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItemCorrection.getSparqlQuery();
			String goldenAnswer = documentItemCorrection.getGoldenAnswer().toString();
			String aggregation = documentItemCorrection.getAggregation();
			String answerType = documentItemCorrection.getAnswerType();
			String onlydbo = documentItemCorrection.getOnlydbo();
			String hybrid = documentItemCorrection.getHybrid();
			String outOfScope = documentItemCorrection.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
			
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
			mav.addObject("SPARQL", sprqlQuery);
			
			/** Provide suggestion **/			
			mav.addObject("isExist", "yes");
			
			/** Provide notification for curated fields **/
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");			
		}
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);
		return mav;  
	}
	/**
	 * @param id
	 * @param datasetVersion
	 * @param request
	 * @param response
	 * @param redirectAttributes
	 * @return
	 */
	@RequestMapping(value = "/document-list/detail/{id}/{datasetVersion}/next", method = RequestMethod.GET)
	public ModelAndView showNextDocumentListDetail(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		/* start check data correction first */
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start intial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button		
		/* end */
		
		
		
		
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";	
		String idNext = documentDao.getNextDocument(id, datasetVersion);
		String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollection(datasetVersion);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollection(previousCollection);
				if (lastRecord != null) {
					idPrevious = lastRecord;
					previousDataset = previousCollection;
					previousStatus="";
				}				
			}
			
		}
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		
		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItemCorrection.getId());
		/** end setting previous and next record **/
		
		// initial is curated = false
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;				
		
		boolean questionRemoveStatus;
		if ((documentItemCorrection.getId()==null)) {
			questionRemoveStatus = true;
		}else {
			if (documentItemCorrection.getStatus().equals("removed")) {
				questionRemoveStatus = false;
			}else {
				questionRemoveStatus = true;
			}
		}
		mav.addObject("questionRemoveStatus", questionRemoveStatus);
		if (documentItemCorrection.getId()==null) { //document has not been curated
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
			System.out.println("Sparql is :"+ sprqlQuery);
			
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			
			/** Retrieve answer from Virtuoso current endpoint **/
			Set<String> results = new HashSet();			
			if (ss.isASKQuery(languageToQuestionEn)) {
				String result = ss.getResultAskQuery(sprqlQuery);
				mav.addObject("onlineAnswer", result);				
			}else {				
				results = ss.getQuery(sprqlQuery);
				mav.addObject("onlineAnswer", results);
			}			
			
			mav.addObject("query",sprqlQuery);
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
			mav.addObject("SPARQL",sprqlQuery);
			
			
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, "not curated", user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);			
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			//Check whether keywords suggestion has been accepted
			if (documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", false);
				//provide menu to do keywords translations
				if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {
					mav.addObject("addKeywordsTranslationsStatus", true);
				}
			}
			//Check whether the question needs keywords suggestion
			if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion)))) {
				mav.addObject("addKeywordsSuggestionStatus", true);
			//	mav.addObject("keywordsTranslation", questionTranslation.getLanguageToKeyword());
			}	
			
			//check whether the question needs new translations
			if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
				mav.addObject("addQuestionTranslationsStatus", true);
			//	mav.addObject("questionTranslation", questionTranslation.getLanguageToQuestion());
			}			
		}else { 
			//check whether current question has been removed or not
			//if (!documentItemCorrection.getStatus().equals(null)) {
			if (documentItemCorrection.getStatus().equals("removed")) {			 
				mav = new ModelAndView("redirect:/document-list/detail/"+idNext+"/"+nextDataset);				
				return mav;
			}
			//}
			// document has been curated
			String languageToQuestionEn = documentItemCorrection.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItemCorrection.getSparqlQuery();
			String goldenAnswer = documentItemCorrection.getGoldenAnswer().toString();
			String aggregation = documentItemCorrection.getAggregation();
			String answerType = documentItemCorrection.getAnswerType();
			String onlydbo = documentItemCorrection.getOnlydbo();
			String hybrid = documentItemCorrection.getHybrid();
			String outOfScope = documentItemCorrection.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
			
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
			mav.addObject("SPARQL", sprqlQuery);
			
			/** Provide suggestion **/			
			mav.addObject("isExist", "yes");			
			//mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");			
		}
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);
		return mav;  
	}
	
	@RequestMapping(value = "/document-list/detail-master/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDetailMaster(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		//check data correction first
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail-master");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "hidden");//Show start button		
		
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";
		String idNext = documentDao.getNextDocument(id, datasetVersion);;
		String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollection(datasetVersion);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollection(previousCollection);
				if (lastRecord != null) {
					idPrevious = lastRecord;
					previousDataset = previousCollection;
					previousStatus="";
				}				
			}
			
		}
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		
		boolean questionRemoveStatus = false;
		mav.addObject("questionRemoveStatus", questionRemoveStatus);		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail-master");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItem.getId());
		
		/** end setting previous and next record **/
		//check whether the document exists
		if (documentItem.getId()!=null) {
			String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItem.getSparqlQuery();
			String goldenAnswer = documentItem.getGoldenAnswer().toString();
			Boolean aggregation = documentItem.getAggregation();
			String answerType = documentItem.getAnswerType();
			Boolean onlydbo = documentItem.getOnlydbo();
			Boolean hybrid = documentItem.getHybrid();
			String outOfScope = String.valueOf(documentItem.getOutOfScope());
			Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
			
			
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			
			/** Retrieve answer from Virtuoso current endpoint **/
			Set<String> results = new HashSet();			
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
			
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, "not curated", user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);			
		}		
		return mav;  
	}
	
	//Display next question for master dataset
	@RequestMapping(value = "/document-list/detail-master/{id}/{datasetVersion}/next", method = RequestMethod.GET)
	public ModelAndView showNextDocumentListDetailMaster(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		
		//check data correction first
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail-master");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "hidden");//Show start button		
		
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";
		String idNext = documentDao.getNextDocument(id, datasetVersion);;
		String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollection(datasetVersion);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollection(previousCollection);
				if (lastRecord != null) {
					idPrevious = lastRecord;
					previousDataset = previousCollection;
					previousStatus="";
				}				
			}
			
		}
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		
		boolean questionRemoveStatus = false;
		mav.addObject("questionRemoveStatus", questionRemoveStatus);		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail-master");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItem.getId());
		
		/** end setting previous and next record **/
		//check whether the document exists
		if (documentItem.getId()!=null) {
			String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItem.getSparqlQuery();
			String goldenAnswer = documentItem.getGoldenAnswer().toString();
			Boolean aggregation = documentItem.getAggregation();
			String answerType = documentItem.getAnswerType();
			Boolean onlydbo = documentItem.getOnlydbo();
			Boolean hybrid = documentItem.getHybrid();
			String outOfScope = String.valueOf(documentItem.getOutOfScope());
			Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
			
			
			/** Pretty display of Sparql Query**/
			SparqlService ss = new SparqlService();
			String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
			
			/** Retrieve answer from Virtuoso current endpoint **/
			Set<String> results = new HashSet();			
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
			
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, "not curated", user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);			
		}
		
		return mav;  
	}
	
		//Display previous question for master dataset	
		@RequestMapping(value = "/document-list/detail-master/{id}/{datasetVersion}/prev", method = RequestMethod.GET)
		public ModelAndView showPreviousDocumentListDetailMaster(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
			Cookie[] cks = request.getCookies();
			CookieDAO cookieDao = new CookieDAO();
			if (!cookieDao.isValidate(cks)) {
				redirectAttributes.addFlashAttribute("message","Session Expired.");
				ModelAndView mav = new ModelAndView("redirect:/login");
				return mav;
			}
			
			//check data correction first
			UserDAO userDao = new UserDAO();
			User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
			DocumentDAO documentDao = new DocumentDAO();
			ModelAndView mav = new ModelAndView("document-detail-master");
			DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
			mav.addObject("classDisplay", "hidden");//Show start button		
			
			/** Setting previous and next record **/
			String previousStatus = "";
			String nextStatus="";
			String idNext = documentDao.getNextDocument(id, datasetVersion);;
			String idPrevious = documentDao.getPreviousDocument(id, datasetVersion);
			String previousDataset = datasetVersion;
			String nextDataset = datasetVersion;
			
			//	Previous Part	
			if (idPrevious==null) {
				previousStatus = "disabled=\"disabled\"";
				idPrevious = id;
				
				//Get previous collection
				String previousCollection = documentDao.getPreviousCollection(datasetVersion);
				
				if (previousCollection != null) {
					String lastRecord = documentDao.getLastRecordCollection(previousCollection);
					if (lastRecord != null) {
						idPrevious = lastRecord;
						previousDataset = previousCollection;
						previousStatus="";
					}				
				}
				
			}
			
			// Next part
			if (idNext==null) {
				nextStatus = "disabled=\"disabled\"";
				idNext = id;
				
				//Get Next collection
				String nextCollection = documentDao.getNextCollection(datasetVersion);
				if (nextCollection != null) {
					String nextRecord = documentDao.getNextRecordCollection(nextCollection);
					if (nextRecord != null) {
						idNext = nextRecord;
						nextDataset = nextCollection;
						nextStatus = "";
					}					
				}
			}
			
			boolean questionRemoveStatus = false;
			mav.addObject("questionRemoveStatus", questionRemoveStatus);		
			
			mav.addObject("previousStatus", previousStatus);
			mav.addObject("nextStatus", nextStatus);
			mav.addObject("pageName", "detail-master");
			mav.addObject("addUrlParameter", "");
			mav.addObject("datasetVersionPrevious", previousDataset);
			mav.addObject("datasetVersionNext", nextDataset);
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
			mav.addObject("isCurated", documentItem.getId());
			
			/** end setting previous and next record **/
			//check whether the document exists
			if (documentItem.getId()!=null) {
				String languageToQuestionEn = documentItem.getLanguageToQuestion().get("en").toString();
				String sprqlQuery = documentItem.getSparqlQuery();
				String goldenAnswer = documentItem.getGoldenAnswer().toString();
				Boolean aggregation = documentItem.getAggregation();
				String answerType = documentItem.getAnswerType();
				Boolean onlydbo = documentItem.getOnlydbo();
				Boolean hybrid = documentItem.getHybrid();
				String outOfScope = String.valueOf(documentItem.getOutOfScope());
				Map<String, List<String>> languageToKeyword = documentItem.getLanguageToKeyword();
				Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
				
				
				/** Pretty display of Sparql Query**/
				SparqlService ss = new SparqlService();
				String formatedSparqlQuery = ss.getQueryFormated(sprqlQuery);	
				
				/** Retrieve answer from Virtuoso current endpoint **/
				Set<String> results = new HashSet();			
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
				
				
				/** Provide suggestion **/
				DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, "not curated", user.getId());
				String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
				String aggregationSugg = documentItemSugg.getAggregationSugg();		
				String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
				String hybridSugg = documentItemSugg.getHybridSugg();
				String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
				Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
				List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
				String resultStatus = documentItemSugg.getResultStatus();	
							
				mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
				mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
				mav.addObject("answerTypeSugg", answerTypeSugg);
				mav.addObject("aggregationSugg", aggregationSugg);
				mav.addObject("onlyDboSugg", onlyDboSugg);
				mav.addObject("hybridSugg", hybridSugg);
				mav.addObject("outOfScopeSugg", outOfScopeSugg);
				mav.addObject("isExist", "yes");
				mav.addObject("resultStatus", resultStatus);
			}else {
				mav.addObject("isExist", "no");
				mav.addObject("id", id);
				mav.addObject("datasetVersion", datasetVersion);			
			}
			
			return mav;  
		}
	
	@RequestMapping(value = "/document-list/detail-correction/{id}/{datasetVersion}/{editStatus}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDetailCorrection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, @PathVariable("editStatus") String editStatus, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); 
		ModelAndView mav = new ModelAndView("document-detail");
		if (editStatus.equals("yes"))
			mav.addObject("classDisplay", "btn btn-default");//Hide start button
		else
			mav.addObject("classDisplay", "hidden");//Hide start button
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); //get documents
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";
		String datasetVersionPrevious;
		String datasetVersionNext;
		String idPrevious;
		String idNext;
		UserDatasetCorrection documentNext = documentCorrectionDao.getNextDocument(user.getId(), id, datasetVersion); 
		UserDatasetCorrection documentPrevious = documentCorrectionDao.getPreviousDocument(user.getId(), id, datasetVersion);
		String pageName = "detail-correction";
		String addUrlParameter = "/no";
		
		if (editStatus.equals("yes"))
			addUrlParameter="/yes";
		
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
		mav.addObject("addUrlParameter", addUrlParameter);
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
			
			/** Provide suggestion **/			
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(id, datasetVersion, "curated", user.getId());
			String resultStatus = documentItemSugg.getResultStatus();			
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");		
		
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			mav.addObject("SPARQL",sprqlQuery);
			
			boolean removedStatus;
			if ((documentItemCorrection.getStatus()).equals("false")) {
				removedStatus = true;
			}else{
				removedStatus = false;
			}				
			mav.addObject("removedStatus", removedStatus);
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	@RequestMapping(value = "/document-list/detail-correction/{id}/{datasetVersion}/{editStatus}/next", method = RequestMethod.GET)
	public ModelAndView showNextDocumentListDetailCorrection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, @PathVariable("editStatus") String editStatus, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); 
		ModelAndView mav = new ModelAndView("document-detail");
		if (editStatus.equals("yes"))
			mav.addObject("classDisplay", "btn btn-default");//Hide start button
		else
			mav.addObject("classDisplay", "hidden");//Hide start button
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); //get documents
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";
		String datasetVersionPrevious;
		String datasetVersionNext;
		String idPrevious;
		String idNext;
		UserDatasetCorrection documentNext = documentCorrectionDao.getNextDocument(user.getId(), id, datasetVersion); 
		UserDatasetCorrection documentPrevious = documentCorrectionDao.getPreviousDocument(user.getId(), id, datasetVersion);
		String pageName = "detail-correction";
		String addUrlParameter = "/no";
		
		if (editStatus.equals("yes"))
			addUrlParameter="/yes";
		
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
		mav.addObject("addUrlParameter", addUrlParameter);
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
			
			/** Provide suggestion **/			
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(id, datasetVersion, "curated", user.getId());			
			String resultStatus = documentItemSugg.getResultStatus();			
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");		
		
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			mav.addObject("SPARQL",sprqlQuery);
			
			boolean removedStatus;
			if ((documentItemCorrection.getStatus()).equals("false")) {
				removedStatus = true;
			}else{
				removedStatus = false;
			}				
			mav.addObject("removedStatus", removedStatus);
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	@RequestMapping(value = "/document-list/detail-correction/{id}/{datasetVersion}/{editStatus}/prev", method = RequestMethod.GET)
	public ModelAndView showPrevDocumentListDetailCorrection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, @PathVariable("editStatus") String editStatus, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); 
		ModelAndView mav = new ModelAndView("document-detail");
		if (editStatus.equals("yes"))
			mav.addObject("classDisplay", "btn btn-default");//Hide start button
		else
			mav.addObject("classDisplay", "hidden");//Hide start button
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion); //get documents
		/** Setting previous and next record **/
		String previousStatus = "";
		String nextStatus="";
		String datasetVersionPrevious;
		String datasetVersionNext;
		String idPrevious;
		String idNext;
		UserDatasetCorrection documentNext = documentCorrectionDao.getNextDocument(user.getId(), id, datasetVersion); 
		UserDatasetCorrection documentPrevious = documentCorrectionDao.getPreviousDocument(user.getId(), id, datasetVersion);
		String pageName = "detail-correction";
		String addUrlParameter = "/no";
		
		if (editStatus.equals("yes"))
			addUrlParameter="/yes";
		
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
		mav.addObject("addUrlParameter", addUrlParameter);
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
			
			/** Provide suggestion **/			
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(id, datasetVersion, "curated", user.getId());			
			String resultStatus = documentItemSugg.getResultStatus();			
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "hybrid");		
		
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			mav.addObject("SPARQL",sprqlQuery);
			
			boolean removedStatus;
			if ((documentItemCorrection.getStatus()).equals("false")) {
				removedStatus = true;
			}else{
				removedStatus = false;
			}				
			mav.addObject("removedStatus", removedStatus);
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);			
		}
		return mav;  
	}
	
	@RequestMapping(value = "/document-list/start-correction/{id}/{datasetVersion}", method = RequestMethod.GET)
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
		
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		ModelAndView mav = new ModelAndView("document-detail-curate");
		mav.addObject("disabledForm", "");
		mav.addObject("startButton", "On Process");
		mav.addObject("startButtonDisabled", "disabled");
		mav.addObject("displayStatus", "");
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
		String statusNoChangeChk="";
		
		// initial is curated = false
		Boolean isSparqlQueryCurated = false;
		Boolean isKeywordCurated = false;
		Boolean isQuestionTranslationCurated = false;
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;
		
		int nextRevision = 0;
		int currentRevision = 0;
		String curatedStatus;
		if (documentItem.getId()!=null) {
			//record starting time of curation
			//check in what revision the current curated one is. Make sure it is not the cancel curated one
			currentRevision = documentItem.getRevision();			
			if ((documentItem.getStatus().equals("curated")) || (documentItem.getStatus().equals("noNeedChanges"))) 
			{				
				nextRevision = currentRevision+1;
			}else if (documentItem.getStatus() == "cancelled") {
				nextRevision = currentRevision;
			}	
			
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
			
			
			/** Provide suggestion **/
			curatedStatus = "curated";
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			/** is Curated ? **/
			isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItem.getUserId(), id, datasetVersion, languageToKeyword);
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItem.getUserId(), id, datasetVersion, languageToQuestion); 
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "hybrid");
			mav.addObject("isSparqlQueryCurated", isSparqlQueryCurated);
			mav.addObject("isKeywordCurated", isKeywordCurated);
			mav.addObject("isQuestionTranslationCurated", isQuestionTranslationCurated);
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			
			DocumentDAO documentDao = new DocumentDAO();
			Question translations= documentDao.getQuestionTranslations(languageToQuestionEn);
			
			//Check whether keywords suggestion has been accepted
			if (documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItem.getUserId(), id, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", false);
				//provide menu to do keywords translations
				mav.addObject("addKeywordsTranslationsStatus", true);
				if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {					
					mav.addObject("keywordsTranslations",translations.getLanguageToKeyword());
				}else {					
					mav.addObject("keywordsTranslations",documentCorrectionDao.generateKeywordsTranslations(documentItem.getUserId(), id, datasetVersion));
				}
					
			}
			//Check whether the question needs keywords suggestion
			if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItem.getUserId(), id, datasetVersion)))) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("keywordsTranslation", translations.getLanguageToKeyword());
			}	
			
			//check whether the question needs new translations
			if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
				mav.addObject("addQuestionTranslationsStatus", true);
				mav.addObject("questionTranslation", translations.getLanguageToQuestion());
			}	
			
		}else { //document has not been curated 
			//set number of revision as 1
			nextRevision = 1;
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel documentMaster = documentDao.getDocument(id, datasetVersion);
			String languageToQuestionEn = documentMaster.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentMaster.getSparqlQuery();
			String goldenAnswer = documentMaster.getGoldenAnswer().toString();
			String aggregation = String.valueOf(documentMaster.getAggregation());
			String answerType = documentMaster.getAnswerType();
			String onlydbo = String.valueOf(documentMaster.getOnlydbo());
			String hybrid = String.valueOf(documentMaster.getHybrid());
			String outOfScope = String.valueOf(documentMaster.getOutOfScope());
			Map<String, List<String>> languageToKeyword = documentMaster.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentMaster.getLanguageToQuestion();
					
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
			mav.addObject("idPrevious", idPrevious);
			mav.addObject("idNext", idNext);
					
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, "not curated", user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();			
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);			
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			Question questionTranslation= documentDao.getQuestionTranslations(languageToQuestionEn);
			
			//Check whether keywords suggestion has been accepted
			if (documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItem.getUserId(), id, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", false);
				//provide menu to do keywords translations
				if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {
					mav.addObject("addKeywordsTranslationsStatus", true);
				}else {
					mav.addObject("keywordsTranslations", questionTranslation.getLanguageToKeyword());
				}					
			}
			//Check whether the question needs keywords suggestion
			if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItem.getUserId(), id, datasetVersion)))) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("listKeywordSuggestion", documentDao.generateKeywords(languageToQuestionEn));
				mav.addObject("keywordsTranslation", questionTranslation.getLanguageToKeyword());
			}	
			
			//check whether the question needs new translations
			if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
				mav.addObject("addQuestionTranslationsStatus", true);
				mav.addObject("questionTranslation", questionTranslation.getLanguageToQuestion());
			}
		}
		//record starting time of curation of current dataset
		documentItem.setId(id);
		documentItem.setDatasetVersion(datasetVersion);
		documentItem.setUserId(user.getId());	
		
		documentItem.setRevision(nextRevision);
		documentItem.setStartingTimeCuration(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		documentCorrectionDao.addDocument(documentItem);
		
		mav.addObject("statusNoChangeChk", statusNoChangeChk);
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);		
		return mav;
	}
	
	@RequestMapping (value = "/document-list/curate/curation-process/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showCurationProcess (@PathVariable("id") String id, @PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		UserDatasetCorrectionDAO documentCorrectionTempDao = new UserDatasetCorrectionDAO();
		ModelAndView mav = new ModelAndView("document-detail-curate");
		mav.addObject("disabledForm", "");
		mav.addObject("startButton", "On Process");
		mav.addObject("startButtonDisabled", "disabled");
		mav.addObject("displayStatus", "");
		UserDatasetCorrectionTemp documentTemp = documentCorrectionTempDao.getTempDocument(user.getId(), id, datasetVersion);
		String statusNoChangeChk="";
		
		// initial is curated = false
		Boolean isSparqlQueryCurated = false;
		Boolean isKeywordCurated = false;
		Boolean isQuestionTranslationCurated = false;
		Boolean isAnswerTypeCurated = false;
		Boolean isOutOfScopeCurated = false;
		Boolean isAggregationCurated = false;
		Boolean isOnlydboCurated = false;
		Boolean isHybridCurated = false;
		if (documentTemp.getId() != null) {
			statusNoChangeChk = "style='display:none'";
			String languageToQuestionEn = documentTemp.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentTemp.getSparqlQuery();
			String goldenAnswer = documentTemp.getGoldenAnswer().toString();
			String aggregation = documentTemp.getAggregation();
			String answerType = documentTemp.getAnswerType();
			String onlydbo = documentTemp.getOnlydbo();
			String hybrid = documentTemp.getHybrid();
			String outOfScope = documentTemp.getOutOfScope();
			Map<String, List<String>> languageToKeyword = documentTemp.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentTemp.getLanguageToQuestion();
			
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
			
			DatasetSuggestionModel documentItemSugg = documentCorrectionTempDao.implementCorrection(id, datasetVersion, "in curation", user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			List<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			/** is Curated ? **/
			isSparqlQueryCurated = documentCorrectionTempDao.isItemCurated(user.getId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionTempDao.isKeywordCurated(user.getId(), id, datasetVersion, languageToKeyword);
			isQuestionTranslationCurated = documentCorrectionTempDao.isQuestionCurated(user.getId(), id, datasetVersion, languageToQuestion); 
			isAnswerTypeCurated = documentCorrectionTempDao.isItemCurated(user.getId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionTempDao.isItemCurated(user.getId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionTempDao.isItemCurated(user.getId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionTempDao.isItemCurated(user.getId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionTempDao.isItemCurated(user.getId(), id, datasetVersion, "hybrid");
			mav.addObject("isSparqlQueryCurated", isSparqlQueryCurated);
			mav.addObject("isKeywordCurated", isKeywordCurated);
			mav.addObject("isQuestionTranslationCurated", isQuestionTranslationCurated);
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			
			DocumentDAO documentDao = new DocumentDAO();
			Question translations= documentDao.getQuestionTranslations(languageToQuestionEn);
			
			//Check whether keywords suggestion has been accepted
			if (documentCorrectionTempDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", false);
				//provide menu to do keywords translations
				mav.addObject("addKeywordsTranslationsStatus", true);
				if (documentDao.doesNeedKeywordsTranslations(languageToQuestionEn)) {					
					mav.addObject("keywordsTranslations",translations.getLanguageToKeyword());
				}else {					
					mav.addObject("keywordsTranslations",documentCorrectionTempDao.generateKeywordsTranslations(user.getId(), id, datasetVersion));
				}
					
			}
			//Check whether the question needs keywords suggestion
			if ((documentDao.doesNeedKeywordSuggestions(languageToQuestionEn, datasetVersion)) && (!(documentCorrectionTempDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion)))) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("keywordsTranslation", translations.getLanguageToKeyword());
			}	
			
			//check whether the question needs new translations
			if (documentDao.doesNeedQuestionTranslations(languageToQuestionEn)) {
				mav.addObject("addQuestionTranslationsStatus", true);
				mav.addObject("questionTranslation", translations.getLanguageToQuestion());
			}			
		}
		return mav;
	}
	
	@RequestMapping(value = "/document-list/detail/cancel-curate/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView cancelCurate(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId = user.getId();
		
		//find document
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel document = documentDao.getDocument(id, datasetVersion);
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		//get all data from the view part		
		String sparqlQuery = request.getParameter("sparqlQuery");
		String goldenAnswer = request.getParameter("goldenAnswer");
		String endpoint = request.getParameter("endpoint");		
		String answerType = request.getParameter("answerType");
		String aggregation = request.getParameter("aggregation");
		String onlydbo = request.getParameter("onlydbo");
		String hybrid = request.getParameter("hybrid");		
		String pseudoSparqlQuery = request.getParameter("pseudoSparqlQuery");
		String outOfScope = request.getParameter("outOfScope");
		/*Map<String, List<String>> languageToKeyword = request.getParameter("languageToKeyword");
		Map<String, String> languageToQuestion = request.getParameter("languageToQuestion");*/
				
		//build logInfo
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);			
		
		//put all data from master dataset to documentCorrection					
		UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
		documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
		documentCorrection.setAnswerType(document.getAnswerType());
		documentCorrection.setDatasetVersion(datasetVersion);
		documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
		documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
		documentCorrection.setId(id);
		documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
		documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());		
		documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
		documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
		documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
		documentCorrection.setRevision(1);
		documentCorrection.setSparqlQuery(document.getSparqlQuery());
		documentCorrection.setStatus("false");
		documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		documentCorrection.setUserId(userId);
		udcDao.addDocument(documentCorrection);			
				
		userLog.setUserId(userId);
		userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		userLog.setLogType("remove");
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLogDao.addLogCurate(userLog);//add UserLogcdcd		
		
		/** Setting previous and next record **/
		String nextStatus="";	
		String idNext = documentDao.getNextDocument(id, datasetVersion);
		String nextDataset = datasetVersion;
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+idNext+"/"+nextDataset);
		
		return mav;
	}
	
	@RequestMapping(value = "/document-list/curate/remove-question/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView removeQuestion(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId = user.getId();
		
		//find document
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel document = documentDao.getDocument(id, datasetVersion);
		UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		//build logInfo
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
			
		if (udcDao.isDocumentExist(userId, id, datasetVersion)) {
			documentCorrection = udcDao.getDocument(userId, id, datasetVersion);			
			if (documentCorrection.getFinishingTimeCuration()!= null) { // the document is finished in curation
				documentCorrection.setStatus("removed");
				documentCorrection.setRemovingTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				udcDao.updateDocument(documentCorrection);				
			}else {
				//User has started doing curation. but before finish it (button done is not selected), user decide to remove the question
				//new record that has been created in UserDatasetCorrection will be updated where all question's field values are set using value from master data.
				//But status and removingTime will be updated  
				//the only one record in UserDatasetCorrectionTemp will be deleted
				//all log info in UserLogTemp will be deleted				
				documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
				documentCorrection.setAnswerType(document.getAnswerType());
				documentCorrection.setDatasetVersion(datasetVersion);
				documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
				documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
				documentCorrection.setId(id);
				documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
				documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());			
				documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
				documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
				documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());				
				documentCorrection.setSparqlQuery(document.getSparqlQuery());
				documentCorrection.setStatus("removed");
				documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				documentCorrection.setUserId(userId);
				documentCorrection.setRemovingTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));				
				udcDao.removeDocumentBeforeCurationDone(documentCorrection);
				udcDao.deleteTempDocument(userId, id, datasetVersion); 
				udcDao.removeAllCurationLogForRemoveFunction(userId, id, datasetVersion);
			}					
		}else {
			//put all data from master dataset to documentCorrection		
			documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
			documentCorrection.setAnswerType(document.getAnswerType());
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
			documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
			documentCorrection.setId(id);
			documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
			documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());			
			documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
			documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
			documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
			documentCorrection.setRevision(0);
			documentCorrection.setSparqlQuery(document.getSparqlQuery());
			documentCorrection.setStatus("removed");
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setUserId(userId);
			documentCorrection.setRemovingTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			udcDao.addDocument(documentCorrection);			
		}		
		/** Setting previous and next record **/
		String nextStatus="";	
		String idNext = documentDao.getNextDocument(id, datasetVersion);
		String nextDataset = datasetVersion;
		
		// Next part
		if (idNext==null) {
			nextStatus = "disabled=\"disabled\"";
			idNext = id;
			
			//Get Next collection
			String nextCollection = documentDao.getNextCollection(datasetVersion);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollection(nextCollection);
				if (nextRecord != null) {
					idNext = nextRecord;
					nextDataset = nextCollection;
					nextStatus = "";
				}					
			}
		}
		ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+idNext+"/"+nextDataset);		
		return mav;
	}
	
	//Curation process is done
	@RequestMapping (value = "document-list/curate/done/{id}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody ModelAndView curateIsDone (@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){		
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();		
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));	
		
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}	
		
		//get curated data from temporary table
		UserDatasetCorrectionDAO document = new UserDatasetCorrectionDAO();
		UserDatasetCorrectionTemp tempCuratedDocument = document.getTempDocument(user.getId(), id, datasetVersion);
		String startingTime =  document.getStartingTimeCuration(user.getId(), id, datasetVersion);
		int revision = document.getRevisionOfCuration(user.getId(), id, datasetVersion);
		
		//provide document to be stored in UserDatasetCorrection table
		UserDatasetCorrection finalDocument = new UserDatasetCorrection();
		finalDocument.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		finalDocument.setId(id);
		finalDocument.setUserId(user.getId());
		finalDocument.setDatasetVersion(datasetVersion);
		finalDocument.setAnswerType(tempCuratedDocument.getAnswerType());
		finalDocument.setAggregation(tempCuratedDocument.getAggregation());
		finalDocument.setHybrid(tempCuratedDocument.getHybrid());
		finalDocument.setOnlydbo(tempCuratedDocument.getOnlydbo());
		finalDocument.setSparqlQuery(tempCuratedDocument.getSparqlQuery());
		finalDocument.setPseudoSparqlQuery(tempCuratedDocument.getPseudoSparqlQuery());
		finalDocument.setOutOfScope(tempCuratedDocument.getOutOfScope());
		finalDocument.setLanguageToQuestion(tempCuratedDocument.getLanguageToQuestion());
		finalDocument.setLanguageToKeyword(tempCuratedDocument.getLanguageToKeyword());
		finalDocument.setGoldenAnswer(tempCuratedDocument.getGoldenAnswer());
		finalDocument.setRevision(revision);
		finalDocument.setStartingTimeCuration(startingTime);
		String finishingTime = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		finalDocument.setFinishingTimeCuration(finishingTime);
		finalDocument.setStatus("curated");
		
		//store the final details in UserDatasetCorrection table through update process. This is due to the record has been existed with starting time and number of revision value. 
		//These two values must be set in the beginning of curation process
		document.updateDocument(finalDocument);	
		document.deleteTempDocument(user.getId(), id, datasetVersion);
		
		//get all curation log info from UserLogTemp and store them in UserLog. After that, all curation log in UserLogTemp are removed
		document.getAllCurationLogAndStoreAndDelete(user.getId(), id, datasetVersion, startingTime, finishingTime);	
		
		ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+id+"/"+datasetVersion);
		return mav;
	}
	
	/*
	 * Autosave Document
	 */
	@RequestMapping(value = "/document-list/curate/save", method = RequestMethod.POST)
	public @ResponseBody UserDatasetCorrectionTemp save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		String time = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();		
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));		
		
		int userId=user.getId();
		
		//get all data from the view part
		String datasetVersion = request.getParameter("datasetVersion");
		String id = request.getParameter("id");
		String answerType = request.getParameter("answerType");
		String aggregation = request.getParameter("aggregation");
		if (aggregation == "")
			aggregation="null";
		String onlydbo = request.getParameter("onlydbo");
		if (onlydbo == "")
			onlydbo="null";
		String hybrid = request.getParameter("hybrid");
		if (hybrid == "")
			hybrid="null";
		String sparqlQuery = request.getParameter("sparqlQuery");
		String pseudoSparqlQuery = request.getParameter("pseudoSparqlQuery");
		String outOfScope = request.getParameter("outOfScope");
		if (outOfScope == "")
			outOfScope="null";
		String answerTypeSugg = request.getParameter("answerTypeSugg");
		String aggregationSugg = request.getParameter("aggregationSugg");
		String outOfScopeSugg = request.getParameter("outOfScopeSugg");
		
		String onlyDboSugg=request.getParameter("onlyDboSugg");
		String hybridSugg=request.getParameter("hybridSugg");
		
		//check whether it is the first curation on the question or the revised ones. For the first time curation case, old value are from master dataset. 
		//for the revised one, old values are from the previous revision. 
		UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
		UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, id, datasetVersion);
		UserDatasetCorrectionTemp documentUdc = new UserDatasetCorrectionTemp();
		
		String oldSparql;	
		Set<String> oldGoldenAnswer; 
		String oldAnswerType;
		String oldAggregation;
		String oldHybrid;
		String oldOnlyDbo;
		String oldOutOfScope;
		if (tempObj.getId()!= null) { // any changing of question's field has been done. old values are from temp table			
			oldSparql = tempObj.getSparqlQuery();
			oldAnswerType = tempObj.getAnswerType();
			oldAggregation = tempObj.getAggregation();
			oldHybrid = tempObj.getHybrid();
			oldOnlyDbo = tempObj.getOnlydbo();
			oldOutOfScope = tempObj.getOutOfScope();
			documentUdc.setLanguageToKeyword(tempObj.getLanguageToKeyword());
			documentUdc.setLanguageToQuestion(tempObj.getLanguageToQuestion());
			documentUdc.setGoldenAnswer(tempObj.getGoldenAnswer());				
		}else { //there is no changing of question's field at all. The curation of the question is just started
			//check whether it is the first time of the curation or the revised one
			UserDatasetCorrection docObj = udcDaoObj.getDocumentCurationProcess(userId, id, datasetVersion);
			if (docObj.getId() != null) {
				oldSparql = docObj.getSparqlQuery();
				oldAnswerType = docObj.getAnswerType();
				oldAggregation = docObj.getAggregation();
				oldHybrid = docObj.getHybrid();
				oldOnlyDbo = docObj.getOnlydbo();
				oldOutOfScope = docObj.getOutOfScope();
				documentUdc.setLanguageToKeyword(docObj.getLanguageToKeyword());
				documentUdc.setLanguageToQuestion(docObj.getLanguageToQuestion());
				documentUdc.setGoldenAnswer(docObj.getGoldenAnswer());
			}else {
				//find document from master dataset (original data)	
				DocumentDAO documentDao = new DocumentDAO();
				DatasetModel document = documentDao.getDocument(id, datasetVersion);
				oldAnswerType = document.getAnswerType();
				oldAggregation = String.valueOf(document.getAggregation());			
				oldHybrid = String.valueOf(document.getHybrid());			
				oldOnlyDbo = String.valueOf(document.getOnlydbo());			
				oldOutOfScope = String.valueOf(document.getOutOfScope());			
				documentUdc.setLanguageToKeyword(document.getLanguageToKeyword());
				documentUdc.setLanguageToQuestion(document.getLanguageToQuestion());
				documentUdc.setGoldenAnswer(document.getGoldenAnswer());
			}		
		}		
		//if an old variable is empty, it will be assigned string null. This is to make sure CRUD operation can be done 
		if (oldAnswerType == "") {
			oldAnswerType = "null";
		}
		if (oldAggregation == "")
			oldAggregation="null";
		
		if (oldHybrid == "")
			oldHybrid="null";
		
		if (oldOnlyDbo == "")
			oldOnlyDbo="null";
		
		if (oldOutOfScope == "")
			oldOutOfScope="null";
		
		documentUdc.setAggregation(aggregation);
		documentUdc.setAnswerType(answerType);
		documentUdc.setDatasetVersion(datasetVersion);
		documentUdc.setHybrid(hybrid);
		documentUdc.setId(id);
		documentUdc.setOnlydbo(onlydbo);
		documentUdc.setOutOfScope(outOfScope);
		documentUdc.setPseudoSparqlQuery(pseudoSparqlQuery);
		documentUdc.setSparqlQuery(sparqlQuery);
		documentUdc.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));	
		
		documentUdc.setUserId(userId);	
		documentUdc.setLastRevision(time);
						
		//store the document in temporary table. At first, check whether curation process has been started.		
		if (tempObj.getId()!= null) {
			udcDaoObj.updateTempDocument(documentUdc);
		}else {
			udcDaoObj.addDocumentInTempTable(documentUdc);
		}
		
		//record log of activity	
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
		//checking changes
		//check answer type changes
		if (!answerType.equals(oldAnswerType)) {
			logInfo.put("field", "answerType");
			logInfo.put("originValue", oldAnswerType);
			logInfo.put("fieldValue", answerType);
			logInfo.put("suggestionValue", answerTypeSugg);
		}
		//check outOfScope changes
		if (!outOfScope.equals(oldOutOfScope)) {
			logInfo.put("field", "outOfScope");
			logInfo.put("originValue", oldOutOfScope);
			logInfo.put("fieldValue", outOfScope);
			logInfo.put("suggestionValue", outOfScopeSugg);
		}
		//check aggregation changes
		if (!aggregation.equals(oldAggregation)) {
			logInfo.put("field", "aggregation");
			logInfo.put("originValue", oldAggregation);
			logInfo.put("fieldValue", aggregation);
			logInfo.put("suggestionValue", aggregationSugg);
		}
		//check onlydbo changes
		if (!onlydbo.equals(oldOnlyDbo)) {
			logInfo.put("field", "onlydbo");
			logInfo.put("originValue", oldOnlyDbo);
			logInfo.put("fieldValue", onlydbo);
			logInfo.put("suggestionValue", onlyDboSugg);
		}
		//check hybrid changes
		if (!hybrid.equals(oldHybrid)) {
			logInfo.put("field", "hybrid");
			logInfo.put("originValue", oldHybrid);
			logInfo.put("fieldValue", hybrid);
			logInfo.put("suggestionValue", hybridSugg);
		}		
		userLog.setUserId(userId);		
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLog.setLogDate(time);
		userLogDao.addLogCurate(userLog);		
		return documentUdc;	
	}
	
		
	@RequestMapping(value = "/document-list/document/edit-question/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
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
		
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO(); //define user dataset correction access object
		
		if (udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
			UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, datasetId, datasetVersion); //get document from dataset correction
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
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", datasetId);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("field", "languageToQuestion");
			logInfo.put("originValue", documentCorrection.getLanguageToQuestion());
			logInfo.put("fieldValue", mapLanguageToQuestion);
			logInfo.put("suggestionValue", "");
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curated");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			
			documentCorrection.setLanguageToQuestion(mapLanguageToQuestion);//change value of languageToQuestion
			udcDao.updateDocument(documentCorrection);//update document
			userLogDao.addLogCurate(userLog);//add userLog
		}else {
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel documentItem = documentDao.getDocument(datasetId, datasetVersion);
			
			// build languageToQuestion parameter
			Map<String, String> languageToQuestion = documentItem.getLanguageToQuestion();
			Map<String, String> mapLanguageToQuestion = new HashMap<String, String>();
			for (Map.Entry<String, String> mapEntry : languageToQuestion.entrySet()) {
				if (mapEntry.getKey().equals(id)) {
					mapLanguageToQuestion.put(mapEntry.getKey(), value);
				}else {
					mapLanguageToQuestion.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
			
			// Add document to UserDatasetCorrection
			UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setId(datasetId);
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setAnswerType(documentItem.getAnswerType());
			documentCorrection.setAggregation(String.valueOf(documentItem.getAggregation()));
			documentCorrection.setOnlydbo(String.valueOf(documentItem.getOnlydbo()));
			documentCorrection.setHybrid(String.valueOf(documentItem.getHybrid()));
			documentCorrection.setSparqlQuery(documentItem.getSparqlQuery());
			documentCorrection.setPseudoSparqlQuery(documentItem.getPseudoSparqlQuery());
			documentCorrection.setOutOfScope(String.valueOf(documentItem.getOutOfScope()));
			documentCorrection.setLanguageToKeyword(documentItem.getLanguageToKeyword());
			documentCorrection.setLanguageToQuestion(mapLanguageToQuestion);
			documentCorrection.setGoldenAnswer(documentItem.getGoldenAnswer());
			documentCorrection.setUserId(userId);
			documentCorrection.setRevision(1);			
			udcDao.addDocument(documentCorrection); 
			
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", datasetId);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("field", "languageToQuestion");
			logInfo.put("originValue", documentItem.getLanguageToQuestion());
			logInfo.put("fieldValue", mapLanguageToQuestion);
			logInfo.put("suggestionValue", "");
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curate");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);//add userLog
		}
		
		return "Dataset has been updated";
		
	}
	
	//Edit function of Question keyword 
	@RequestMapping(value = "/document-list/document/edit-keyword/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String editKeyword(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		String id =request.getParameter("id");
		String value =request.getParameter("value");
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO(); //define user dataset correction access object
		
		if (udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
			UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, datasetId, datasetVersion); //get document from dataset correction
			//Build languageToKeyword Parameter
			Map<String, List<String>> languageToKeyword = documentCorrection.getLanguageToKeyword();
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
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", datasetId);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("field", "languageToKeyword");
			logInfo.put("originValue", documentCorrection.getLanguageToQuestion());
			logInfo.put("fieldValue", mapLanguageToKeyword);
			logInfo.put("suggestionValue", "");
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curate");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			
			documentCorrection.setLanguageToKeyword(mapLanguageToKeyword);//change value of languageToKeyword
			udcDao.updateDocument(documentCorrection); //update correction document
			userLogDao.addLogCurate(userLog);//add userLog
			
		}else {
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel documentItem = documentDao.getDocument(datasetId, datasetVersion);
			
			//Build languageToKeyword Parameter
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
			// Add document to UserDatasetCorrection
			UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setId(datasetId);
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setAnswerType(documentItem.getAnswerType());
			documentCorrection.setAggregation(String.valueOf(documentItem.getAggregation()));
			documentCorrection.setOnlydbo(String.valueOf(documentItem.getOnlydbo()));
			documentCorrection.setHybrid(String.valueOf(documentItem.getHybrid()));
			documentCorrection.setSparqlQuery(documentItem.getSparqlQuery());
			documentCorrection.setPseudoSparqlQuery(documentItem.getPseudoSparqlQuery());
			documentCorrection.setOutOfScope(String.valueOf(documentItem.getOutOfScope()));
			documentCorrection.setLanguageToKeyword(mapLanguageToKeyword);
			documentCorrection.setLanguageToQuestion(documentItem.getLanguageToQuestion());
			documentCorrection.setGoldenAnswer(documentItem.getGoldenAnswer());
			documentCorrection.setUserId(userId);
			documentCorrection.setRevision(1);			
			udcDao.addDocument(documentCorrection); 
						
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", datasetId);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("field", "languageToKeyword");
			logInfo.put("originValue", documentItem.getLanguageToKeyword());
			logInfo.put("fieldValue", mapLanguageToKeyword);
			logInfo.put("suggestionValue", "");
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curate");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);//add userLog
		}
		
		return "Dataset has been updated";
	}
	@RequestMapping(value = "/document-list/document/change-answer-from-file/{datasetId}/{datasetVersion}", method = RequestMethod.POST)
	public @ResponseBody String ediAnswerFile(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
		
		return "Dataset has been updated";
	}	
	@RequestMapping(value = "/document-list/done-correction/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDoneCorrection(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (!cookieDao.isValidate(cks)) {
			redirectAttributes.addFlashAttribute("message","Session Expired.");
			ModelAndView mav = new ModelAndView("redirect:/login");
			return mav;
		}
		String noChangeChk = "";
		if(request.getParameterMap().containsKey("noChangeChk")) {
			if (request.getParameter("noChangeChk").equals("on")) {
				noChangeChk = request.getParameter("noChangeChk");
			}
		}
		
		
		if (noChangeChk.equals("on")) {
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel document = documentDao.getDocument(datasetId, datasetVersion);
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setId(datasetId);
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setAnswerType(document.getAnswerType());
			documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
			documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
			documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
			documentCorrection.setSparqlQuery(document.getSparqlQuery());
			documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
			documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
			documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
			documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
			documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
			documentCorrection.setUserId(userId);
			documentCorrection.setRevision(1);
						
			udcDao.addDocument(documentCorrection);
			
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", datasetId);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("field", "all");
			logInfo.put("originValue", "");
			logInfo.put("fieldValue", "");
			logInfo.put("suggestionValue", "");
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("no change needed");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);//add userLog
		}
		ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+datasetId+"/"+datasetVersion);
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO(); //define user dataset correction access object
		
		if (!udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
			mav = new ModelAndView("redirect:/document-list/detail/"+datasetId+"/"+datasetVersion);
		}
		return mav;
	}
	
	//Acccept SPARQL Suggestion
	@RequestMapping(value = "/document-detail-curate/save-sparql-suggestion/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveSparqlSuggestion(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (request.getParameterValues("sparqlValue") != null) {
			String sparqlSuggValue="";
			boolean sparqlSuggStatus = false;
			for (String element: request.getParameterValues("sparqlValue")) {
				if (!(element.contains("is missing"))) {					
					sparqlSuggStatus = true;
					sparqlSuggValue = element;
				}				
			}		
			
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel document = documentDao.getDocument(datasetId, datasetVersion);
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			//check whether the suggestion is to remove the question or use a new sparql suggestion 
			if (sparqlSuggStatus) {				
				String question = document.getLanguageToQuestion().get("en");
				SparqlService ss = new SparqlService();
				Set<String> results = new HashSet();				
				if (udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
					UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, datasetId, datasetVersion);
					String question1 = documentCorrection.getLanguageToQuestion().get("en");
					documentCorrection.setSparqlQuery(sparqlSuggValue);
					//get answer from current endpoint based on new sparql					
					/** Retrieve answer from Virtuoso current endpoint **/
					if (ss.isASKQuery(question1)) {
						String result = ss.getResultAskQuery(sparqlSuggValue);	
						results.add(result);
					}else {				
						results = ss.getQuery(sparqlSuggValue);					
					}
					documentCorrection.setGoldenAnswer(results);					
					udcDao.updateDocument(documentCorrection);
					
					
				}else {
					UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
					documentCorrection.setDatasetVersion(datasetVersion);
					documentCorrection.setId(document.getId());
					documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					
					//get updated on all others question attributes after having new sparql
					// get answer from current endpoint
					if (ss.isASKQuery(question)) {
						String result = ss.getResultAskQuery(sparqlSuggValue);	
						results.add(result);
					}else {				
						results = ss.getQuery(sparqlSuggValue);					
					}			
					
					documentCorrection.setAnswerType(documentDao.answerTypeChecking(results));
					documentCorrection.setAggregation(documentDao.AggregationChecking(sparqlSuggValue));
					documentCorrection.setOnlydbo(documentDao.onlyDboChecking(sparqlSuggValue));
					documentCorrection.setHybrid(documentDao.HybridChecking(sparqlSuggValue));
					documentCorrection.setSparqlQuery(sparqlSuggValue);
					documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
					documentCorrection.setOutOfScope(documentDao.outOfScopeChecking(sparqlSuggValue, question));
					documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
					documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
					documentCorrection.setGoldenAnswer(results);
					documentCorrection.setUserId(userId);
					documentCorrection.setRevision(1);					
					documentCorrection.setStatus("true");
					udcDao.addDocument(documentCorrection);				
				}
				//build logInfo				
				logInfo.put("id", datasetId);
				logInfo.put("datasetVersion", datasetVersion);
				logInfo.put("field", "sparqlQuery");
				logInfo.put("originValue", document.getSparqlQuery());
				logInfo.put("fieldValue", sparqlSuggValue);
				logInfo.put("suggestionValue", sparqlSuggValue);
			
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("curate");
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);//add userLog			
			}else { //remove the question
				UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
				documentCorrection.setDatasetVersion(datasetVersion);
				documentCorrection.setId(document.getId());
				documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				documentCorrection.setAnswerType(document.getAnswerType());
				documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
				documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
				documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
				documentCorrection.setSparqlQuery(document.getSparqlQuery());
				documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
				documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
				documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
				documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
				documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
				documentCorrection.setUserId(userId);
				documentCorrection.setRevision(1);				
				documentCorrection.setStatus("false");
				udcDao.addDocument(documentCorrection);
				
				//build logInfo
				
				logInfo.put("id", document.getId());
				logInfo.put("datasetVersion", datasetVersion);				
				
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("remove");
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);//add UserLogcdcd
				
				/** Setting previous and next record **/
				String nextStatus="";	
				String idNext = documentDao.getNextDocument(document.getId(), datasetVersion);
				String nextDataset = datasetVersion;
				
				// Next part
				if (idNext==null) {
					nextStatus = "disabled=\"disabled\"";
					idNext = document.getId();
					
					//Get Next collection
					String nextCollection = documentDao.getNextCollection(datasetVersion);
					if (nextCollection != null) {
						String nextRecord = documentDao.getNextRecordCollection(nextCollection);
						if (nextRecord != null) {
							idNext = nextRecord;
							nextDataset = nextCollection;
							nextStatus = "";
						}					
					}
				}
				ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+idNext+"/"+nextDataset);
				return mav;
			}			
		}		
					
		ModelAndView mav = new ModelAndView("redirect:/document-list/start-correction/"+datasetId+"/"+datasetVersion);
		return mav;
	}
	
	
	@RequestMapping(value = "/document-detail-curate/save-question-suggestion/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveQuestionSuggestion(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (request.getParameterValues("langId") != null) {
			HashMap<String,String> hmQuestion = new HashMap<String,String>(); //define Map<String, String> languageToQuestion 
			for (String langId : request.getParameterValues("langId")) {
				String str = langId;
		        String [] arrOfStr = str.split(";", 2);
				hmQuestion.put(arrOfStr[0], arrOfStr[1]);
			}
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel document = documentDao.getDocument(datasetId, datasetVersion);
			
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			if (udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
				UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, datasetId, datasetVersion);
				documentCorrection.setLanguageToQuestion(hmQuestion);
				udcDao.updateDocument(documentCorrection); //update UserDatasetCorrection
				//build logInfo
				UserLogDAO userLogDao = new UserLogDAO();
				UserLog userLog = new UserLog();
				BasicDBObject logInfo = new BasicDBObject();
				logInfo.put("id", datasetId);
				logInfo.put("datasetVersion", datasetVersion);
				logInfo.put("field", "languageToQuestion");
				logInfo.put("originValue", documentCorrection.getLanguageToQuestion());
				logInfo.put("fieldValue", hmQuestion);
				logInfo.put("suggestionValue", hmQuestion);
			
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("curate");
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);//add userLog
			}else {
				UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
				documentCorrection.setDatasetVersion(datasetVersion);
				documentCorrection.setId(document.getId());
				documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				documentCorrection.setAnswerType(document.getAnswerType());
				documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
				documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
				documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
				documentCorrection.setSparqlQuery(document.getSparqlQuery());
				documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
				documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
				documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
				documentCorrection.setLanguageToQuestion(hmQuestion);
				documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
				documentCorrection.setUserId(userId);
				documentCorrection.setRevision(1);				
				documentCorrection.setStatus("true");
				udcDao.addDocument(documentCorrection);
				//build logInfo
				UserLogDAO userLogDao = new UserLogDAO();
				UserLog userLog = new UserLog();
				BasicDBObject logInfo = new BasicDBObject();
				logInfo.put("id", datasetId);
				logInfo.put("datasetVersion", datasetVersion);
				logInfo.put("field", "languageToQuestion");
				logInfo.put("originValue", document.getLanguageToQuestion());
				logInfo.put("fieldValue", hmQuestion);
				logInfo.put("suggestionValue", hmQuestion);
			
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("curate");
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);//add userLog
			}
			
		}
		
		ModelAndView mav = new ModelAndView("redirect:/document-list/start-correction/"+datasetId+"/"+datasetVersion);
		return mav;
	}
	@RequestMapping(value = "/document-detail-curate/save-keywords-translations/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveKeywordsTranslations(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (request.getParameterValues("langId") != null) {
			HashMap<String,List<String>> hmKeywords = new HashMap<String,List<String>>(); //define Map<String, List<String>> languageTOKeyword 
			for (String langId : request.getParameterValues("langId")) {
				String str = langId;
		        String [] arrOfStr = str.split(";", 2);
		        String keywords = arrOfStr[1].substring(1, arrOfStr[1].length()-1);
		        String [] arrOfKeywords = keywords.split(",");
		        List<String> listKeywords = new ArrayList<String>();
		        for (String a : arrOfKeywords)
		            listKeywords.add(a);
		        
		        
				hmKeywords.put(arrOfStr[0], listKeywords);
			}
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel document = documentDao.getDocument(datasetId, datasetVersion);
			
			UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
			if (udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
				UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, datasetId, datasetVersion);
				documentCorrection.setLanguageToKeyword(hmKeywords);
				udcDao.updateDocument(documentCorrection); //update UserDatasetCorrection
				//build logInfo
				UserLogDAO userLogDao = new UserLogDAO();
				UserLog userLog = new UserLog();
				BasicDBObject logInfo = new BasicDBObject();
				logInfo.put("id", datasetId);
				logInfo.put("datasetVersion", datasetVersion);
				logInfo.put("field", "languageToKeyword");
				logInfo.put("originValue", documentCorrection.getLanguageToKeyword());
				logInfo.put("fieldValue", hmKeywords);
				logInfo.put("suggestionValue", hmKeywords);
			
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("curate");
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);//add userLog
			}else {
				UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
				documentCorrection.setDatasetVersion(datasetVersion);
				documentCorrection.setId(document.getId());
				documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				documentCorrection.setAnswerType(document.getAnswerType());
				documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
				documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
				documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
				documentCorrection.setSparqlQuery(document.getSparqlQuery());
				documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
				documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
				documentCorrection.setLanguageToKeyword(hmKeywords);
				documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
				documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
				documentCorrection.setUserId(userId);
				documentCorrection.setRevision(1);				
				documentCorrection.setStatus("true");
				udcDao.addDocument(documentCorrection);
				//build logInfo
				UserLogDAO userLogDao = new UserLogDAO();
				UserLog userLog = new UserLog();
				BasicDBObject logInfo = new BasicDBObject();
				logInfo.put("id", datasetId);
				logInfo.put("datasetVersion", datasetVersion);
				logInfo.put("field", "languageToKeyword");
				logInfo.put("originValue", document.getLanguageToKeyword());
				logInfo.put("fieldValue", hmKeywords);
				logInfo.put("suggestionValue", hmKeywords);
			
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("curate");
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);//add userLog
			}
			
		}
		
		ModelAndView mav = new ModelAndView("redirect:/document-list/start-correction/"+datasetId+"/"+datasetVersion);
		return mav;
	}
	
	//Save keywords suggestion
	@RequestMapping(value = "/document-detail-curate/save-keywords-suggestion/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveKeywordsSuggestion(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (request.getParameterValues("keywordTerm") != null) {
			HashMap<String,List<String>> hmKeywords = new HashMap<String,List<String>>(); //define Map<String, List<String>> languageTOKeyword 
			List<String> keywords = new ArrayList<String>();
			for (String keyword : request.getParameterValues("keywordTerm")) {				
		        keywords.add(keyword);
		}
		hmKeywords.put("en", keywords);
			
		DocumentDAO documentDao = new DocumentDAO();
		DatasetModel document = documentDao.getDocument(datasetId, datasetVersion);		
		
		//build log info
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", datasetId);
		logInfo.put("datasetVersion", datasetVersion);
		logInfo.put("field", "languageToKeyword");
		
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		if (udcDao.isDocumentExist(userId, datasetId, datasetVersion)) {
			UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, datasetId, datasetVersion);
			documentCorrection.setLanguageToKeyword(hmKeywords);
			udcDao.updateDocument(documentCorrection); //update UserDatasetCorrection
			
			//build logInfo			
			logInfo.put("originValue", documentCorrection.getLanguageToKeyword());
			
		}else {
			UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setId(document.getId());
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setAnswerType(document.getAnswerType());
			documentCorrection.setAggregation(String.valueOf(document.getAggregation()));
			documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
			documentCorrection.setHybrid(String.valueOf(document.getHybrid()));
			documentCorrection.setSparqlQuery(document.getSparqlQuery());
			documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
			documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
			documentCorrection.setLanguageToKeyword(hmKeywords);
			documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
			documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
			documentCorrection.setUserId(userId);
			documentCorrection.setRevision(1);			
			documentCorrection.setStatus("true");
			udcDao.addDocument(documentCorrection);	
			
			//Build log info
			logInfo.put("originValue", document.getLanguageToKeyword());
		}	
		
		logInfo.put("fieldValue", hmKeywords);
		logInfo.put("suggestionValue", hmKeywords);
	
		userLog.setUserId(userId);
		userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		userLog.setLogType("curate");
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLogDao.addLogCurate(userLog);
		
	}		
	ModelAndView mav = new ModelAndView("redirect:/document-list/start-correction/"+datasetId+"/"+datasetVersion);
	return mav;
	}
}
