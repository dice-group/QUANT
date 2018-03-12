package app.controller;

import java.util.ArrayList;
import java.util.List;

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

import app.model.Dataset;
import app.response.QuestionResponse;

@Controller
public class ImportController {
	 @Autowired
	  
	  @RequestMapping(value = "/import-dataset-list", method = RequestMethod.GET)
	  public ModelAndView showImportDatasetList(HttpServletRequest request, HttpServletResponse response) {
	    ModelAndView mav = new ModelAndView("import-dataset-list");
	    Dataset datasets = new Dataset();
	    mav.addObject("datasets", datasets.getDatasetVersionLists());
	    
	    return mav;
	  }
	 
	 @RequestMapping(value = "/import-dataset-list/detail/{name}", method = RequestMethod.GET)
	  public ModelAndView showImportDatasetDetail(@PathVariable("name") String name, HttpServletRequest request, HttpServletResponse response) {
		RestTemplate restTemplate = new RestTemplate();
		String qr = restTemplate.getForObject("http://localhost:8080/webqald/import/dataset/question/"+name, String.class);
		JSONArray jsonArr = new JSONArray(qr);
		List<QuestionResponse> qrList = new ArrayList<QuestionResponse>();
		for (int i = 0; i < jsonArr.length(); i++)
        {
            JSONObject jsonObj = jsonArr.getJSONObject(i);
            Gson gson = new GsonBuilder().create();
    		QuestionResponse datasetObj = gson.fromJson(jsonObj.toString(), QuestionResponse.class);
    		
    		QuestionResponse  item = new QuestionResponse();
    		item.setId(datasetObj.getId());
    		item.setLanguageToQuestion(datasetObj.getLanguageToQuestion());
    		item.setLanguageToKeyword(datasetObj.getLanguageToKeyword());
			qrList.add(item);
        }
	    ModelAndView mav = new ModelAndView("import-dataset-detail");
	    mav.addObject("datasets", qrList);
		return mav;
	  }
	  
}
