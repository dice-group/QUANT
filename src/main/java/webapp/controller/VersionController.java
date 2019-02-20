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
import webapp.Repository.TranslationsRepository;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;
import webapp.services.QuestionsService;
import webapp.services.TranslationsService;
import webapp.services.UserService;

import java.util.*;


@Controller
public class VersionController {

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    TranslationsRepository translationsRepository;

    @Autowired
    TranslationsService translationsService;

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
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);

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
            System.out.println("Update: Active version successfully saved to database.");
            return "redirect:/questionVersionList/" + setId + "/" + qsId;
        }
        catch (Exception e) {
            return "Error while saving new active version";
        }
    }

    @RequestMapping(value = "/merge/{setId}/{qsId}", method = RequestMethod.POST)
    public String mergePost(@PathVariable("setId") long setId, @PathVariable("qsId") long qsId,
                                                @RequestParam("query")long queryUserId,
                                                @RequestParam("metadata")long metadataUserId,
                                                @RequestParam("setActive")Optional<Boolean> setActive,
                                                @RequestParam("translation")List<String> translations) {
        String query = questionsService.findDistinctById(queryUserId).getSparqlQuery();
        Questions metaQuestion = questionsService.findDistinctById(queryUserId);
        String answertype  = metaQuestion.getAnswertype();
        boolean aggregation = metaQuestion.isAggregation();
        boolean onlydb = metaQuestion.isOnlydb();
        boolean hybrid = metaQuestion.isHybrid();
        boolean outOfScope = metaQuestion.isOutOfScope();
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        Set answers = new HashSet();
        Boolean pres = setActive.isPresent();
        answers.addAll(metaQuestion.getAnswer());
        Questions mergedQuestionVersion = new Questions(metaQuestion.getDatasetQuestion(), answertype, aggregation, onlydb, hybrid, metaQuestion.isOriginal(), setActive.isPresent(), true, user, questionsRepository.findTop1VersionByQuestionSetIdOrderByVersionDesc(metaQuestion.getQuestionSetId()).getVersion()+1, outOfScope, metaQuestion.getQuestionSetId(), query,answers);
        questionsService.saveQuestions(mergedQuestionVersion);
        for(String translation:translations){
            String[]questionLang=translation.split(":");
            Optional<Translations> t = translationsRepository.findByQidAndLang(questionsService.findDistinctById(Long.parseLong(questionLang[0])),questionLang[1]);
            t.ifPresent(tr ->{
                List keywords =new ArrayList();
                keywords.addAll(tr.getKeywords());
                Translations tran = new Translations(mergedQuestionVersion, questionLang[1], keywords, tr.getQuestionString());
                translationsService.saveTranslations(tran);
            } );
        }
        return "redirect:/questionVersionList/" + setId + "/" + qsId;
    }

    @RequestMapping(value = "/merge/{setId}/{qsId}", method = RequestMethod.GET)
    public ModelAndView merge(@PathVariable("setId") long setId, @PathVariable("qsId") long qsId) {
        ModelAndView model = new ModelAndView("/merge");

        model.addObject("Questions", questionsService.findQuestionsByDatasetQuestionIdAndQuestionSetId(setId, qsId));
        model.addObject("Set", setId);
        model.addObject("Id",qsId);
        Map<String,List<String>>mergingTranslationsMap = questionsService.generateMergingTranslationsMap(setId,qsId);
        model.addObject("MergingTranslationsMap",mergingTranslationsMap);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        return model;
    }




    }
