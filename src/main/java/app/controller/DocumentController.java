package app.controller;

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

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.time.DateUtils;
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
		
		ModelAndView mav = new ModelAndView("document-list");
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
		
		ModelAndView mav = new ModelAndView("document-list");
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
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			//check whether the question needs translations 			
			mav.addObject("addKeywordsTranslationsStatus", documentDao.doesNeedTranslations(languageToQuestionEn));
			//mav.addObject("addKeywordsTranslationsStatus", true);
			
		}else { 
			//check whether current question has been removed or not
			//if (!documentItemCorrection.getStatus().equals(null)) {
			if (documentItemCorrection.getStatus().equals("false")) {			 
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
			/*DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(user.getId(), id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();
			System.out.println("The SPARQL Suggestion is "+sparqlSugg);
			
			//Check SPARQL correction result. If it is null, then the suggestion will be as to delete the question
			String suggestionValue;
			if (sparqlSugg!=null) {
			if (sparqlSugg.contains("is missing")) {
				suggestionValue = "The Question should be removed from the dataset";
				sparqlSugg.add(suggestionValue);
			}}
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);*/
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
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			//check whether the question needs translations 			
			mav.addObject("addKeywordsTranslationsStatus", documentDao.doesNeedTranslations(languageToQuestionEn));
			//mav.addObject("addKeywordsTranslationsStatus", true);
			
		}else { 
			//check whether current question has been removed or not
			//if (!documentItemCorrection.getStatus().equals(null)) {
			if (documentItemCorrection.getStatus().equals("false")) {			 
				mav = new ModelAndView("redirect:/document-list/detail/"+idPrevious+"/"+previousDataset);				
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
			/*DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(user.getId(), id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();
			System.out.println("The SPARQL Suggestion is "+sparqlSugg);
			
			//Check SPARQL correction result. If it is null, then the suggestion will be as to delete the question
			String suggestionValue;
			if (sparqlSugg!=null) {
			if (sparqlSugg.contains("is missing")) {
				suggestionValue = "The Question should be removed from the dataset";
				sparqlSugg.add(suggestionValue);
			}}
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);*/
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
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			//check whether the question needs translations 			
			mav.addObject("addKeywordsTranslationsStatus", documentDao.doesNeedTranslations(languageToQuestionEn));
			//mav.addObject("addKeywordsTranslationsStatus", true);
			
		}else { 
			//check whether current question has been removed or not
			//if (!documentItemCorrection.getStatus().equals(null)) {
			if (documentItemCorrection.getStatus().equals("false")) {			 
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
			/*DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(user.getId(), id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();
			System.out.println("The SPARQL Suggestion is "+sparqlSugg);
			
			//Check SPARQL correction result. If it is null, then the suggestion will be as to delete the question
			String suggestionValue;
			if (sparqlSugg!=null) {
			if (sparqlSugg.contains("is missing")) {
				suggestionValue = "The Question should be removed from the dataset";
				sparqlSugg.add(suggestionValue);
			}}
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);*/
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
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button		
		
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
		
		
		
		mav.addObject("previousStatus", previousStatus);
		mav.addObject("nextStatus", nextStatus);
		mav.addObject("pageName", "detail");
		mav.addObject("addUrlParameter", "");
		mav.addObject("datasetVersionPrevious", previousDataset);
		mav.addObject("datasetVersionNext", nextDataset);
		mav.addObject("idPrevious", idPrevious);
		mav.addObject("idNext", idNext);
		mav.addObject("isCurated", documentItem.getId());
		
		/** end setting previous and next record **/
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
			/*results.add(null);
			String result = null;*/
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
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();	
			System.out.println("Result Status is "+resultStatus);
			
			mav.addObject("sparqlSugg", sparqlSugg);
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
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(user.getId(), id, datasetVersion);
			/*String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();*/
			String resultStatus = documentItemSugg.getResultStatus();
			/*System.out.println("The SPARQL Suggestion is "+sparqlSugg);
			
			//Check SPARQL correction result. If it is null, then the suggestion will be as to delete the question
			String suggestionValue;
			if (sparqlSugg!=null) {
			if (sparqlSugg.contains("is missing")) {
				suggestionValue = "The Question should be removed from the dataset";
				sparqlSugg.add(suggestionValue);
			}}
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);*/
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
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	@RequestMapping(value = "/document-list/start-correction/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListStartCorrection(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
			
			
			/** Provide suggestion **/
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(user.getId(), id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();
			//System.out.println("The SPARQL Suggestion is "+sparqlSugg);
			
			//Check SPARQL correction result. If it is null, then the suggestion will be as to delete the question
			/*String suggestionValue;
			suggestionValue = "The Question should be removed from the dataset";
			sparqlSugg.add(suggestionValue);*/
			
			/*if (sparqlSugg!=null) {
				suggestionValue = "The Question should be removed from the dataset";
				sparqlSugg.add(suggestionValue);
			if (sparqlSugg.contains("is missing")) {				
				suggestionValue = "The Question should be removed from the dataset";
				sparqlSugg.add(suggestionValue);
			}}*/
			mav.addObject("sparqlSugg", sparqlSugg);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			/** is Curated ? **/
			isAnswerTypeCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionDao.isItemCurated(documentItem.getUserId(), id, datasetVersion, "hybrid");	
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			
			//check whether the question needs new translations
			DocumentDAO documentDao = new DocumentDAO();
			
			mav.addObject("addKeywordsTranslationsStatus", documentDao.doesNeedTranslations(languageToQuestionEn));
			Question questionTranslation= documentDao.getQuestionTranslations(languageToQuestionEn);
			mav.addObject("questionTranslation", questionTranslation.getLanguageToQuestion());
			mav.addObject("keywordsTranslation", questionTranslation.getLanguageToKeyword());
		}else {
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
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion);
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			List<String> sparqlSugg = documentItemSugg.getSparqlSugg();
			String resultStatus = documentItemSugg.getResultStatus();
			//System.out.println("The SPARQL Suggestion is "+sparqlSugg);				
						
			mav.addObject("sparqlSugg", sparqlSugg);			
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			//check whether the question needs new translations
			mav.addObject("addKeywordsTranslationsStatus", documentDao.doesNeedTranslations(languageToQuestionEn));
			Question questionTranslation= documentDao.getQuestionTranslations(languageToQuestionEn);
			mav.addObject("questionTranslation", questionTranslation.getLanguageToQuestion());
			mav.addObject("keywordsTranslation", questionTranslation.getLanguageToKeyword());
		}
		mav.addObject("statusNoChangeChk", statusNoChangeChk);
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);		
		return mav;
	}
	@RequestMapping(value = "/document-list/detail/remove-question/{id}/{datasetVersion}", method = RequestMethod.GET)
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
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		//build logInfo
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
			
		if (udcDao.isDocumentExist(userId, id, datasetVersion)) {
			UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, id, datasetVersion);			
			
			documentCorrection.setStatus("false");
			udcDao.updateDocument(documentCorrection); //update UserDatasetCorrection			
		}else {
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
			documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			documentCorrection.setOnlydbo(String.valueOf(document.getOnlydbo()));
			documentCorrection.setOutOfScope(String.valueOf(document.getOutOfScope()));
			documentCorrection.setPseudoSparqlQuery(document.getPseudoSparqlQuery());
			documentCorrection.setRevision(1);
			documentCorrection.setSparqlQuery(document.getSparqlQuery());
			documentCorrection.setStatus("false");
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setUserId(userId);
			udcDao.addDocument(documentCorrection);			
		}		
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
		String answerTypeSugg = request.getParameter("answerTypeSugg");
		String aggregationSugg = request.getParameter("aggregationSugg");
		String outOfScopeSugg = request.getParameter("outOfScopeSugg");
		String onlyDboSugg=request.getParameter("onlyDboSugg");
		String hybridSugg=request.getParameter("hybridSugg");
		
		//find document
		DatasetModel document = documentDao.getDocument(id, datasetVersion);
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		
		
		if (udcDao.isDocumentExist(userId, id, datasetVersion)) { //Document exist in userdatasetcorrection or update
			UserDatasetCorrection documentCorrection = udcDao.getDocument(userId, id, datasetVersion);
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", id);
			logInfo.put("datasetVersion", datasetVersion);
			//checking changes
			//check answer type changes
			if (!answerType.equals(documentCorrection.getAnswerType())) {
				logInfo.put("field", "answerType");
				logInfo.put("originValue", documentCorrection.getAnswerType());
				logInfo.put("fieldValue", answerType);
				logInfo.put("suggestionValue", answerTypeSugg);
			}
			//check outOfScope changes
			if (!outOfScope.equals(documentCorrection.getOutOfScope())) {
				logInfo.put("field", "outOfScope");
				logInfo.put("originValue", documentCorrection.getOutOfScope());
				logInfo.put("fieldValue", outOfScope);
				logInfo.put("suggestionValue", outOfScopeSugg);
			}
			//check aggregation changes
			if (!aggregation.equals(documentCorrection.getAggregation())) {
				logInfo.put("field", "aggregation");
				logInfo.put("originValue", documentCorrection.getAggregation());
				logInfo.put("fieldValue", aggregation);
				logInfo.put("suggestionValue", aggregationSugg);
			}
			//check onlydbo changes
			if (!onlydbo.equals(documentCorrection.getOnlydbo())) {
				logInfo.put("field", "onlydbo");
				logInfo.put("originValue", documentCorrection.getOnlydbo());
				logInfo.put("fieldValue", onlydbo);
				logInfo.put("suggestionValue", onlyDboSugg);
			}
			//check hybrid changes
			if (!hybrid.equals(documentCorrection.getHybrid())) {
				logInfo.put("field", "hybrid");
				logInfo.put("originValue", documentCorrection.getHybrid());
				logInfo.put("fieldValue", hybrid);
				logInfo.put("suggestionValue", hybridSugg);
			}
			
			documentCorrection.setAnswerType(answerType);
			documentCorrection.setAggregation(aggregation);
			documentCorrection.setOnlydbo(onlydbo);
			documentCorrection.setHybrid(hybrid);
			documentCorrection.setSparqlQuery(sparqlQuery);
			documentCorrection.setPseudoSparqlQuery(pseudoSparqlQuery);
			documentCorrection.setOutOfScope(outOfScope);
			udcDao.updateDocument(documentCorrection); //update UserDatasetCorrection
			
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curate");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);//add UserLogcdcd
			
		}else { //add documentto userdatasetcorrection
			
			UserDatasetCorrection documentCorrection = new UserDatasetCorrection();
			
			//build logInfo
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", id);
			logInfo.put("datasetVersion", datasetVersion);
			//checking changes
			//check answer type changes
			if (!answerType.equals(document.getAnswerType())) {
				logInfo.put("field", "answerType");
				logInfo.put("originValue", document.getAnswerType());
				logInfo.put("fieldValue", answerType);
				logInfo.put("suggestionValue", answerTypeSugg);
			}
			//check outOfScope changes
			if (!outOfScope.equals(String.valueOf(document.getOutOfScope()))) {
				logInfo.put("field", "outOfScope");
				logInfo.put("originValue", document.getOutOfScope());
				logInfo.put("fieldValue", outOfScope);
				logInfo.put("suggestionValue", outOfScopeSugg);
			}
			//check aggregation changes
			if (!aggregation.equals(String.valueOf(document.getAggregation()))) {
				logInfo.put("field", "aggregation");
				logInfo.put("originValue", document.getAggregation());
				logInfo.put("fieldValue", aggregation);
				logInfo.put("suggestionValue", aggregationSugg);
			}
			//check onlydbo changes
			if (!onlydbo.equals(String.valueOf(document.getOnlydbo()))) {
				logInfo.put("field", "onlydbo");
				logInfo.put("originValue", document.getOnlydbo());
				logInfo.put("fieldValue", onlydbo);
				logInfo.put("suggestionValue", onlyDboSugg);
			}
			//check hybrid changes
			if (!hybrid.equals(String.valueOf(document.getHybrid()))) {
				logInfo.put("field", "hybrid");
				logInfo.put("originValue", document.getHybrid());
				logInfo.put("fieldValue", hybrid);
				logInfo.put("suggestionValue", hybridSugg);
			}
			documentCorrection.setDatasetVersion(datasetVersion);
			documentCorrection.setId(id);
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setAnswerType(answerType);
			documentCorrection.setAggregation(aggregation);
			documentCorrection.setOnlydbo(onlydbo);
			documentCorrection.setHybrid(hybrid);
			documentCorrection.setSparqlQuery(sparqlQuery);
			documentCorrection.setPseudoSparqlQuery(pseudoSparqlQuery);
			documentCorrection.setOutOfScope(outOfScope);
			documentCorrection.setLanguageToKeyword(document.getLanguageToKeyword());
			documentCorrection.setLanguageToQuestion(document.getLanguageToQuestion());
			documentCorrection.setGoldenAnswer(document.getGoldenAnswer());
			documentCorrection.setUserId(userId);
			documentCorrection.setRevision(1);
			documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			documentCorrection.setStatus("true");
			udcDao.addDocument(documentCorrection);
			
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curate");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);//add userLog
		}
		
		
		return document;
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
			userLog.setLogType("curate");
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
			documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
	public @ResponseBody String ediKeyword(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) throws Exception {
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
			documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
			documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			
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
	@RequestMapping(value = "/document-list/save-question-suggestion/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
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
				documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
	@RequestMapping(value = "/document-list/save-keywords-suggestion/{datasetId}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveKeywordsSuggestion(@PathVariable("datasetId") String datasetId,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
				documentCorrection.setLastRevision(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
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
}
