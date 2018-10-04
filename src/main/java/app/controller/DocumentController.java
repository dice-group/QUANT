package app.controller;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.mchange.v2.collection.MapEntry;
import com.mongodb.BasicDBObject;

import app.dao.CookieDAO;
import app.dao.DocumentDAO;
import app.dao.UserDAO;
import app.dao.UserDatasetCorrectionDAO;
import app.dao.UserLogDAO;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.model.User;
import app.model.UserDatasetCorrection;
import app.model.UserDatasetCorrectionTemp;
import app.model.UserLog;
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
		String userName = user.getUsername();
		String role = user.getRole();
		DocumentDAO documentDao = new DocumentDAO();
		
		
		//Check whether there is an unfinishfed curation process
		UserDatasetCorrectionDAO udcDao =  new UserDatasetCorrectionDAO();
		udcDao.checkUnfinishedCuration(userId);
		if (userId == 19) {
			ModelAndView mav = new ModelAndView("ManualCheckingDocument-List");
			mav.addObject("ManualCheckingDatasets", documentDao.getAllDatasetsManualChecking(userId));
			mav.addObject("userId", userId);
			mav.addObject("name", user.getName());
			mav.addObject("role", user.getRole());
			return mav;
		}else {
			ModelAndView mav = new ModelAndView("document-list");
			mav.addObject("datasets", documentDao.getAllDatasets(userId, userName, role));
			mav.addObject("userId", userId);
			mav.addObject("name", user.getName());
			mav.addObject("role", user.getRole());
			return mav;
		}		      
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
		
		String[] qaldName = qaldTrain.split("_");
		// if this project will move to the production server, the path should be changed to src/main/webapp/resources/reports/
		File f = new File("C:\\Users\\riagu\\Documents\\new-repo\\QALDCurator\\src\\main\\webapp\\resources\\reports\\"+qaldName[0]+".json");
		//File f = new File("src/main/webapp/resources/reports/"+user.getName()+".json");
		Boolean fExist = false;
		if(f.exists() && !f.isDirectory()) { 
		    fExist = true;
		}	
		mav.addObject("fExists", fExist);
		mav.addObject("datasetName", qaldName[0]);
		mav.addObject("qaldTrain", qaldTrain);
		mav.addObject("qaldTest", qaldTest);
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
		String[] qaldName = qaldTest.split("_");
		String qaldNameNew = "curated_"+qaldName[0];
		//check whether there is an unfinished curation
		curatedQuestion.checkUnfinishedCuration(userId);
		
		mav.addObject("datasets", curatedQuestion.getAllDatasetsInParticularVersion(userId, qaldTrain, qaldTest));		
		// if this project will move to the production server, the path should be changed to src/main/webapp/resources/reports/
		File f = new File("C:\\Users\\riagu\\Documents\\new-repo\\QALDCurator\\src\\main\\webapp\\resources\\reports\\"+qaldNameNew+".json");
		//File f = new File("src/main/webapp/resources/reports/"+user.getName()+".json");
		Boolean fExist = false;
		if(f.exists() && !f.isDirectory()) { 
		    fExist = true;
		}	
		mav.addObject("fExists", fExist);
		mav.addObject("datasetName", qaldName[0]);
		mav.addObject("qaldTrain", qaldTrain);
		mav.addObject("qaldTest", qaldTest);
		mav.addObject("qaldNameNew", qaldNameNew);
		mav.addObject("status","versionBasedCurated");
		
	    return mav;  
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		//Check whether there is an unfinishfed curation process		
		documentCorrectionDao.checkUnfinishedCuration(user.getId());
		
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start initial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button
		/* end */	
		
		//get dataset for particular user
				String dataset = documentDao.getDatasetName(user.getId());
				String previousStatus = "";
				String nextStatus="";	
				String idNext = documentDao.getNextDocumentByUserId(id, datasetVersion, dataset);
				String idPrevious = documentDao.getPreviousDocumentByUserId(id, datasetVersion, dataset);
				String previousDataset = datasetVersion;
				String nextDataset = datasetVersion;
				
				//	Previous Part	
				if (idPrevious==null) {
					previousStatus = "disabled=\"disabled\"";
					idPrevious = id;
					
					//Get previous collection
					String previousCollection = documentDao.getPreviousCollectionByUserId(datasetVersion, dataset);
					
					if (previousCollection != null) {
						String lastRecord = documentDao.getLastRecordCollectionByUserId(previousCollection, dataset);
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
					String nextCollection = documentDao.getNextCollectionByUserId(datasetVersion, dataset);
					if (nextCollection != null) {
						String nextRecord = documentDao.getNextRecordCollectionByUserId(nextCollection, dataset);
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
		
		String status = "";
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
			mav.addObject("status", "notCurated");
			
			
			/** Provide suggestion **/
			String curatedStatus = "not curated";
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			boolean sparqlCorrectionStatus = documentItemSugg.getSparqlCorrectionStatus();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			
			Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("sparqlCorrectionStatus", sparqlCorrectionStatus);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);			
			//check whether the keywords must be suggested or already exist. Provide menu to get suggestion or do translation
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("listKeywordSuggestion", documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn));		
			}else {		
				if (documentDao.doesNeedKeywordsTranslations(id, datasetVersion, languageToQuestionEn)) {
					
					if (translations != null) {		
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());
					}					
				}								
			}	
			
			if (documentDao.doesNeedQuestionTranslations(id, datasetVersion, languageToQuestionEn)) {
				//provide menu to do translation	
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", translations.getLanguageToQuestion());
				}
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
			status = documentItemCorrection.getStatus();
			int revision = documentItemCorrection.getRevision();
			String removingTime = documentItemCorrection.getRemovingTime();
			Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
			String startingTimeCuration = documentItemCorrection.getStartingTimeCuration();
			String finishingTimeCuration = documentItemCorrection.getFinishingTimeCuration();
			
			
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
			mav.addObject("revision", revision);
			mav.addObject("status", status);
			if (removingTime != null) {
				mav.addObject("removingTime", removingTime);
			}
			
			/** Check whether a curated field value is the same with the suggested one **/
			mav.addObject("isExist", "yes");			
			//mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToKeyword");
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToQuestion"); 
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
						
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
			//check whether the keywords must be suggested or already exist
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				//Check whether keywords suggestion has not been accepted
				if (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
					mav.addObject("addKeywordsSuggestionStatus", true);
					Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
					for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
						List<String> listKeywordSuggestion = mapEntry.getValue();
						mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
					}
				}else { //keywords suggestion have been accepted					
					//check whether suggested keywords have not been translated
					if (!(documentCorrectionDao.haveKeywordsSuggestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);						
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}else {
								//englishKeywordTranslation.put(mapEntry.getKey(), mapEntry.getValue());
								String englishKeywordsList = "";
								for (String element: mapEntry.getValue()) {
									englishKeywordsList = englishKeywordsList + element + ",";
								}
								mav.addObject("englishKeywordTranslation", englishKeywordsList);
							}
								
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);										
					}else {
						//check whether all suggested translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
							mav.addObject("addKeywordsTranslationsStatus", true);
							mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", "keywordsSuggestionsTranslations", startingTimeCuration, finishingTimeCuration));
						}						
					}
				}
			}else {
				//check whether the keywords have not been translated
				if (!(documentCorrectionDao.haveKeywordsBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
					if (translations != null) {
					mav.addObject("addKeywordsTranslationsStatus", true);
					mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());
					}
				}else {
					//check whether all keywords translations have been accepted completely for 11 targeted languages or not
					if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
					}
				}
			}							
					
			//Check whether the question has not been translated either for all or added translations			
			if (!(documentCorrectionDao.hasQuestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
				
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", translations.getLanguageToQuestion());
				}				
			}else {
				//check whether all question translations have been accepted completely for 11 targeted languages or not
				if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "question"))) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", documentCorrectionDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
				}
			}			
		}
		
		//provide real time suggestion (either a field has been curated or not)		
		DocumentDAO dDao = new DocumentDAO();
		DatasetSuggestionModel suggObj = dDao.getSuggestion(user.getId(), id, datasetVersion);		
		mav.addObject("answerTypeSugg", suggObj.getAnswerTypeSugg());
		mav.addObject("aggregationSugg", suggObj.getAggregationSugg());
		mav.addObject("onlyDboSugg", suggObj.getOnlyDboSugg());
		mav.addObject("hybridSugg", suggObj.getHybridSugg());
		mav.addObject("outOfScopeSugg", suggObj.getOutOfScopeSugg());
		return mav;  
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/document-list-manual-checking/detail/{userId}/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDetailManualChecking(@PathVariable("userId") int userId, @PathVariable("id") String id, @PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		//Check whether there is an unfinishfed curation process		
		documentCorrectionDao.checkUnfinishedCuration(user.getId());
		
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentForManualChecking(userId, id, datasetVersion); 
				
		/* Start initial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail-manual-checking");		
		mav.addObject("classDisplay", "btn btn-default");//Show start button
		/* end */	
		
		//get dataset for particular user
				String dataset = documentDao.getDatasetName(user.getId());
				String previousStatus = "";
				String nextStatus="";	
				String idNext = documentDao.getNextDocumentByUserId(id, datasetVersion, dataset);
				String idPrevious = documentDao.getPreviousDocumentByUserId(id, datasetVersion, dataset);
				String previousDataset = datasetVersion;
				String nextDataset = datasetVersion;
				
				//	Previous Part	
				if (idPrevious==null) {
					previousStatus = "disabled=\"disabled\"";
					idPrevious = id;
					
					//Get previous collection
					String previousCollection = documentDao.getPreviousCollectionByUserId(datasetVersion, dataset);
					
					if (previousCollection != null) {
						String lastRecord = documentDao.getLastRecordCollectionByUserId(previousCollection, dataset);
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
					String nextCollection = documentDao.getNextCollectionByUserId(datasetVersion, dataset);
					if (nextCollection != null) {
						String nextRecord = documentDao.getNextRecordCollectionByUserId(nextCollection, dataset);
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
		
		String status = "";
		if (documentItemCorrection.getId() != null) {
			String languageToQuestionEn = documentItemCorrection.getLanguageToQuestion().get("en").toString();
			String sprqlQuery = documentItemCorrection.getSparqlQuery();
			String goldenAnswer = documentItemCorrection.getGoldenAnswer().toString();
			String aggregation = documentItemCorrection.getAggregation();
			String answerType = documentItemCorrection.getAnswerType();
			String onlydbo = documentItemCorrection.getOnlydbo();
			String hybrid = documentItemCorrection.getHybrid();
			String outOfScope = documentItemCorrection.getOutOfScope();
			status = documentItemCorrection.getStatus();
			int revision = documentItemCorrection.getRevision();
			String removingTime = documentItemCorrection.getRemovingTime();
			Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
			String startingTimeCuration = documentItemCorrection.getStartingTimeCuration();
			String finishingTimeCuration = documentItemCorrection.getFinishingTimeCuration();
			
			
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
			mav.addObject("revision", revision);
			mav.addObject("status", status);
			if (removingTime != null) {
				mav.addObject("removingTime", removingTime);
			}
			
			/** Check whether a curated field value is the same with the suggested one **/
			mav.addObject("isExist", "yes");			
			//mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToKeyword");
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToQuestion"); 
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
						
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
			//check whether the keywords must be suggested or already exist
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				//Check whether keywords suggestion has not been accepted
				if (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
					mav.addObject("addKeywordsSuggestionStatus", true);
					Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
					for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
						List<String> listKeywordSuggestion = mapEntry.getValue();
						mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
					}
				}else { //keywords suggestion have been accepted					
					//check whether suggested keywords have not been translated
					if (!(documentCorrectionDao.haveKeywordsSuggestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);						
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}else {
								//englishKeywordTranslation.put(mapEntry.getKey(), mapEntry.getValue());
								String englishKeywordsList = "";
								for (String element: mapEntry.getValue()) {
									englishKeywordsList = englishKeywordsList + element + ",";
								}
								mav.addObject("englishKeywordTranslation", englishKeywordsList);
							}
								
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);										
					}else {
						//check whether all suggested translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
							mav.addObject("addKeywordsTranslationsStatus", true);
							mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", "keywordsSuggestionsTranslations", startingTimeCuration, finishingTimeCuration));
						}						
					}
				}
			}else {
				//check whether the keywords have not been translated
				if (!(documentCorrectionDao.haveKeywordsBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
					if (translations != null) {
					mav.addObject("addKeywordsTranslationsStatus", true);
					mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());
					}
				}else {
					//check whether all keywords translations have been accepted completely for 11 targeted languages or not
					if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
					}
				}
			}							
					
			//Check whether the question has not been translated either for all or added translations			
			if (!(documentCorrectionDao.hasQuestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
				
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", translations.getLanguageToQuestion());
				}				
			}else {
				//check whether all question translations have been accepted completely for 11 targeted languages or not
				if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "question"))) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", documentCorrectionDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
				}
			}
		}
		return mav;
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start intial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button		
		/* end */	
		
		//get dataset for particular user
				String dataset = documentDao.getDatasetName(user.getId());
				String previousStatus = "";
				String nextStatus="";	
				String idNext = documentDao.getNextDocumentByUserId(id, datasetVersion, dataset);
				String idPrevious = documentDao.getPreviousDocumentByUserId(id, datasetVersion, dataset);
				String previousDataset = datasetVersion;
				String nextDataset = datasetVersion;
				
				//	Previous Part	
				if (idPrevious==null) {
					previousStatus = "disabled=\"disabled\"";
					idPrevious = id;
					
					//Get previous collection
					String previousCollection = documentDao.getPreviousCollectionByUserId(datasetVersion, dataset);
					
					if (previousCollection != null) {
						String lastRecord = documentDao.getLastRecordCollectionByUserId(previousCollection, dataset);
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
					String nextCollection = documentDao.getNextCollectionByUserId(datasetVersion, dataset);
					if (nextCollection != null) {
						String nextRecord = documentDao.getNextRecordCollectionByUserId(nextCollection, dataset);
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
		String status = "";
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
			mav.addObject("status", "notCurated");
			
			
			/** Provide suggestion **/
			String curatedStatus = "not curated";
			DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
			boolean sparqlCorrectionStatus = documentItemSugg.getSparqlCorrectionStatus();
			
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			mav.addObject("sparqlCorrectionStatus", sparqlCorrectionStatus);
			
			
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);			
			//check whether the keywords must be suggested or already exist. Provide menu to get suggestion or do translation
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				mav.addObject("listKeywordSuggestion", documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn));		
			}else {		
				if (documentDao.doesNeedKeywordsTranslations(id, datasetVersion, languageToQuestionEn)) {
					if (translations != null) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());
					}					
				}								
			}	
			
			if (documentDao.doesNeedQuestionTranslations(id, datasetVersion, languageToQuestionEn)) {
				//provide menu to do translation	
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", translations.getLanguageToQuestion());
				}
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
			status = documentItemCorrection.getStatus();
			int revision = documentItemCorrection.getRevision();
			String removingTime = documentItemCorrection.getRemovingTime();
			Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
			Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
			String startingTimeCuration = documentItemCorrection.getStartingTimeCuration();
			String finishingTimeCuration = documentItemCorrection.getFinishingTimeCuration();
			
			
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
			mav.addObject("revision", revision);
			mav.addObject("status", status);
			if (removingTime != null) {
				mav.addObject("removingTime", removingTime);
			}
			
			/** Provide suggestion **/
			
			mav.addObject("isExist", "yes");			
			//mav.addObject("resultStatus", resultStatus);
			
			/** Provide notification for curated fields **/
			isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToKeyword");
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToQuestion"); 
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
						
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
			//check whether the keywords must be suggested or already exist
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				//Check whether keywords suggestion has not been accepted
				if (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
					mav.addObject("addKeywordsSuggestionStatus", true);
					Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
					for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
						List<String> listKeywordSuggestion = mapEntry.getValue();
						mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
					}
				}else { //keywords suggestion have been accepted					
					//check whether suggested keywords have not been translated
					if (!(documentCorrectionDao.haveKeywordsSuggestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);						
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}else {
								new HashMap<String, List<String>>();
								//englishKeywordTranslation.put(mapEntry.getKey(), mapEntry.getValue());
								String englishKeywordsList = "";
								for (String element: mapEntry.getValue()) {
									englishKeywordsList = englishKeywordsList + element + ",";
								}
								mav.addObject("englishKeywordTranslation", englishKeywordsList);
							}
								
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);										
					}else {
						//check whether all suggested translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
							mav.addObject("addKeywordsTranslationsStatus", true);
							mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", "keywordsSuggestionsTranslations", startingTimeCuration, finishingTimeCuration));
						}						
					}
				}
			}else {
				//check whether the keywords have not been translated
				if (!(documentCorrectionDao.haveKeywordsBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
					if (translations != null) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());
					}					
				}else {
					//check whether all keywords translations have been accepted completely for 11 targeted languages or not
					if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
					}
				}
			}							
					
			//Check whether the question has not been translated either for all or added translations			
			if (!(documentCorrectionDao.hasQuestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
				
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", translations.getLanguageToQuestion());
				}				
			}else {
				//check whether all question translations have been accepted completely for 11 targeted languages or not
				if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "question"))) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", documentCorrectionDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
				}
			}
		}
		//provide real time suggestion (either a field has been curated or not)		
		DocumentDAO dDao = new DocumentDAO();
		DatasetSuggestionModel suggObj = dDao.getSuggestion(user.getId(), id, datasetVersion);		
		mav.addObject("answerTypeSugg", suggObj.getAnswerTypeSugg());
		mav.addObject("aggregationSugg", suggObj.getAggregationSugg());
		mav.addObject("onlyDboSugg", suggObj.getOnlyDboSugg());
		mav.addObject("hybridSugg", suggObj.getHybridSugg());
		mav.addObject("outOfScopeSugg", suggObj.getOutOfScopeSugg());
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); //get documents correctio
		/* end */
		
		/* Start intial document master */
		DocumentDAO documentDao = new DocumentDAO();
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);//get documents
		mav.addObject("classDisplay", "btn btn-default");//Show start button		
		/* end */
		
		
		
		
		/** Setting previous and next record **/
		
		//get dataset for particular user
		String dataset = documentDao.getDatasetName(user.getId());
		String previousStatus = "";
		String nextStatus="";	
		String idNext = documentDao.getNextDocumentByUserId(id, datasetVersion, dataset);
		String idPrevious = documentDao.getPreviousDocumentByUserId(id, datasetVersion, dataset);
		String previousDataset = datasetVersion;
		String nextDataset = datasetVersion;
		
		//	Previous Part	
		if (idPrevious==null) {
			previousStatus = "disabled=\"disabled\"";
			idPrevious = id;
			
			//Get previous collection
			String previousCollection = documentDao.getPreviousCollectionByUserId(datasetVersion, dataset);
			
			if (previousCollection != null) {
				String lastRecord = documentDao.getLastRecordCollectionByUserId(previousCollection, dataset);
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
			String nextCollection = documentDao.getNextCollectionByUserId(datasetVersion, dataset);
			if (nextCollection != null) {
				String nextRecord = documentDao.getNextRecordCollectionByUserId(nextCollection, dataset);
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
		String status = "";
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
					mav.addObject("status", "notCurated");
					
					
					/** Provide suggestion **/
					String curatedStatus = "not curated";
					DatasetSuggestionModel documentItemSugg = documentDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
					String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
					String aggregationSugg = documentItemSugg.getAggregationSugg();		
					String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
					String hybridSugg = documentItemSugg.getHybridSugg();
					String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
					Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
					Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
					String resultStatus = documentItemSugg.getResultStatus();	
					boolean sparqlCorrectionStatus = documentItemSugg.getSparqlCorrectionStatus();
					
					mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
					mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
					mav.addObject("answerTypeSugg", answerTypeSugg);
					mav.addObject("aggregationSugg", aggregationSugg);
					mav.addObject("onlyDboSugg", onlyDboSugg);
					mav.addObject("hybridSugg", hybridSugg);
					mav.addObject("outOfScopeSugg", outOfScopeSugg);
					mav.addObject("isExist", "yes");
					mav.addObject("resultStatus", resultStatus);
					mav.addObject("sparqlCorrectionStatus", sparqlCorrectionStatus);
					
					DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);			
					//check whether the keywords must be suggested or already exist. Provide menu to get suggestion or do translation
					if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
						mav.addObject("addKeywordsSuggestionStatus", true);
						mav.addObject("listKeywordSuggestion", documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn));		
					}else {		
						if (documentDao.doesNeedKeywordsTranslations(id, datasetVersion, languageToQuestionEn)) {
							
							if (translations!= null) {
								mav.addObject("addKeywordsTranslationsStatus", true);
								mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());							}
							
						}								
					}	
					
					if (documentDao.doesNeedQuestionTranslations(id, datasetVersion, languageToQuestionEn)) {
						//provide menu to do translation	
						if (translations != null) {
							mav.addObject("addQuestionTranslationsStatus", true);
							mav.addObject("questionTranslation", translations.getLanguageToQuestion());
						}
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
					status = documentItemCorrection.getStatus();
					int revision = documentItemCorrection.getRevision();
					String removingTime = documentItemCorrection.getRemovingTime();
					Map<String, List<String>> languageToKeyword = documentItemCorrection.getLanguageToKeyword();
					Map<String, String> languageToQuestion = documentItemCorrection.getLanguageToQuestion();
					String startingTimeCuration = documentItemCorrection.getStartingTimeCuration();
					String finishingTimeCuration = documentItemCorrection.getFinishingTimeCuration();
					
					
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
					mav.addObject("revision", revision);
					mav.addObject("status", status);
					if (removingTime != null) {
						mav.addObject("removingTime", removingTime);
					}
					
					/** Provide suggestion **/
					
					mav.addObject("isExist", "yes");			
					//mav.addObject("resultStatus", resultStatus);
					
					/** Provide notification for curated fields **/
					isSparqlQueryCurated = documentCorrectionDao.isItemCurated(documentItemCorrection.getUserId(), id, datasetVersion, "sprqlQuery");
					isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToKeyword");
					isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItemCorrection.getUserId(), id, datasetVersion, "languageToQuestion"); 
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
								
					DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
					//check whether the keywords must be suggested or already exist
					if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
						//Check whether keywords suggestion has not been accepted
						if (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
							mav.addObject("addKeywordsSuggestionStatus", true);
							Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
							for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
								List<String> listKeywordSuggestion = mapEntry.getValue();
								mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
							}
						}else { //keywords suggestion have been accepted					
							//check whether suggested keywords have not been translated
							if (!(documentCorrectionDao.haveKeywordsSuggestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
								mav.addObject("addKeywordsTranslationsStatus", true);
								Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);						
								//remove english translations from suggestion
								Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
								for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
									if (!(mapEntry.getKey().equals("en"))) {
										keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
									}else {
										//send english translations to view page in a string format
										Map<String, List<String>> englishKeywordTranslation = new HashMap<String, List<String>>();
										//englishKeywordTranslation.put(mapEntry.getKey(), mapEntry.getValue());
										String englishKeywordsList = "";
										for (String element: mapEntry.getValue()) {
											englishKeywordsList = englishKeywordsList + element + ",";
										}
										mav.addObject("englishKeywordTranslation", englishKeywordsList);
									}
										
								}
								mav.addObject("keywordsTranslations", keywordsTranslations);										
							}else {
								//check whether all suggested translations have been accepted completely for 11 targeted languages or not
								if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
									mav.addObject("addKeywordsTranslationsStatus", true);
									mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", "keywordsSuggestionsTranslations", startingTimeCuration, finishingTimeCuration));
								}						
							}
						}
					}else {
						//check whether the keywords have not been translated
						if (!(documentCorrectionDao.haveKeywordsBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
							
							if (translations != null) {
								mav.addObject("addKeywordsTranslationsStatus", true);
								mav.addObject("keywordsTranslations", translations.getLanguageToKeyword());
							}
						}else {
							//check whether all keywords translations have been accepted completely for 11 targeted languages or not
							if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
								mav.addObject("addKeywordsTranslationsStatus", true);
								mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
							}
						}
					}							
							
					//Check whether the question has not been translated either for all or added translations			
					if (!(documentCorrectionDao.hasQuestionBeenTranslated(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, revision))) {
						
						if (translations != null) {
							mav.addObject("addQuestionTranslationsStatus", true);
							mav.addObject("questionTranslation", translations.getLanguageToQuestion());
						}				
					}else {
						//check whether all question translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "question"))) {
							mav.addObject("addQuestionTranslationsStatus", true);
							mav.addObject("questionTranslation", documentCorrectionDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
						}
					}
				}
		//provide real time suggestion (either a field has been curated or not)		
		DocumentDAO dDao = new DocumentDAO();
		DatasetSuggestionModel suggObj = dDao.getSuggestion(user.getId(), id, datasetVersion);		
		mav.addObject("answerTypeSugg", suggObj.getAnswerTypeSugg());
		mav.addObject("aggregationSugg", suggObj.getAggregationSugg());
		mav.addObject("onlyDboSugg", suggObj.getOnlyDboSugg());
		mav.addObject("hybridSugg", suggObj.getHybridSugg());
		mav.addObject("outOfScopeSugg", suggObj.getOutOfScopeSugg());
		return mav;  
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		@SuppressWarnings("unused")
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
			
			
			/** Provide suggestion **//*
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
			mav.addObject("resultStatus", resultStatus);*/
			mav.addObject("isExist", "yes");
			
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);			
		}		
		return mav;  
	}
	
	//Display next question for master dataset
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		@SuppressWarnings("unused")
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
			
			
			/** Provide suggestion **//*
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
			mav.addObject("resultStatus", resultStatus);*/
			mav.addObject("isExist", "yes");
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
			@SuppressWarnings("unused")
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
				
				
				/** Provide suggestion **//*
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
				mav.addObject("resultStatus", resultStatus);*/
				mav.addObject("isExist", "yes");
			}else {
				mav.addObject("isExist", "no");
				mav.addObject("id", id);
				mav.addObject("datasetVersion", datasetVersion);			
			}
			
			return mav;  
		}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
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
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		//Check whether there is an unfinishfed curation process
		documentCorrectionDao.checkUnfinishedCuration(user.getId());
		
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); 
		ModelAndView mav = new ModelAndView("document-detail");
		if (editStatus.equals("yes"))
			mav.addObject("classDisplay", "btn btn-default");//Hide start button
		else
			mav.addObject("classDisplay", "hidden");//Hide start button
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); //get documents
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
			@SuppressWarnings("unused")
			String startingTimeCuration = documentItem.getStartingTimeCuration();
			@SuppressWarnings("unused")
			String finishingTimeCuration = documentItem.getFinishingTimeCuration();
			String removingTime = documentItemCorrection.getRemovingTime();
			String status = documentItemCorrection.getStatus();
			int revision = documentItemCorrection.getRevision();
			
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
			mav.addObject("revision", revision);
			mav.addObject("status", status);
			mav.addObject("curationStatus",true);
			if (removingTime != null) {
				mav.addObject("removingTime", removingTime);
			}
			
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
			mav.addObject("stageStatus","onlyDisplay");
			
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); 
		ModelAndView mav = new ModelAndView("document-detail");
		if (editStatus.equals("yes"))
			mav.addObject("classDisplay", "btn btn-default");//Hide start button
		else
			mav.addObject("classDisplay", "hidden");//Hide start button
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); //get documents
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
			String startingTimeCuration = documentItem.getStartingTimeCuration();
			String finishingTimeCuration = documentItem.getFinishingTimeCuration();
			String removingTime = documentItemCorrection.getRemovingTime();
			String status = documentItemCorrection.getStatus();
			int revision = documentItemCorrection.getRevision();
			
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
			mav.addObject("revision", revision);
			mav.addObject("status", status);
			if (removingTime != null) {
				mav.addObject("removingTime", removingTime);
			}
			
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
			mav.addObject("stageStatus","onlyDisplay");			
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		UserDatasetCorrectionDAO documentCorrectionDao = new UserDatasetCorrectionDAO();
		UserDatasetCorrection documentItemCorrection = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); 
		ModelAndView mav = new ModelAndView("document-detail");
		if (editStatus.equals("yes"))
			mav.addObject("classDisplay", "btn btn-default");//Hide start button
		else
			mav.addObject("classDisplay", "hidden");//Hide start button
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocumentFromAnyStatus(user.getId(), id, datasetVersion); //get documents
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
			@SuppressWarnings("unused")
			String startingTimeCuration = documentItem.getStartingTimeCuration();
			@SuppressWarnings("unused")
			String finishingTimeCuration = documentItem.getFinishingTimeCuration();
			String removingTime = documentItemCorrection.getRemovingTime();
			String status = documentItemCorrection.getStatus();
			int revision = documentItemCorrection.getRevision();
			
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
			mav.addObject("revision", revision);
			mav.addObject("status", status);
			if (removingTime != null) {
				mav.addObject("removingTime", removingTime);
			}
			
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
			mav.addObject("stageStatus","onlyDisplay");
			
		}else {
			mav.addObject("isExist", "no");
			mav.addObject("id", id);
			mav.addObject("datasetVersion", datasetVersion);
			
		}
		return mav;  
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
		mav.addObject("startButton", "Correction Process");
		mav.addObject("startButtonDisabled", "disabled");
		mav.addObject("displayStatus", "");
		
		//Check whether there is an unfinishfed curation process		
		documentCorrectionDao.checkUnfinishedCuration(user.getId());
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion);
		/** Setting previous and next record **/
		int idCurrent = Integer.parseInt(id);
		@SuppressWarnings("unused")
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
		if (documentItem.getId()!=null) { //document has been curated or has set as no need changes
			//record starting time of curation
			//check in what revision the current curated one is. Make sure it is not the cancel curated one
			currentRevision = documentItem.getRevision();			
			if ((documentItem.getStatus().equals("curated")) || (documentItem.getStatus().equals("noNeedChanges"))) 
			{				
				nextRevision = currentRevision+1;
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
			String startingTimeCuration = documentItem.getStartingTimeCuration();
			String finishingTimeCuration = documentItem.getFinishingTimeCuration();
					
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
			
			//Store details of current curated/noNeedChanges question into temporary table, UserDatasetCorrection
			UserDatasetCorrectionTemp udcTemp = new UserDatasetCorrectionTemp();
			udcTemp.setAggregation(aggregation);
			udcTemp.setAnswerType(answerType);
			udcTemp.setDatasetVersion(datasetVersion);
			udcTemp.setGoldenAnswer(results);
			udcTemp.setHybrid(hybrid);
			udcTemp.setId(id);
			udcTemp.setLanguageToKeyword(languageToKeyword);
			udcTemp.setLanguageToQuestion(languageToQuestion);
			udcTemp.setOnlydbo(onlydbo);
			udcTemp.setOutOfScope(outOfScope);
			udcTemp.setPseudoSparqlQuery(documentItem.getPseudoSparqlQuery());
			udcTemp.setSparqlQuery(sprqlQuery);
			udcTemp.setUserId(user.getId());
			documentCorrectionDao.addDocumentInTempTable(udcTemp);
			
			
			/** Provide suggestion **/
			curatedStatus = "curated";
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			//get only the sparql and sparql case
			String sparqlCaseOnly = "";
			String sparqlOnly ="";
			if (sparqlAndCaseSugg != null) {
				for(Map.Entry <String,List<String>> mapEntry : sparqlAndCaseSugg.entrySet()) {
					sparqlOnly = mapEntry.getKey();				
					for (String element : mapEntry.getValue()) {
						sparqlCaseOnly = sparqlCaseOnly + element + ", ";
					}
					sparqlCaseOnly = sparqlCaseOnly.substring(0, sparqlCaseOnly.length() - 1);				
				}
			}
			
			Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("sparqlOnly", sparqlOnly);
			mav.addObject("sparqlCaseOnly", sparqlCaseOnly);			
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
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItem.getUserId(), id, datasetVersion, "languageToKeyword");
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItem.getUserId(), id, datasetVersion, "languageToQuestion"); 
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
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
				
			
			//check whether the keywords must be suggested or already exist
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				//Check whether keywords suggestion has not been accepted
				if (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration,currentRevision))) {
					mav.addObject("addKeywordsSuggestionStatus", true);
					Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
					for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
						List<String> listKeywordSuggestion = mapEntry.getValue();
						mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
					}					
				}else { //keywords suggestion have been accepted 
					//check whether suggested keywords have not been translated
					if (!(documentCorrectionDao.haveKeywordsSuggestionBeenTranslated(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, currentRevision))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}else {
								//send english translations to view page in a string format						
								String englishKeywordsList = "";
								for (String element: mapEntry.getValue()) {
									englishKeywordsList = englishKeywordsList + element + ",";
								}
								mav.addObject("englishKeywordTranslation", englishKeywordsList);
							}								
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);																
					}else {
						//check whether all suggested keywords translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
							mav.addObject("addKeywordsTranslationsStatus", true);
							mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", "keywordsSuggestionsTranslations", startingTimeCuration, finishingTimeCuration));
						}						
					}
				}
			}else {
				//check whether the keywords have not been translated
				if (!(documentCorrectionDao.haveKeywordsBeenTranslated(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, nextRevision))) {
					if (translations != null) {
					mav.addObject("addKeywordsTranslationsStatus", true);
					//remove english translations from suggestion
					Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
					for (Map.Entry<String, List<String>> mapEntry : translations.getLanguageToKeyword().entrySet()) {
						if (!(mapEntry.getKey().equals("en"))) {
							keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
						}						
					}
					mav.addObject("keywordsTranslations", keywordsTranslations);
					}
				}else {
					//check whether all keywords translations have been accepted completely for 11 targeted languages or not
					if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
					}
				}
			}							
					
			//Check whether the question has not been translated either for all or added translations			
			if (!(documentCorrectionDao.hasQuestionBeenTranslated(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, nextRevision))) {
				
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					Map<String, String> questionTranslation = new HashMap<String, String>();
					for (Map.Entry<String, String> mapEntry : translations.getLanguageToQuestion().entrySet()) {
						if (!(mapEntry.getKey().equals("en"))) {
							questionTranslation.put(mapEntry.getKey(), mapEntry.getValue());
						}
					}
					mav.addObject("questionTranslation", questionTranslation);				
				}				
			}else {
				//check whether all question translations have been accepted completely for 11 targeted languages or not
				if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "question"))) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", documentCorrectionDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
				}
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
			Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
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
			
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);			
			
			//check whether the keywords must be suggested or already exist. Provide menu to get suggestion or do translation
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				mav.addObject("addKeywordsSuggestionStatus", true);
				Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn); 
				for (Map.Entry<String, List<String>> mapEntry : suggestedKeywords.entrySet()) {
					List<String> listKeywordSuggestion = mapEntry.getValue();
					mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
				}
						
			}else {			
				if (documentDao.doesNeedKeywordsTranslations(id, datasetVersion, languageToQuestionEn)) {
					
					//remove english translations from suggestion					
					if (translations!= null) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : translations.getLanguageToKeyword().entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);
					}				
				}				
			}							
					
			//check whether the question has already completed with all 11 translations (complete)
			if (documentDao.doesNeedQuestionTranslations(id, datasetVersion, languageToQuestionEn)) {
				//provide menu to do translation	
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					Map<String, String> questionTranslation = new HashMap<String, String>();
					for (Map.Entry<String, String> mapEntry : translations.getLanguageToQuestion().entrySet()) {
						if (!(mapEntry.getKey().equals("en"))) {
							questionTranslation.put(mapEntry.getKey(), mapEntry.getValue());
						}
					}
					mav.addObject("questionTranslation", questionTranslation);
				}
			}						
		}
		//record starting time of curation of current dataset
		documentItem.setId(id);
		documentItem.setDatasetVersion(datasetVersion);
		documentItem.setUserId(user.getId());	
		
		documentItem.setRevision(nextRevision);
		documentItem.setStartingTimeCuration(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		documentItem.setFinishingTimeCuration("0");
		documentCorrectionDao.addDocument(documentItem);
		
		mav.addObject("statusNoChangeChk", statusNoChangeChk);
		mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
		mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
		mav.addObject("isAggregationCurated", isAggregationCurated);
		mav.addObject("isOnlydboCurated", isOnlydboCurated);
		mav.addObject("isHybridCurated", isHybridCurated);	
		mav.addObject("revision", nextRevision);
		return mav;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@RequestMapping(value = "/document-list/start-correction-manual-checking/{userId}/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView StartCorrectionOnManualChecking (@PathVariable("userId") int userId, @PathVariable("id") String id, @PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) throws FileNotFoundException, IOException {
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
		mav.addObject("startButton", "Correction Process");
		mav.addObject("startButtonDisabled", "disabled");
		mav.addObject("displayStatus", "");
		
		//Check whether there is an unfinishfed curation process		
		documentCorrectionDao.checkUnfinishedCuration(user.getId());
		
		UserDatasetCorrection documentItem = documentCorrectionDao.getDocument(user.getId(), id, datasetVersion);
		/** Setting previous and next record **/
		int idCurrent = Integer.parseInt(id);
		@SuppressWarnings("unused")
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
		if (documentItem.getId()!=null) { //document has been curated or has set as no need changes
			//record starting time of curation
			//check in what revision the current curated one is. Make sure it is not the cancel curated one
			currentRevision = documentItem.getRevision();			
			if ((documentItem.getStatus().equals("curated")) || (documentItem.getStatus().equals("noNeedChanges"))) 
			{				
				nextRevision = currentRevision+1;
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
			String startingTimeCuration = documentItem.getStartingTimeCuration();
			String finishingTimeCuration = documentItem.getFinishingTimeCuration();
					
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
			
			//Store details of current curated/noNeedChanges question into temporary table, UserDatasetCorrection
			UserDatasetCorrectionTemp udcTemp = new UserDatasetCorrectionTemp();
			udcTemp.setAggregation(aggregation);
			udcTemp.setAnswerType(answerType);
			udcTemp.setDatasetVersion(datasetVersion);
			udcTemp.setGoldenAnswer(results);
			udcTemp.setHybrid(hybrid);
			udcTemp.setId(id);
			udcTemp.setLanguageToKeyword(languageToKeyword);
			udcTemp.setLanguageToQuestion(languageToQuestion);
			udcTemp.setOnlydbo(onlydbo);
			udcTemp.setOutOfScope(outOfScope);
			udcTemp.setPseudoSparqlQuery(documentItem.getPseudoSparqlQuery());
			udcTemp.setSparqlQuery(sprqlQuery);
			udcTemp.setUserId(user.getId());
			documentCorrectionDao.addDocumentInTempTable(udcTemp);
			
			
			/** Provide suggestion **/
			curatedStatus = "curated";
			DatasetSuggestionModel documentItemSugg = documentCorrectionDao.implementCorrection(id, datasetVersion, curatedStatus, user.getId());
			String answerTypeSugg = documentItemSugg.getAnswerTypeSugg();		
			String aggregationSugg = documentItemSugg.getAggregationSugg();		
			String onlyDboSugg = documentItemSugg.getOnlyDboSugg();
			String hybridSugg = documentItemSugg.getHybridSugg();
			String outOfScopeSugg = documentItemSugg.getOutOfScopeSugg();
			Map<String,List<String>> sparqlAndCaseSugg = documentItemSugg.getSparqlAndCaseList();
			//get only the sparql and sparql case
			String sparqlCaseOnly = "";
			String sparqlOnly ="";
			if (sparqlAndCaseSugg != null) {
				for(Map.Entry <String,List<String>> mapEntry : sparqlAndCaseSugg.entrySet()) {
					sparqlOnly = mapEntry.getKey();				
					for (String element : mapEntry.getValue()) {
						sparqlCaseOnly = sparqlCaseOnly + element + ", ";
					}
					sparqlCaseOnly = sparqlCaseOnly.substring(0, sparqlCaseOnly.length() - 1);				
				}
			}
			
			Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("sparqlOnly", sparqlOnly);
			mav.addObject("sparqlCaseOnly", sparqlCaseOnly);			
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
			isKeywordCurated = documentCorrectionDao.isKeywordCurated(documentItem.getUserId(), id, datasetVersion, "languageToKeyword");
			isQuestionTranslationCurated = documentCorrectionDao.isQuestionCurated(documentItem.getUserId(), id, datasetVersion, "languageToQuestion"); 
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
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
				
			
			//check whether the keywords must be suggested or already exist
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				//Check whether keywords suggestion has not been accepted
				if (!(documentCorrectionDao.haveKeywordsSuggestionBeenAccepted(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration,currentRevision))) {
					mav.addObject("addKeywordsSuggestionStatus", true);
					Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
					for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
						List<String> listKeywordSuggestion = mapEntry.getValue();
						mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
					}					
				}else { //keywords suggestion have been accepted 
					//check whether suggested keywords have not been translated
					if (!(documentCorrectionDao.haveKeywordsSuggestionBeenTranslated(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, currentRevision))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}else {
								//send english translations to view page in a string format						
								String englishKeywordsList = "";
								for (String element: mapEntry.getValue()) {
									englishKeywordsList = englishKeywordsList + element + ",";
								}
								mav.addObject("englishKeywordTranslation", englishKeywordsList);
							}								
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);																
					}else {
						//check whether all suggested keywords translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
							mav.addObject("addKeywordsTranslationsStatus", true);
							mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", "keywordsSuggestionsTranslations", startingTimeCuration, finishingTimeCuration));
						}						
					}
				}
			}else {
				//check whether the keywords have not been translated
				if (!(documentCorrectionDao.haveKeywordsBeenTranslated(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, nextRevision))) {
					if (translations != null) {
					mav.addObject("addKeywordsTranslationsStatus", true);
					//remove english translations from suggestion
					Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
					for (Map.Entry<String, List<String>> mapEntry : translations.getLanguageToKeyword().entrySet()) {
						if (!(mapEntry.getKey().equals("en"))) {
							keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
						}						
					}
					mav.addObject("keywordsTranslations", keywordsTranslations);
					}
				}else {
					//check whether all keywords translations have been accepted completely for 11 targeted languages or not
					if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "keywords"))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", documentCorrectionDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
					}
				}
			}							
					
			//Check whether the question has not been translated either for all or added translations			
			if (!(documentCorrectionDao.hasQuestionBeenTranslated(documentItem.getUserId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, nextRevision))) {
				
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					Map<String, String> questionTranslation = new HashMap<String, String>();
					for (Map.Entry<String, String> mapEntry : translations.getLanguageToQuestion().entrySet()) {
						if (!(mapEntry.getKey().equals("en"))) {
							questionTranslation.put(mapEntry.getKey(), mapEntry.getValue());
						}
					}
					mav.addObject("questionTranslation", questionTranslation);				
				}				
			}else {
				//check whether all question translations have been accepted completely for 11 targeted languages or not
				if (!(documentCorrectionDao.areTranslationsComplete(user.getId(), id, datasetVersion, startingTimeCuration, finishingTimeCuration, "question"))) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", documentCorrectionDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrection", languageToQuestionEn, startingTimeCuration, finishingTimeCuration));
				}
			}	
		}
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
			int revision = documentCorrectionTempDao.getRevisionOfCuration(user.getId(), id, datasetVersion);
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
			mav.addObject("revision", revision);
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
			//get only the sparql and sparql case
			String sparqlCaseOnly = "";
			String sparqlOnly ="";
			if (sparqlAndCaseSugg != null) {
				for(Map.Entry <String,List<String>> mapEntry : sparqlAndCaseSugg.entrySet()) {
					sparqlOnly = mapEntry.getKey();				
					for (String element : mapEntry.getValue()) {
						sparqlCaseOnly = sparqlCaseOnly + element + ", ";
					}
					sparqlCaseOnly = sparqlCaseOnly.substring(0, sparqlCaseOnly.length() - 1);				
				}
			}			
			Set<String> answerFromVirtuosoList = documentItemSugg.getAnswerFromVirtuosoList();
			String resultStatus = documentItemSugg.getResultStatus();	
						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("sparqlOnly", sparqlOnly);
			mav.addObject("sparqlCaseOnly", sparqlCaseOnly);						
			mav.addObject("sparqlAndCaseSugg", sparqlAndCaseSugg);
			mav.addObject("answerFromVirtuosoList", answerFromVirtuosoList);
			mav.addObject("answerTypeSugg", answerTypeSugg);
			mav.addObject("aggregationSugg", aggregationSugg);
			mav.addObject("onlyDboSugg", onlyDboSugg);
			mav.addObject("hybridSugg", hybridSugg);
			mav.addObject("outOfScopeSugg", outOfScopeSugg);
			mav.addObject("isExist", "yes");
			mav.addObject("resultStatus", resultStatus);
			mav.addObject("answerStatus", resultStatus);
			
			/** is Curated ? **/
			isSparqlQueryCurated = documentCorrectionTempDao.isItemCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "sprqlQuery");
			isKeywordCurated = documentCorrectionTempDao.isKeywordCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "languageToKeyword");
			isQuestionTranslationCurated = documentCorrectionTempDao.isQuestionCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "languageToQuestion"); 
			isAnswerTypeCurated = documentCorrectionTempDao.isItemCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "answerType");
			isOutOfScopeCurated = documentCorrectionTempDao.isItemCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "outOfScope");
			isAggregationCurated = documentCorrectionTempDao.isItemCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "aggregation");
			isOnlydboCurated = documentCorrectionTempDao.isItemCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "onlydbo");
			isHybridCurated = documentCorrectionTempDao.isItemCuratedDuringCurationProcess(user.getId(), id, datasetVersion, "hybrid");
			mav.addObject("isSparqlQueryCurated", isSparqlQueryCurated);
			mav.addObject("isKeywordCurated", isKeywordCurated);
			mav.addObject("isQuestionTranslationCurated", isQuestionTranslationCurated);
			mav.addObject("isAnswerTypeCurated", isAnswerTypeCurated);
			mav.addObject("isOutOfScopeCurated", isOutOfScopeCurated);
			mav.addObject("isAggregationCurated", isAggregationCurated);
			mav.addObject("isOnlydboCurated", isOnlydboCurated);
			mav.addObject("isHybridCurated", isHybridCurated);
			
			DocumentDAO documentDao = new DocumentDAO();
			DatasetModel translations= documentDao.getQuestionTranslations(id, datasetVersion, languageToQuestionEn);
			
			//check whether the keywords must be suggested or already exist
			if (documentDao.doesNeedKeywordSuggestions(id, languageToQuestionEn, datasetVersion)) {
				//Check whether keywords suggestion has not been accepted
				if (!(documentCorrectionTempDao.haveKeywordsSuggestionBeenAcceptedDuringCurationProcess(user.getId(), id, datasetVersion, revision))) {
					mav.addObject("addKeywordsSuggestionStatus", true);
					Map<String, List<String>> suggestedKeywords = documentDao.getGeneratedKeywords(id, datasetVersion, languageToQuestionEn);
					for (Map.Entry<String, List<String>> mapEntry: suggestedKeywords.entrySet()) {
						List<String> listKeywordSuggestion = mapEntry.getValue();
						mav.addObject("listKeywordSuggestion", listKeywordSuggestion);
					}
				}else { //keywords suggestion have been accepted					
					//check whether suggested keywords have not been translated
					if (!(documentCorrectionTempDao.haveKeywordsSuggestionBeenTranslatedDuringCurationProcess(user.getId(), id, datasetVersion, revision))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						Map<String, List<String>> suggestedKeywordsTranslations = documentDao.getTranslationsOfSuggestedKeywords(id, datasetVersion, languageToQuestionEn);						
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : suggestedKeywordsTranslations.entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}else {
								//send english translations to view page in a string format
								Map<String, List<String>> englishKeywordTranslation = new HashMap<String, List<String>>();
								//englishKeywordTranslation.put(mapEntry.getKey(), mapEntry.getValue());
								String englishKeywordsList = "";
								for (String element: mapEntry.getValue()) {
									englishKeywordsList = englishKeywordsList + element + ",";
								}
								mav.addObject("englishKeywordTranslation", englishKeywordsList);
							}
								
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);										
					}else {
						//check whether all suggested translations have been accepted completely for 11 targeted languages or not
						if (!(documentCorrectionTempDao.areTranslationsCompleteDuringCurationProcess(user.getId(), id, datasetVersion, "keywords"))) {
							mav.addObject("addKeywordsTranslationsStatus", true);
							mav.addObject("keywordsTranslations", documentCorrectionTempDao.getRestOfKeywordsTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrectionTemp", "keywordsSuggestionsTranslations", "", ""));
						}						
					}
				}
			}else {
				//check whether the keywords have not been translated
				if (!(documentCorrectionTempDao.haveKeywordsBeenTranslatedDuringCurationProcess(user.getId(), id, datasetVersion, revision))) {
					if (translations != null) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						//remove english translations from suggestion
						Map<String, List<String>> keywordsTranslations = new HashMap<String, List<String>>();
						for (Map.Entry<String, List<String>> mapEntry : translations.getLanguageToKeyword().entrySet()) {
							if (!(mapEntry.getKey().equals("en"))) {
								keywordsTranslations.put(mapEntry.getKey(), mapEntry.getValue());
							}						
						}
						mav.addObject("keywordsTranslations", keywordsTranslations);
					}
				}else {
					//check whether all keywords translations have been accepted completely for 11 targeted languages or not
					if (!(documentCorrectionTempDao.areTranslationsCompleteDuringCurationProcess(user.getId(), id, datasetVersion, "keywords"))) {
						mav.addObject("addKeywordsTranslationsStatus", true);
						mav.addObject("keywordsTranslations", documentCorrectionTempDao.getRestOfKeywordsTranslationNotSuggestion(user.getId(), id, datasetVersion, "UserDatasetCorrectionTemp", languageToQuestionEn, "", ""));
					}
				}
			}							
					
			//Check whether the question has not been translated either for all or added translations			
			if (!(documentCorrectionTempDao.hasQuestionBeenTranslatedDuringCurationProcess(user.getId(), id, datasetVersion, revision))) {
				
				if (translations != null) {
					mav.addObject("addQuestionTranslationsStatus", true);
					Map<String, String> suggestedQuestionTranslations = translations.getLanguageToQuestion();						
					//remove english translations from suggestion
					Map<String, String> questionTranslations = new HashMap<String, String>();
					for (Map.Entry<String, String> mapEntry : suggestedQuestionTranslations.entrySet()) {
						if (!(mapEntry.getKey().equals("en"))) {
							questionTranslations.put(mapEntry.getKey(), mapEntry.getValue());
						}							
					}					
					mav.addObject("questionTranslation", questionTranslations);
				}				
			}else {
				//check whether all question translations have been accepted completely for 11 targeted languages or not
				if (!(documentCorrectionTempDao.areTranslationsCompleteDuringCurationProcess(user.getId(), id, datasetVersion, "question"))) {
					mav.addObject("addQuestionTranslationsStatus", true);
					mav.addObject("questionTranslation", documentCorrectionTempDao.getRestOfQuestionTranslation(user.getId(), id, datasetVersion, "UserDatasetCorrectionTemp", languageToQuestionEn, "", ""));
				}
			}			
		}	
		return mav;
	}
	
	@RequestMapping(value = "/document-list/curate/cancel//{id}/{datasetVersion}", method = RequestMethod.GET)
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
		
		//remove question record in UserDatasetCorrection, UserDatasetCorrectionTemp, and UserLogTemp 
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		udcDao.cancelCuration(userId, id, datasetVersion);
		udcDao.deleteTempDocument(userId, id, datasetVersion);
		udcDao.removeAllCurationLogFromUserLogTemp(userId, id, datasetVersion);
				
		
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
		ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+id+"/"+datasetVersion);
		
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
			if ((documentCorrection.getFinishingTimeCuration()!= null) || (documentCorrection.getNoNeedChangesTime()!= null)) { // the document is finished in curation
				//get data from current record and make a new record based on those data except the revision time is increased and the status becomes removed
				UserDatasetCorrection udcDaoObj = new UserDatasetCorrection();
				udcDaoObj.setAggregation(documentCorrection.getAggregation());
				udcDaoObj.setAnswerType(documentCorrection.getAnswerType());
				udcDaoObj.setDatasetVersion(datasetVersion);
				udcDaoObj.setGoldenAnswer(documentCorrection.getGoldenAnswer());
				udcDaoObj.setHybrid(documentCorrection.getHybrid());
				udcDaoObj.setId(id);
				udcDaoObj.setLanguageToKeyword(documentCorrection.getLanguageToKeyword());
				udcDaoObj.setLanguageToQuestion(documentCorrection.getLanguageToQuestion());			
				udcDaoObj.setOnlydbo(documentCorrection.getOnlydbo());
				udcDaoObj.setOutOfScope(documentCorrection.getOutOfScope());
				udcDaoObj.setPseudoSparqlQuery(documentCorrection.getPseudoSparqlQuery());
				udcDaoObj.setRevision(documentCorrection.getRevision()+1);
				udcDaoObj.setSparqlQuery(documentCorrection.getSparqlQuery());
				udcDaoObj.setStatus("removed");
				udcDaoObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				udcDaoObj.setUserId(userId);
				udcDaoObj.setRemovingTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				udcDao.addDocument(udcDaoObj);								
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
				documentCorrection.setRevision(1);
				udcDao.removeDocumentBeforeCurationDone(documentCorrection);
				udcDao.deleteTempDocument(userId, id, datasetVersion); 
				udcDao.removeAllCurationLogFromUserLogTemp(userId, id, datasetVersion);
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
			documentCorrection.setRevision(1);
			documentCorrection.setSparqlQuery(document.getSparqlQuery());
			documentCorrection.setStatus("removed");
			documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			documentCorrection.setUserId(userId);
			documentCorrection.setRemovingTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			udcDao.addDocument(documentCorrection);			
		}		
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
		userLog.setUserId(userId);		
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLog.setLogDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		userLog.setLogType("removed");
		userLogDao.addLogRemove(userLog);
		
		
		
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
		
	//The question does not need any changes
	@RequestMapping (value = "document-list/curate/noNeedChanges/{id}/{datasetVersion}", method = RequestMethod.GET)
	public @ResponseBody ModelAndView noNeedChanges (@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes){		
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
		
		
		//get all question data from master dataset
		DocumentDAO document = new DocumentDAO();
		DatasetModel docObj = document.getDocument(id, datasetVersion);
		
		//store question details in UserDatasetCorrection table through save process. Set time for no need changes activity  
		UserDatasetCorrection udcObj = new UserDatasetCorrection();
		udcObj.setAggregation(String.valueOf(docObj.getAggregation()));
		udcObj.setAnswerType(docObj.getAnswerType());
		udcObj.setDatasetVersion(datasetVersion);
		
		//get answer from endpoint to be used as golden answer value		
		SparqlService ss = new SparqlService();
		Set<String> results = new HashSet();
		/** Retrieve answer from Virtuoso current endpoint **/
		if (ss.isASKQuery(docObj.getLanguageToQuestion().get("en"))) { 
			String result = ss.getResultAskQuery(docObj.getSparqlQuery());	
			results.add(result);
		}else {				
			results = ss.getQuery(docObj.getSparqlQuery());					
		}
		udcObj.setGoldenAnswer(results);		
		udcObj.setHybrid(String.valueOf(docObj.getHybrid()));
		udcObj.setId(id);
		udcObj.setLanguageToKeyword(docObj.getLanguageToKeyword());
		udcObj.setLanguageToQuestion(docObj.getLanguageToQuestion());
		udcObj.setNoNeedChangesTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		udcObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
		udcObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
		udcObj.setRevision(1);
		udcObj.setSparqlQuery(docObj.getSparqlQuery());
		udcObj.setStatus("noNeedChanges");
		udcObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		udcObj.setUserId(user.getId());	
		
		UserDatasetCorrectionDAO udcDao = new UserDatasetCorrectionDAO();
		udcDao.addDocument(udcObj);
		
		//build logInfo
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);		
		userLog.setUserId(user.getId());		
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLog.setLogDate(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
		userLog.setLogType("noNeedChanges");
		userLogDao.addLogRemove(userLog);
		
		
		ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+id+"/"+datasetVersion);
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
		if (tempCuratedDocument.getId() != null) {
			//provide document to be stored in UserDatasetCorrection table
			UserDatasetCorrection finalDocument = new UserDatasetCorrection();
			finalDocument.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			finalDocument.setId(id);
			finalDocument.setUserId(user.getId());
			finalDocument.setDatasetVersion(datasetVersion);
			
			//get answer from endpoint to be used as golden answer value		
			SparqlService ss = new SparqlService();
			Set<String> results = new HashSet();
			/** Retrieve answer from Virtuoso current endpoint **/
			if (ss.isASKQuery(tempCuratedDocument.getLanguageToQuestion().get("en"))) { 
				String result = ss.getResultAskQuery(tempCuratedDocument.getSparqlQuery());	
				results.add(result);
			}else {				
				results = ss.getQuery(tempCuratedDocument.getSparqlQuery());					
			}

			finalDocument.setAnswerType(tempCuratedDocument.getAnswerType());
			finalDocument.setAggregation(tempCuratedDocument.getAggregation());
			finalDocument.setHybrid(tempCuratedDocument.getHybrid());
			finalDocument.setOnlydbo(tempCuratedDocument.getOnlydbo());
			finalDocument.setSparqlQuery(tempCuratedDocument.getSparqlQuery());
			finalDocument.setPseudoSparqlQuery(tempCuratedDocument.getPseudoSparqlQuery());
			finalDocument.setOutOfScope(tempCuratedDocument.getOutOfScope());
			finalDocument.setLanguageToQuestion(tempCuratedDocument.getLanguageToQuestion());
			finalDocument.setLanguageToKeyword(tempCuratedDocument.getLanguageToKeyword());
			finalDocument.setGoldenAnswer(results);
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
			document.getAllCurationLogAndStoreAndDelete(user.getId(), id, datasetVersion, startingTime, finishingTime, revision);	
		}else {
			ModelAndView mav = new ModelAndView("redirect:/document-list/curate/cancel/"+id+"/"+datasetVersion);
			return mav;
		}
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
		String question = request.getParameter("question");
		String datasetVersion = request.getParameter("datasetVersion");
		String id = request.getParameter("id");
		String answerType = request.getParameter("answerType");
		/*if (answerType == "") {
			answerType = "null";
		}*/
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
		//get sparql suggestion
		String sparqlOnly = request.getParameter("sparqlOnly");
		//get sparql suggestion's case
		String sparqlCaseOnly = request.getParameter("sparqlCaseOnly");
		String pseudoSparqlQuery = request.getParameter("pseudoSparqlQuery");
		String outOfScope = request.getParameter("outOfScope");
		if (outOfScope == "")
			outOfScope="null";
		String answerTypeSugg = request.getParameter("answerTypeSugg");
		String aggregationSugg = request.getParameter("aggregationSugg");
		String outOfScopeSugg = request.getParameter("outOfScopeSugg");		
		String onlyDboSugg=request.getParameter("onlyDboSugg");
		String hybridSugg=request.getParameter("hybridSugg");
		String revision = request.getParameter("revision");
		
		//check whether it is the first curation on the question or the revised ones. For the first time curation case, old value are from master dataset. 
		//for the revised one, old values are from the previous revision. 
		UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
		UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, id, datasetVersion);
		UserDatasetCorrectionTemp documentUdc = new UserDatasetCorrectionTemp();
		
		String oldSparql = "";		 
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
				oldSparql = document.getSparqlQuery();
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
		
		//Prepare recording log of activity
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
				
		//check whether there is a change on sparqlQuery. If it is the case, golden answer must be updated based on result returned by the new sparql from Virtuoso
		if (!sparqlQuery.equals(oldSparql)) {
			SparqlService ss = new SparqlService();
			Set<String> results = new HashSet();
			/** Retrieve answer from Virtuoso current endpoint **/
			if (ss.isASKQuery(question)) { 
				String result = ss.getResultAskQuery(sparqlQuery);	
				results.add(result);
			}else {				
				results = ss.getQuery(sparqlQuery);					
			}
			documentUdc.setGoldenAnswer(results);			
		}
						
		//store the document in temporary table. At first, check whether curation process has been started.		
		if (tempObj.getId()!= null) {
			udcDaoObj.updateTempDocument(documentUdc);
		}else {
			udcDaoObj.addDocumentInTempTable(documentUdc);
		}
		
		//record log of activity
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
		//check the changes
		//check answer type changes
		if (!(answerType.equals(oldAnswerType))) {
			logInfo.put("field", "answerType");
			logInfo.put("originValue", oldAnswerType);
			logInfo.put("fieldValue", answerType);
			logInfo.put("suggestionValue", answerTypeSugg);
		}else if (!(outOfScope.equals(oldOutOfScope))) { //check outOfScope changes
			logInfo.put("field", "outOfScope");
			logInfo.put("originValue", oldOutOfScope);
			logInfo.put("fieldValue", outOfScope);
			logInfo.put("suggestionValue", outOfScopeSugg);
		} else if (!(aggregation.equals(oldAggregation))) { //check aggregation changes
			logInfo.put("field", "aggregation");
			logInfo.put("originValue", oldAggregation);
			logInfo.put("fieldValue", aggregation);
			logInfo.put("suggestionValue", aggregationSugg);
		} else if (!(onlydbo.equals(oldOnlyDbo))) { //check onlydbo changes
			logInfo.put("field", "onlydbo");
			logInfo.put("originValue", oldOnlyDbo);
			logInfo.put("fieldValue", onlydbo);
			logInfo.put("suggestionValue", onlyDboSugg);
		} else if (!(hybrid.equals(oldHybrid))) { //check hybrid changes
			logInfo.put("field", "hybrid");
			logInfo.put("originValue", oldHybrid);
			logInfo.put("fieldValue", hybrid);
			logInfo.put("suggestionValue", hybridSugg);
		} else if (!(sparqlQuery.equals(oldSparql))) { //check sparql query changes
			logInfo.put("field", "sparql");
			logInfo.put("originValue", oldSparql);
			logInfo.put("fieldValue", sparqlQuery);
			logInfo.put("suggestionValue", sparqlOnly);
			userLog.setSparqlCases(sparqlCaseOnly);			
		}		
		userLog.setUserId(userId);		
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLog.setLogDate(time);
		userLog.setRevision(Integer.parseInt(revision));
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
		
		// build languageToQuestion parameter		 
		Map<String, String> mapLanguageToQuestion = new HashMap<String, String>();
		Map<String, String> languageToQuestion = new HashMap<String, String>();
		
		//check whether there is a changing done before. It will check UserDatasetCorrectionTemp
		UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
		UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, datasetId, datasetVersion);
		if (tempObj.getId() != null) { //question record is already in UserDatasetCorrectionTemp. It needs to be updated with suggested keywords 
			//Get the new keywords data and do update
			languageToQuestion = tempObj.getLanguageToQuestion();
			for (Map.Entry<String, String> mapEntry : languageToQuestion.entrySet()) {
				if (mapEntry.getKey().equals(id)) {
					mapLanguageToQuestion.put(id, value);
				}else {
					mapLanguageToQuestion.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
			//save the new keywords
			tempObj.setLanguageToQuestion(mapLanguageToQuestion);
			udcDaoObj.updateTempDocument(tempObj);
		}else { // question record does not exist in UserDatasetCorrectionTemp, but the curation process is already running. It needs to create a record that contains data from master dataset except the keywords come from suggestion
			DocumentDAO docDaoObj = new DocumentDAO();
			DatasetModel docObj = docDaoObj.getDocument(datasetId, datasetVersion);
			//Get the new keywords data and do update
			languageToQuestion = docObj.getLanguageToQuestion();
			for (Map.Entry<String, String> mapEntry : languageToQuestion.entrySet()) {
				if (mapEntry.getKey().equals(id)) {
					mapLanguageToQuestion.put(mapEntry.getKey(), value);
				}else {
					mapLanguageToQuestion.put(mapEntry.getKey(), mapEntry.getValue());
				}
			}
			
			tempObj.setAggregation(String.valueOf(docObj.getAggregation()));
			tempObj.setAnswerType(docObj.getAnswerType());
			tempObj.setDatasetVersion(datasetVersion);
			tempObj.setGoldenAnswer(docObj.getGoldenAnswer());
			tempObj.setHybrid(String.valueOf(docObj.getHybrid()));
			tempObj.setId(datasetId);
			tempObj.setLanguageToKeyword(docObj.getLanguageToKeyword());
			tempObj.setLanguageToQuestion(mapLanguageToQuestion);
			tempObj.setLastRevision(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			tempObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
			tempObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
			tempObj.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());
			tempObj.setSparqlQuery(docObj.getSparqlQuery());
			tempObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			tempObj.setUserId(userId);
			udcDaoObj.addDocumentInTempTable(tempObj);			
		}
				
		//record log of activity
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
		logInfo.put("originValue", languageToQuestion);
		logInfo.put("field", "languageToQuestion");	
		logInfo.put("fieldValue", mapLanguageToQuestion);			
		userLog.setUserId(userId);
		userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		userLog.setLogType("curated");
		userLog.setLogTypeQuestion("edit");
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLogDao.addLogCurate(userLog);
		
		//ModelAndView mav = new ModelAndView("redirect:/document-list/curate/curation-process/"+datasetId+"/"+datasetVersion);
		return "Data updated";	
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
		
		//Prepare the new keywords data		
		Map<String, List<String>> mapLanguageToKeyword = new HashMap<String, List<String>>();
		Map<String, List<String>> languageToKeyword = new HashMap<String, List<String>>();
		//check whether there is a changing done before. It will check UserDatasetCorrectionTemp
		UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
		UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, datasetId, datasetVersion);
		if (tempObj.getId() != null) { //question record is already in UserDatasetCorrectionTemp. It needs to be updated with suggested keywords 
			//Get the new keywords data and do update
			languageToKeyword = tempObj.getLanguageToKeyword();
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
			//save the new keywords
			tempObj.setLanguageToKeyword(mapLanguageToKeyword);
			udcDaoObj.updateTempDocument(tempObj);
		}else { // question record does not exist in UserDatasetCorrectionTemp, but the curation process is already running. It needs to create a record that contains data from master dataset except the keywords come from suggestion
			DocumentDAO docDaoObj = new DocumentDAO();
			DatasetModel docObj = docDaoObj.getDocument(datasetId, datasetVersion);
			//Get the new keywords data and do update
			languageToKeyword = docObj.getLanguageToKeyword();
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
			
			tempObj.setAggregation(String.valueOf(docObj.getAggregation()));
			tempObj.setAnswerType(docObj.getAnswerType());
			tempObj.setDatasetVersion(datasetVersion);
			tempObj.setGoldenAnswer(docObj.getGoldenAnswer());
			tempObj.setHybrid(String.valueOf(docObj.getHybrid()));
			tempObj.setId(datasetId);
			tempObj.setLanguageToKeyword(mapLanguageToKeyword);
			tempObj.setLanguageToQuestion(docObj.getLanguageToQuestion());
			tempObj.setLastRevision(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			tempObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
			tempObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
			tempObj.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());
			tempObj.setSparqlQuery(docObj.getSparqlQuery());
			tempObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			tempObj.setUserId(userId);
			udcDaoObj.addDocumentInTempTable(tempObj);			
		}
				
		//record log of activity
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);
		logInfo.put("originValue", languageToKeyword);
		logInfo.put("field", "languageToKeyword");	
		logInfo.put("fieldValue", mapLanguageToKeyword);			
		userLog.setUserId(userId);
		userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		userLog.setLogType("curated");
		userLog.setLogTypeKeyword("edit");
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLogDao.addLogCurate(userLog);
		
		return "Data updated";
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
	@RequestMapping(value = "/document-detail-curate/save-sparql-suggestion/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveSparqlSuggestion(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (request.getParameterValues("sparqlAnswerValue") != null) {
			HashMap<String,String> sparqlAndAnswer = new HashMap<String,String>(); 
			String sparqlSuggValue="";	
			String sparqlCases="";
			boolean sparqlSuggStatus = false;
			for (String theData : request.getParameterValues("sparqlAnswerValue")) {				
		        String [] arrOfStr = theData.split(";", 3);
				sparqlAndAnswer.put(arrOfStr[0], arrOfStr[2]);
				sparqlSuggValue = arrOfStr[0];				
				if (!(sparqlSuggValue.contains("This question should be removed from the dataset"))) {
					sparqlSuggStatus = true;						
					sparqlCases = arrOfStr[1].substring(arrOfStr[1].indexOf("[")+1,arrOfStr[1].indexOf("]"));					
				}	
			}
						
			UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
			UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, id, datasetVersion);
			
			//preparing to get original data
			DocumentDAO docDaoObj = new DocumentDAO();
			DatasetModel docObj = docDaoObj.getDocument(id, datasetVersion);
			
			//check whether the question has a new sparql or has to be removed
			if (sparqlSuggStatus) {
				//preparing get the new answer
				SparqlService ss = new SparqlService();
				Set<String> results = new HashSet();
				//preparing write the log of activity
				UserLogDAO userLogDao = new UserLogDAO();
				UserLog userLog = new UserLog();
				BasicDBObject logInfo = new BasicDBObject();			
				
				//check whether there is a changing done before. It will check UserDatasetCorrectionTemp
				if (tempObj.getId() != null) { //question record is already in UserDatasetCorrectionTemp. It needs to be updated with suggested keywords 
					//write the origin value of sparql in the log
					logInfo.put("originValue", tempObj.getSparqlQuery());
					tempObj.setSparqlQuery(sparqlSuggValue);
					//get the new answer
					if (ss.isASKQuery(tempObj.getLanguageToQuestion().get("en"))) {
						String result = ss.getResultAskQuery(sparqlSuggValue);	
						results.add(result);
					}else {				
						results = ss.getQuery(sparqlSuggValue);					
					}
					tempObj.setGoldenAnswer(results);					
					udcDaoObj.updateTempDocument(tempObj);
					
				}else { // question record does not exist in UserDatasetCorrectionTemp, but the curation process is already running. It needs to create a record that contains data from master dataset					
										
					//get the new answer					
					if (ss.isASKQuery(docObj.getLanguageToQuestion().get("en"))) {
						String result = ss.getResultAskQuery(sparqlSuggValue);	
						results.add(result);
					}else {				
						results = ss.getQuery(sparqlSuggValue);					
					}
					
					//write the origin value of sparql in the log
					logInfo.put("originValue", docObj.getSparqlQuery());
					
					tempObj.setAggregation(String.valueOf(docObj.getAggregation()));
					tempObj.setAnswerType(docObj.getAnswerType());
					tempObj.setDatasetVersion(datasetVersion);
					tempObj.setGoldenAnswer(results);
					tempObj.setHybrid(String.valueOf(docObj.getHybrid()));
					tempObj.setId(id);
					tempObj.setLanguageToKeyword(docObj.getLanguageToKeyword());
					tempObj.setLanguageToQuestion(docObj.getLanguageToQuestion());
					tempObj.setLastRevision(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					tempObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
					tempObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
					tempObj.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());
					tempObj.setSparqlQuery(sparqlSuggValue);
					tempObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
					tempObj.setUserId(userId);
					udcDaoObj.addDocumentInTempTable(tempObj);			
				}						
				//record log of activity
				
				logInfo.put("id", id);
				logInfo.put("datasetVersion", datasetVersion);				
				logInfo.put("field", "sparqlQuery");	
				logInfo.put("fieldValue", sparqlSuggValue);
				logInfo.put("suggestionValue", sparqlSuggValue);
				logInfo.put("sparqlCases", sparqlCases);
				userLog.setUserId(userId);
				userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
				userLog.setLogType("curated");				
				userLog.setIpAddress("");
				userLog.setLogInfo(logInfo);
				userLogDao.addLogCurate(userLog);	
				
				ModelAndView mav = new ModelAndView("redirect:/document-list/curate/curation-process/"+id+"/"+datasetVersion);
				return mav;
			}else {
				UserDatasetCorrection documentCorrection = new UserDatasetCorrection();				
				documentCorrection.setAggregation(String.valueOf(docObj.getAggregation()));
				documentCorrection.setAnswerType(docObj.getAnswerType());
				documentCorrection.setDatasetVersion(datasetVersion);
				documentCorrection.setGoldenAnswer(docObj.getGoldenAnswer());
				documentCorrection.setHybrid(String.valueOf(docObj.getHybrid()));
				documentCorrection.setId(id);
				documentCorrection.setLanguageToKeyword(docObj.getLanguageToKeyword());
				documentCorrection.setLanguageToQuestion(docObj.getLanguageToQuestion());			
				documentCorrection.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
				documentCorrection.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
				documentCorrection.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());				
				documentCorrection.setSparqlQuery(docObj.getSparqlQuery());
				documentCorrection.setSparqlSuggestion(sparqlSuggValue);
				documentCorrection.setStatus("removed");
				documentCorrection.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				documentCorrection.setUserId(userId);
				documentCorrection.setRevision(1);
				documentCorrection.setRemovingTime(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));				
				udcDaoObj.removeDocumentBeforeCurationDone(documentCorrection);
				udcDaoObj.deleteTempDocument(userId, id, datasetVersion); 
				udcDaoObj.removeAllCurationLogFromUserLogTemp(userId, id, datasetVersion);
			}
			ModelAndView mav = new ModelAndView("redirect:/document-list/detail/"+id+"/"+datasetVersion);
			return mav;
		}
		return null;	
	}
	
	
	@RequestMapping(value = "/document-detail-curate/save-questionTranslations-suggestion/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveQuestionSuggestion(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
			//check whether there is a changing done before. It will check UserDatasetCorrectionTemp
			//prepare old value of the question
			Map<String, String> oldQuestionValue = new HashMap<String, String>();
			UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
			UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, id, datasetVersion);
			if (tempObj.getId() != null) { //question record is already in UserDatasetCorrectionTemp. It needs to be updated with suggested keywords 
				oldQuestionValue = tempObj.getLanguageToQuestion();
				//join existing and added keywords translations
				for (Map.Entry<String, String> mapEntry : oldQuestionValue.entrySet()) {
					hmQuestion.put(mapEntry.getKey(), mapEntry.getValue());
				}
				tempObj.setLanguageToQuestion(hmQuestion);
				udcDaoObj.updateTempDocument(tempObj);
			}else { // question record does not exist in UserDatasetCorrectionTemp, but the curation process is already running. It needs to create a record that contains data from master dataset except the keywords come from suggestion
				DocumentDAO docDaoObj = new DocumentDAO();
				DatasetModel docObj = docDaoObj.getDocument(id, datasetVersion);
				oldQuestionValue = docObj.getLanguageToQuestion();
				//join existing and added keywords translations
				for (Map.Entry<String, String> mapEntry : oldQuestionValue.entrySet()) {
					hmQuestion.put(mapEntry.getKey(), mapEntry.getValue());
				}
				tempObj.setAggregation(String.valueOf(docObj.getAggregation()));
				tempObj.setAnswerType(docObj.getAnswerType());
				tempObj.setDatasetVersion(datasetVersion);
				tempObj.setGoldenAnswer(docObj.getGoldenAnswer());
				tempObj.setHybrid(String.valueOf(docObj.getHybrid()));
				tempObj.setId(id);
				tempObj.setLanguageToKeyword(docObj.getLanguageToKeyword());
				tempObj.setLanguageToQuestion(hmQuestion);
				tempObj.setLastRevision(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				tempObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
				tempObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
				tempObj.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());
				tempObj.setSparqlQuery(docObj.getSparqlQuery());
				tempObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				tempObj.setUserId(userId);
				udcDaoObj.addDocumentInTempTable(tempObj);	
				oldQuestionValue = docObj.getLanguageToQuestion();
			}
					
			//record log of activity
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", id);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("originValue", oldQuestionValue);
			logInfo.put("field", "languageToQuestion");	
			logInfo.put("fieldValue", hmQuestion);
			logInfo.put("suggestionValue", hmQuestion);	
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curated");
			userLog.setLogTypeQuestion("translation");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);			
		}	
		ModelAndView mav = new ModelAndView("redirect:/document-list/curate/curation-process/"+id+"/"+datasetVersion);		
		return mav;
	}
	
	@RequestMapping(value = "/document-detail-curate/save-keywords-translations/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveKeywordsTranslations(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
		Cookie[] cks = request.getCookies();
		CookieDAO cookieDao = new CookieDAO();
		
		//retrieve User
		UserDAO userDao = new UserDAO();
		User user = userDao.getUserByUsername(cookieDao.getAuth(cks));
		int userId=user.getId();//assign userId
		
		if (request.getParameterValues("langId") != null) {
			HashMap<String,List<String>> hmKeywords = new HashMap<String,List<String>>(); //define Map<String, List<String>> languageTOKeyword
								
			//get the translations
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
			
			//check whether there is a changing done before. It will check UserDatasetCorrectionTemp
			//prepare old value of the question
			Map<String, List<String>> oldKeywordsValue = new HashMap<String, List<String>>();
			UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
			UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, id, datasetVersion);
			if (tempObj.getId() != null) { //question record is already in UserDatasetCorrectionTemp. It needs to be updated with suggested keywords 
				oldKeywordsValue = tempObj.getLanguageToKeyword();
				//join existing and added keywords translations
				for (Map.Entry<String, List<String>> mapEntry : oldKeywordsValue.entrySet()) {
					hmKeywords.put(mapEntry.getKey(), mapEntry.getValue());
				}
				tempObj.setLanguageToKeyword(hmKeywords);				
				udcDaoObj.updateTempDocument(tempObj);				
			}else { // question record does not exist in UserDatasetCorrectionTemp, but the curation process is already running. It needs to create a record that contains data from master dataset except the keywords come from suggestion
				DocumentDAO docDaoObj = new DocumentDAO();
				DatasetModel docObj = docDaoObj.getDocument(id, datasetVersion);
				oldKeywordsValue = docObj.getLanguageToKeyword();
				//join existing and added keywords translations
				for (Map.Entry<String, List<String>> mapEntry : docObj.getLanguageToKeyword().entrySet()) {
					hmKeywords.put(mapEntry.getKey(), mapEntry.getValue());
				}
				tempObj.setAggregation(String.valueOf(docObj.getAggregation()));
				tempObj.setAnswerType(docObj.getAnswerType());
				tempObj.setDatasetVersion(datasetVersion);
				tempObj.setGoldenAnswer(docObj.getGoldenAnswer());
				tempObj.setHybrid(String.valueOf(docObj.getHybrid()));
				tempObj.setId(id);
				tempObj.setLanguageToKeyword(hmKeywords);
				tempObj.setLanguageToQuestion(docObj.getLanguageToQuestion());
				tempObj.setLastRevision(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				tempObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
				tempObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
				tempObj.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());
				tempObj.setSparqlQuery(docObj.getSparqlQuery());
				tempObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
				tempObj.setUserId(userId);
				udcDaoObj.addDocumentInTempTable(tempObj);			
			}
					
			//record log of activity
			UserLogDAO userLogDao = new UserLogDAO();
			UserLog userLog = new UserLog();
			BasicDBObject logInfo = new BasicDBObject();
			logInfo.put("id", id);
			logInfo.put("datasetVersion", datasetVersion);
			logInfo.put("originValue", oldKeywordsValue);
			logInfo.put("field", "languageToKeyword");	
			logInfo.put("fieldValue", hmKeywords);
			logInfo.put("suggestionValue", hmKeywords);	
			userLog.setUserId(userId);
			userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
			userLog.setLogType("curated");
			userLog.setLogTypeKeyword("translation");
			userLog.setIpAddress("");
			userLog.setLogInfo(logInfo);
			userLogDao.addLogCurate(userLog);
			
		}		
		ModelAndView mav = new ModelAndView("redirect:/document-list/curate/curation-process/"+id+"/"+datasetVersion);
		return mav;	
	}
	
	//Save keywords suggestion
	@RequestMapping(value = "/document-detail-curate/save-keywords-suggestion/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showSaveKeywordsSuggestion(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response, RedirectAttributes redirectAttributes) {
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
		
		//prepare log of activities recording
		UserLogDAO userLogDao = new UserLogDAO();
		UserLog userLog = new UserLog();
		BasicDBObject logInfo = new BasicDBObject();
		
		//check whether there is a changing done before. It will check UserDatasetCorrectionTemp
		UserDatasetCorrectionDAO udcDaoObj = new UserDatasetCorrectionDAO();
		UserDatasetCorrectionTemp tempObj = udcDaoObj.getTempDocument(userId, id, datasetVersion);
		if (tempObj.getId() != null) { //question record is already in UserDatasetCorrectionTemp. It needs to be updated with suggested keywords 
			logInfo.put("originValue", tempObj.getLanguageToKeyword());
			tempObj.setLanguageToKeyword(hmKeywords);
			udcDaoObj.updateTempDocument(tempObj);
		}else { // question record does not exist in UserDatasetCorrectionTemp, but the curation process is already running. It needs to create a record that contains data from master dataset except the keywords come from suggestion
			DocumentDAO docDaoObj = new DocumentDAO();
			DatasetModel docObj = docDaoObj.getDocument(id, datasetVersion);
			logInfo.put("originValue", docObj.getLanguageToKeyword());
			tempObj.setAggregation(String.valueOf(docObj.getAggregation()));
			tempObj.setAnswerType(docObj.getAnswerType());
			tempObj.setDatasetVersion(datasetVersion);
			tempObj.setGoldenAnswer(docObj.getGoldenAnswer());
			tempObj.setHybrid(String.valueOf(docObj.getHybrid()));
			tempObj.setId(id);
			tempObj.setLanguageToKeyword(hmKeywords);
			tempObj.setLanguageToQuestion(docObj.getLanguageToQuestion());
			tempObj.setLastRevision(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			tempObj.setOnlydbo(String.valueOf(docObj.getOnlydbo()));
			tempObj.setOutOfScope(String.valueOf(docObj.getOutOfScope()));
			tempObj.setPseudoSparqlQuery(docObj.getPseudoSparqlQuery());
			tempObj.setSparqlQuery(docObj.getSparqlQuery());
			tempObj.setTransId(new SimpleDateFormat("yyyyMMddHHmmss").format(new Date()));
			tempObj.setUserId(userId);
			udcDaoObj.addDocumentInTempTable(tempObj);			
		}
				
		//record log of activity
		
		logInfo.put("id", id);
		logInfo.put("datasetVersion", datasetVersion);		
		logInfo.put("field", "languageToKeyword");	
		logInfo.put("fieldValue", hmKeywords);
		logInfo.put("suggestionValue", hmKeywords);	
		userLog.setUserId(userId);
		userLog.setLogDate(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
		userLog.setLogType("curated");
		userLog.setLogTypeKeyword("suggestion");
		userLog.setIpAddress("");
		userLog.setLogInfo(logInfo);
		userLogDao.addLogCurate(userLog);
		
	}		
	ModelAndView mav = new ModelAndView("redirect:/document-list/curate/curation-process/"+id+"/"+datasetVersion);
	return mav;
	}
	
}
