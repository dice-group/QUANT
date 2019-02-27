package webapp.controller;

import datahandler.WriteQaldDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.repository.DatasetRepository;
import webapp.repository.QuestionsRepository;
import webapp.repository.TranslationsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;
import webapp.services.DatasetService;
import webapp.services.QuestionsService;
import webapp.services.TranslationsService;
import webapp.services.UserService;
import java.util.Arrays;
import java.util.List;
import java.util.Set;


@Controller
public class CrudController {

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    DatasetService datasetService;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    TranslationsService translationsService;

    @Autowired
    TranslationsRepository translationsRepository;

    @Autowired
    UserService userService;

    @Autowired
    WriteQaldDataset w;

    @RequestMapping(value="/newQuestion/{datasetId}" , method =RequestMethod.GET)
    public ModelAndView newQuestion(@PathVariable("datasetId") long datasetId) {
        ModelAndView model =new ModelAndView("newQuestion");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        model.addObject("Dataset", datasetService.findDatasetByID(datasetId));
        return model;
    }

    @RequestMapping(value="/newQuestion/{datasetId}" , method =RequestMethod.POST)
    public String saveNewQuestion(@PathVariable("datasetId") long datasetId,
                                  @RequestParam("user") User user,
                                  @RequestParam("answertype") String answertype,
                                  @RequestParam("optscope") boolean outOfScope,
                                  @RequestParam("optaggregation") boolean aggregation,
                                  @RequestParam("optdbpedia") boolean onlydb,
                                  @RequestParam("opthybrid") boolean hybrid,
                                  @RequestParam("sparql") String sparqlQuery,
                                  @RequestParam("file_answer") Set<String> answer,
                                  @RequestParam("trans_lang") List<String> trans_lang,
                                  @RequestParam("trans_question") List<String> trans_question,
                                  @RequestParam("trans_keywords") List<String> trans_keywords,

                                  RedirectAttributes attributes)
    {
        Dataset dataset = datasetService.findDatasetByID(datasetId);
        long questionSetId = 0;
        boolean original = false;
        int version = 0;
        boolean anotated = true;
        boolean activeVersion = true;

        try {
            // save Question in neuer Version
            Questions newQuestion = new Questions(dataset, answertype, aggregation, onlydb, hybrid, original, activeVersion, anotated, user, version, outOfScope, questionSetId, sparqlQuery, answer);
            questionsService.saveQuestions(newQuestion);
            newQuestion.setQuestionSetId(newQuestion.getId());
            questionsService.saveQuestions(newQuestion);

            // dann Schleife über "trans_lang" - jedes Element erzeugt neuen Datensatz
            for (int i = 0; i < trans_lang.size(); i++) {
                List<String> keywords = null;
                if(!trans_keywords.isEmpty()) {
                    if(!trans_keywords.get(i).isEmpty())
                    {
                        if  (trans_lang.size()>1)
                        {
                            keywords = Arrays.asList(trans_keywords.get(i).split(",\\s?"));
                        }
                        else
                        {
                            keywords = trans_keywords;
                        }
                    }
                    if (!"".equals(trans_lang.get(i)) && !"".equals(trans_question.get(i))) {
                        Translations translations = new Translations(newQuestion, trans_lang.get(i), keywords, trans_question.get(i));
                        translationsService.saveTranslations(translations);
                    }
                }
                else {
                    Translations translations = new Translations(newQuestion, trans_lang.get(i), trans_question.get(i));
                    translationsService.saveTranslations(translations);

            }

            }

            attributes.addFlashAttribute("success", "Question has been saved successfully.");
            return "redirect:/newQuestion/" + datasetId;
        }

        catch (Exception e) {
            attributes.addFlashAttribute("error", "An error occured while saving the question.");
            return "redirect:/newQuestion/" + datasetId;
        }


    }

    @RequestMapping(value = "/newDataset", method = RequestMethod.GET)
    public ModelAndView newDataset() {
        ModelAndView model = new ModelAndView("/newDataset");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        return model;
    }


    @RequestMapping(value = "/newDataset", method = RequestMethod.POST)
    public String newDataset(@RequestParam("file") MultipartFile file,
                             @RequestParam("endpoint") String endpoint,
                             @RequestParam("defaultLanguage") String defaultLanguage,
                             @RequestParam("datasetName") String datasetName,
                             RedirectAttributes attributes) {
        ModelAndView model = new ModelAndView("/newDataset");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);

        try {

            if (!file.isEmpty()) {

                w.datsetWriter(user, file, endpoint, defaultLanguage);
                return "redirect:/datasetlist";
            } else {
                w.emptyDatasetWriter(user, datasetName, endpoint, defaultLanguage);
                return "redirect:/datasetlist";

            }
        } catch (Exception e) {
            return "redirect:/newDataset";
        }

    }


    @RequestMapping(value = "/deleteDataset", method= RequestMethod.POST)
    public String deleteDataset(@RequestParam("datasetId") long datasetId,
                                RedirectAttributes attributes)
    {

        try{
            Dataset dataset = datasetService.findDatasetByID(datasetId);

            List<Questions> questionList = questionsService.findAllQuestionsByDatasetQuestion_Id(datasetId);
            for (Questions q : questionList)
            {
                questionsRepository.delete(q);

                List<Translations> translationList = translationsRepository.findByQid(q);
                for (Translations t : translationList) {
                    translationsRepository.delete(t);
                }

            }
            datasetRepository.delete(dataset);

            attributes.addFlashAttribute("success", "Dataset has been deleted successfully!");
            return "redirect:/datasetlist";
        }

        catch(Exception e) {

            attributes.addFlashAttribute("error", "An error occured while deleting the dataset!");
            return "redirect:/datasetlist";
        }
    }

    @RequestMapping(value ="/deleteQuestionVersion/{SetId}/{Id}", method=RequestMethod.POST)
    public String deleteQuestionVersion(@PathVariable("SetId") long SetId,
                                        @PathVariable("Id") long Id,
                                        @RequestParam("deleteId") long deleteId,
                                        RedirectAttributes attributes)
    {
        System.out.println("Question to delete: " + deleteId);
        Questions originalQ = questionsService.findDistinctById(Id);
        Questions q = questionsService.findDistinctById(deleteId);
        List<Translations> t = translationsRepository.findByQid(q);


        try {
            if(q.isActiveVersion() || q.isOriginal()) {
                attributes.addFlashAttribute("error", "Deleting a question, that is marked as 'active question' or is a original question, is not allowed!");
                System.out.println("is active or original");
                return "redirect:/questionVersionList/"+SetId + "/" + Id;
            }
            else {
                for (Translations item :t){

                    translationsRepository.delete(item);}

                questionsRepository.delete(q);

                List<Questions> qVersions = questionsService.findQuestionsByDatasetQuestionIdAndQuestionSetId(SetId, Id);
                if(qVersions.size() ==1)
                {
                    originalQ.setAnotated(false);
                    questionsRepository.save(originalQ);
                    System.out.println("SetAnotated to false:" +originalQ.getId());
                }
                attributes.addFlashAttribute("success", "The question was successfully deleted!");
                return "redirect:/questionVersionList/" + SetId + "/" + Id;
            }
        }
        catch(Exception e) {
            attributes.addFlashAttribute("error", "An error occured while deleting the question!");
            return "redirect:/questionVersionList/" + SetId + "/" + Id;
        }

    }


}
