package webapp.controller;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestMapping;
import webapp.Repository.QuestionsRepository;
import webapp.model.Questions;
import webapp.model.User;
import webapp.services.QuestionsService;
import webapp.services.UserService;


@Controller
public class VersionController {

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    UserService userService;

    @RequestMapping(value = "/questionVersionList/{setId}/{qsId}", method = RequestMethod.GET)
    public ModelAndView questionVersionList(@PathVariable("setId") long setId, @PathVariable("qsId") long qsId) {
        ModelAndView model = new ModelAndView("/questionVersionList");
        model.addObject("Questions", questionsService.findQuestionsByDatasetQuestionIdAndQuestionSetId(setId, qsId));
        model.addObject("Set", setId);
        model.addObject("Id",qsId);

        return model;
    }



    @RequestMapping(value = "/questionVersionList/{setId}/{qsId}", method = RequestMethod.POST)
        public String updateActiveVersion(@PathVariable("setId") long setId, @PathVariable("qsId") long qsId, @RequestParam("wasActive") long wq, @RequestParam("nowActive") long nq) {

        System.out.println("now active ID: " + nq + " was active ID: " + wq);

        try {
            Questions wasActive = questionsService.findDistinctById(wq);
            wasActive.setActiveVersion(false);
            questionsService.saveQuestions(wasActive);

            Questions nowActive = questionsService.findDistinctById(nq);
            nowActive.setActiveVersion(true);
            questionsService.saveQuestions(nowActive);
            System.out.println("Update active version successfully saved to database.");
            return "redirect:/questionVersionList/" + setId + "/" + qsId;
        }
        catch (Exception e) {
        return "Error while saving new active version";
        }
        }
    }
