package webapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import webapp.model.Dataset;
import webapp.services.DatasetService;
import webapp.services.QuestionsService;

@RestController
public class DatasetController {

    @Autowired
    DatasetService datasetService;

    @Autowired
    QuestionsService questionsService;

    Authentication auth;
    {
        auth = SecurityContextHolder.getContext().getAuthentication();
    }

    @RequestMapping(value="/datasetlist",method = RequestMethod.GET)
    public ModelAndView datasetList(){
        ModelAndView model = new ModelAndView("/datasetlist");
        model.addObject("Datasets",datasetService.getAllDatasets());
        model.addObject("Title", "QUANT- Dataset Übersicht");
        return model;
    }
    @RequestMapping(value="/questionslist/{id}",method = RequestMethod.GET)
    public ModelAndView questionList(@PathVariable("id") long id){
        ModelAndView model = new ModelAndView("/questionslist");
        model.addObject("Questions",questionsService.findQuestionsByDatasetQuestion_Id(id));
        model.addObject("DatasetName", datasetService.findDatasetByID(id).getName());
        model.addObject("Title", "QUANT - Dataset Questions");
        return model;
    }

    @RequestMapping(value="/questionVersionList/{id}", method = RequestMethod.GET)
    public ModelAndView questionVersionList(@PathVariable("id") String id){
        ModelAndView model = new ModelAndView("/questionVersionList");

        model.addObject("Questions", questionsService.findQuestionsByQuestionSetId(id));
        return model;
    }

    @RequestMapping(value="/anotate/{id}",method=RequestMethod.GET)
    public ModelAndView anotate(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/anotate");
        model.addObject("Question", questionsService.findDistinctById(id));
        return model;
    }
}
