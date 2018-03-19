package app.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.dao.DocumentDAO;
import app.model.DatasetModel;
import app.model.DatasetSuggestionModel;
import app.response.QuestionResponse;
import app.sparql.SparqlService;

@Controller
public class DocumentController {
	
	@Autowired
	
	/**
	 * 
	 * Display all filtered document
	 * 
	 * 
	 */
	@RequestMapping(value = "/document-list", method = RequestMethod.GET)
	public ModelAndView showDocumentList(HttpServletRequest request, HttpServletResponse response) {
		DocumentDAO documentDao = new DocumentDAO();
		
		ModelAndView mav = new ModelAndView("document-list");
		mav.addObject("datasets", documentDao.getAllDatasets());
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
	public ModelAndView showDocumentListDetail(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) {
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
	
	/*
	 * Autosave Document
	 */
	@RequestMapping(value = "/document-list/document/save", method = RequestMethod.POST)
	public @ResponseBody DatasetModel save(HttpServletRequest request, HttpServletResponse response) throws Exception {
		DocumentDAO documentDao = new DocumentDAO();
		
		String datasetVersion = request.getParameter("datasetVersion");
		String id = request.getParameter("id");
		String answerType = request.getParameter("answerType");
		String aggregation = request.getParameter("aggregation");
		String onlydbo = request.getParameter("onlydbo");
		String hybrid = request.getParameter("hybrid");
		String sparqlQuery = request.getParameter("sparqlQuery");
		String pseudoSparqlQuery = request.getParameter("pseudoSparqlQuery");
		String outOfScope = request.getParameter("outOfScope");
		
		DatasetModel document = documentDao.getDocument(id, datasetVersion);
		
		document.setDatasetVersion(datasetVersion);
		document.setId(id);
		document.setAnswerType(answerType);
		document.setAggregation(Boolean.parseBoolean(aggregation));
		document.setOnlydbo(Boolean.parseBoolean(onlydbo));
		document.setHybrid(Boolean.parseBoolean(hybrid));
		document.setSparqlQuery(sparqlQuery);
		document.setPseudoSparqlQuery(pseudoSparqlQuery);
		document.setOutOfScope(Boolean.parseBoolean(outOfScope));
		
		documentDao.updateDocument(document, datasetVersion);
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
