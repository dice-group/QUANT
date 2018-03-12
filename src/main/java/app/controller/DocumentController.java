package app.controller;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.ModelAndView;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import app.bean.DocumentBean;
import app.dao.DocumentDAO;
import app.model.DatasetModel;
import app.response.QuestionResponse;
import app.sparql.SparqlService;



@Controller
public class DocumentController {
	
	@Autowired
	 
	
	@RequestMapping(value = "/document-list", method = RequestMethod.GET)
	public ModelAndView showDocumentList(HttpServletRequest request, HttpServletResponse response) {
		DocumentDAO documentDao = new DocumentDAO();
		
		ModelAndView mav = new ModelAndView("document-list");
		mav.addObject("datasets", documentDao.filteredDocument());
	    return mav;  
	} 
	@RequestMapping(value = "/document-list/detail/{id}/{datasetVersion}", method = RequestMethod.GET)
	public ModelAndView showDocumentListDetail(@PathVariable("id") String id,@PathVariable("datasetVersion") String datasetVersion, HttpServletRequest request, HttpServletResponse response) {
		DocumentDAO documentDao = new DocumentDAO();
		
		ModelAndView mav = new ModelAndView("document-detail");
		DatasetModel documentItem = documentDao.getDocument(id, datasetVersion);
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
		return mav;  
	}
	
}
