package app.controller;

/** Basic java lib required**/
import java.util.List;
import java.util.logging.Logger;

import org.springframework.web.bind.annotation.PathVariable;
/** Define lib for spring framework required **/
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import app.response.BaseResponse;
import app.response.QuestionResponse;
import java.util.List;

/** Define lib for load dataset QALD required  **/
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.Dataset;
import org.aksw.qa.commons.load.LoaderController;
import org.aksw.qa.commons.qald.*;
import org.json.JSONArray;
import org.json.JSONObject;


@RestController
@RequestMapping("/import/dataset")

public class ImportRestController {
	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	/**
	 * test
	 * @return
	 */
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	 public BaseResponse test() {
	  BaseResponse response = new BaseResponse();
	 
	   // Return success response to the client.
	   response.setStatus(SUCCESS_STATUS);
	   response.setCode(CODE_SUCCESS);
	   return response;
	 }
	/**
	 * Display All Datasets :
	 * QALD1_Test_dbpedia 
	 * QALD1_Train_dbpedia 
	 * QALD2_Test_dbpedia
	 * QALD2_Train_dbpedia
	 * QALD3_Test_dbpedia
	 * QALD4_Test_Multilingual
	 * QALD4_Train_Multilingual
	 * QALD5_Test_Multilingual
	 * QALD5_Train_Multilingual
	 * QALD6_Test_Multilingual
	 * QALD6_Train_Multilingual
	 * QALD7_Test_Multilingual
	 * QALD7_Train_Multilingual
	 * QALD8_Test_Multilingual
	 * QALD8_Train_Multilingual
	 * @return
	 */
	@RequestMapping(value = "/question/all", method = RequestMethod.GET)
	 public JSONArray QuestionAll() {
		JSONArray arrayObject = new JSONArray();
		JSONObject parentObject = new JSONObject();
		for (Dataset d : Dataset.values()) {
			LOGGER.info("Try to load:" + d.name());
			if (
					d.name().equals("QALD1_Test_dbpedia") || 
					d.name().equals("QALD1_Train_dbpedia") || 
					d.name().equals("QALD2_Test_dbpedia") ||
					d.name().equals("QALD2_Train_dbpedia") ||
					d.name().equals("QALD3_Test_dbpedia") ||
					d.name().equals("QALD4_Test_Multilingual") ||
					d.name().equals("QALD4_Train_Multilingual") ||
					d.name().equals("QALD5_Test_Multilingual") ||
					d.name().equals("QALD5_Train_Multilingual") ||
					d.name().equals("QALD6_Test_Multilingual") ||
					d.name().equals("QALD6_Train_Multilingual") ||
					d.name().equals("QALD7_Test_Multilingual") ||
					d.name().equals("QALD7_Train_Multilingual") ||
					d.name().equals("QALD8_Test_Multilingual") ||
					d.name().equals("QALD8_Train_Multilingual") 
				) 
			{
				try {
					List<IQuestion> questions = LoaderController.load(d);
					LOGGER.info("Dataset succesfully loaded:" + d.name());
					
					JSONArray arrayItem = new JSONArray();
					for (IQuestion q : questions) {
						QuestionResponse item = new QuestionResponse();
						item.setLanguageToQuestion(q.getLanguageToQuestion());
						item.setLanguageToKeyword(q.getLanguageToKeywords());
						arrayItem.put(item);
						LOGGER.info("item :"+ q.toString());
					}
					parentObject.put(d.name(), arrayItem);
					arrayObject.put(parentObject);
					return arrayObject;
				} catch (Exception e) {
					LOGGER.info("Dataset couldn't be loaded:" + d.name());
				}
			}
		}
		return null;
	 }
	@RequestMapping(value = "/question/all2", method = RequestMethod.GET)
	 public JSONArray QuestionAllAlt() {
		JSONArray arrayObject = new JSONArray();
		for (Dataset d : Dataset.values()) {
			LOGGER.info("Try to load:" + d.name());
			if (
					d.name().equals("QALD1_Test_dbpedia") || 
					d.name().equals("QALD1_Train_dbpedia") || 
					d.name().equals("QALD2_Test_dbpedia") ||
					d.name().equals("QALD2_Train_dbpedia") ||
					d.name().equals("QALD3_Test_dbpedia") ||
					d.name().equals("QALD4_Test_Multilingual") ||
					d.name().equals("QALD4_Train_Multilingual") ||
					d.name().equals("QALD5_Test_Multilingual") ||
					d.name().equals("QALD5_Train_Multilingual") ||
					d.name().equals("QALD6_Test_Multilingual") ||
					d.name().equals("QALD6_Train_Multilingual") ||
					d.name().equals("QALD7_Test_Multilingual") ||
					d.name().equals("QALD7_Train_Multilingual") ||
					d.name().equals("QALD8_Test_Multilingual") ||
					d.name().equals("QALD8_Train_Multilingual") 
				) 
			{
				try {
					List<IQuestion> questions = LoaderController.load(d);
					LOGGER.info("Dataset succesfully loaded:" + d.name());
					
					JSONArray arrayItem = new JSONArray();
					for (IQuestion q : questions) {
						QuestionResponse item = new QuestionResponse();
						//item.setDatasetVersion(d.name());
						item.setLanguageToQuestion(q.getLanguageToQuestion());
						item.setLanguageToKeyword(q.getLanguageToKeywords());
						arrayObject.put(item);
						LOGGER.info("item :"+ q.toString());
					}
					
				} catch (Exception e) {
					LOGGER.info("Dataset couldn't be loaded:" + d.name());
				}
				return arrayObject;
			}
		}
		return null;
	 }
	@RequestMapping(value = "/question/{qaldVersion}", method = RequestMethod.GET)
	 public JSONArray QuestionByQaldVersion(@PathVariable("qaldVersion") String qaldVersion) {
		JSONArray arrayObject = new JSONArray();
		for (Dataset d : Dataset.values()) {
			LOGGER.info("Try to load:" + d.name());
			if (
					d.name().equals(qaldVersion)
				) 
			{
				try {
					List<IQuestion> questions = LoaderController.load(d);
					LOGGER.info("Dataset succesfully loaded:" + d.name());
					for (IQuestion q : questions) {
						QuestionResponse item = new QuestionResponse();
						item.setId(q.getId());
						item.setAnswerType(q.getAnswerType());
						item.setAggregation(q.getAggregation());
						item.setOnlydbo(q.getOnlydbo());
						item.setHybrid(q.getHybrid());
						item.setLanguageToQuestion(q.getLanguageToQuestion());
						item.setLanguageToKeyword(q.getLanguageToKeywords());
						item.setSparqlQuery(q.getSparqlQuery());
						item.setPseudoSparqlQuery(q.getPseudoSparqlQuery());
						item.setGoldenAnswer(q.getGoldenAnswers());
						
						arrayObject.put(item);
						LOGGER.info("item :"+ q.toString());
					}
					
					return arrayObject;
				} catch (Exception e) {
					LOGGER.info("Dataset couldn't be loaded:" + d.name());
				}
			}
		}
		return null;
	 }
}
