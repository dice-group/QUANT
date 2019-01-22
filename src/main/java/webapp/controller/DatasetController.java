package webapp.controller;


import org.apache.jena.rdf.model.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.Repository.QuestionsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.services.DatasetService;
import webapp.services.QuestionsService;
import datahandler.WriteQaldDataset;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@RestController
public class DatasetController {

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    DatasetService datasetService;

    @Autowired
    QuestionsService questionsService;

    Authentication auth;

    {
        auth = SecurityContextHolder.getContext().getAuthentication();
    }

    @RequestMapping(value = "/datasetlist", method = RequestMethod.GET)
    public ModelAndView datasetList() {
        ModelAndView model = new ModelAndView("/datasetlist");
        model.addObject("Datasets", datasetService.getAllDatasets());
        model.addObject("Title", "QUANT- Dataset Übersicht");
        return model;
    }

    @RequestMapping(value = "/questionslist/{id}", method = RequestMethod.GET)
    public ModelAndView questionList(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/questionslist");
        model.addObject("Questions", questionsService.findQuestionsByDatasetQuestion_Id(id));
        model.addObject("DatasetName", datasetService.findDatasetByID(id).getName());
        model.addObject("Title", "QUANT - Dataset Questions");
        return model;
    }

    @RequestMapping(value = "/questionVersionList/{setId}/{id}", method = RequestMethod.GET)
    public ModelAndView questionVersionList(@PathVariable("id") String id, @PathVariable("setId") long setId) {
        ModelAndView model = new ModelAndView("/questionVersionList");
        // model.addObject("Questions", questionsService.findQuestionsByQuestionSetId(id));
        // model.addObject("datasetId", setId);
        model.addObject("Questions", questionsService.findQuestionsByDatasetQuestionIdAndQuestionSetId(setId, id));
        return model;
    }

    @RequestMapping(value = "/anotate/{id}", method = RequestMethod.GET)
    public ModelAndView anotate(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/anotate");
        model.addObject("Question", questionsService.findDistinctById(id));
        model.addObject("GoldenAnswer", questionsService.findDistinctById(id).getAnswerAsString());
        model.addObject("nextQuestion", questionsService.findDistinctById(id).getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(id).getDatasetQuestion().getId())));
        model.addObject("formQuestion", new Questions());
        return model;
    }
/*
    @RequestMapping(value = "/anotate/{id}", method=RequestMethod.POST)
    public ModelAndView newVersion(@RequestParam("nextQuestion")long nextQuestion, @RequestParam("id") long id, @RequestParam("nextVersion") int nextVersion, @RequestParam("answertype") String answertype, @RequestParam("optscope") boolean outOfScope, @RequestParam("optaggregation")boolean aggregation, @RequestParam("optdbpedia")boolean onlydb,
                                   @RequestParam("opthybrid") boolean hybrid, @RequestParam("sparql") String sparqlQuery,
                                   @RequestParam("file_answer") Set<String> answer, @RequestParam("trans_lang") List<String> trans_lang, @RequestParam("trans_question") List<String> trans_question, @RequestParam("trans_keywords") List<String> trans_keywords){
    ModelAndView model=new ModelAndView("/anotate");
        model.addObject("Question", questionsService.findDistinctById(nextQuestion));
        model.addObject("GoldenAnswer", questionsService.findDistinctById(nextQuestion).getAnswerAsString());
        model.addObject("nextQuestion", questionsService.findDistinctById(nextQuestion).getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(nextQuestion).getDatasetQuestion().getId())));
        model.addObject("formQuestion", new Questions());
    System.out.println(sparqlQuery);
    System.out.println(trans_lang);
    System.out.println(trans_keywords);
    System.out.println(trans_question);
    return model;
    }*/

    @RequestMapping(value = "/anotate/{id}", method=RequestMethod.POST)
    public ModelAndView newVersion(@PathVariable("id") long id, @RequestParam("answertype") String answertype, @RequestParam("optscope") boolean outOfScope, @RequestParam("optaggregation")boolean aggregation, @RequestParam("optdbpedia")boolean onlydb,
                                   @RequestParam("opthybrid") boolean hybrid, @RequestParam("sparql") String sparqlQuery,
                                   @RequestParam("file_answer") Set<String> answer, @RequestParam("trans_lang") List<String> trans_lang, @RequestParam("trans_question") List<String> trans_question, @RequestParam("trans_keywords") List<String> trans_keywords) {
    long nextQuestion = questionsService.findDistinctById(id).getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(id).getDatasetQuestion().getId()));
    int version = questionsService.findDistinctById(id).getVersion();

    System.out.println(version);

    // Schleife über "trans_lang" - jedes Element erzeugt neuen Datensatz
    //  Translations translations = new Translations();

    return anotate(nextQuestion);
    }
}


