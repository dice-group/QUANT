/*


package webapp.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.ModelMap;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import webapp.model.Questions;
import webapp.services.DatasetService;
import webapp.services.QuestionsService;

import javax.validation.Valid;

@RestController
public class SaveController {

    @Autowired
    DatasetService datasetService;
    @Autowired
    QuestionsService questionsService;

    Authentication auth;
    {
        auth = SecurityContextHolder.getContext().getAuthentication();
    }

    @RequestMapping(value="/saveAnotation",method = RequestMethod.POST)
    public String submit(@Valid @ModelAttribute("questions") Questions questions,
                         BindingResult result, ModelMap model) {
        if (result.hasErrors()) {
            return "error";
        }
        model.addAttribute("id", questions.getId());

    }

}
*/
