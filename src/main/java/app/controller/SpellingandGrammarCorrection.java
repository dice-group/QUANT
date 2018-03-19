package app.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;

import org.json.JSONArray;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import app.config.MongoDBManager;
import app.model.Dataset;
import app.model.DatasetList;
import app.model.DatasetModel;
import app.model.DatasetModelShortVersion;
import app.response.BaseResponse;
import app.response.QuestionResponse;

@RestController
@RequestMapping ("/question/attributes")
public class SpellingandGrammarCorrection {
	private static final String SUCCESS_STATUS = "success";
	private static final String ERROR_STATUS = "error";
	private static final int CODE_SUCCESS = 100;
	private static final int AUTH_FAILURE = 102;
	private static Logger LOGGER = Logger.getLogger("InfoLogging");
	
	@RequestMapping(value = "/test", method = RequestMethod.GET)
	 public BaseResponse test() {
		BaseResponse response = new BaseResponse();	 
		// Return success response to the client.
		response.setStatus(SUCCESS_STATUS);
		response.setCode(CODE_SUCCESS);
		return response;
	 }
}
