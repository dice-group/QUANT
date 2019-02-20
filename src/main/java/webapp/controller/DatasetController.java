package webapp.controller;


import datahandler.WriteJsonFileFromDataset;
import datahandler.WriteQaldDataset;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import suggestion.Suggestions;
import suggestion.keywords.KeyWordSuggestor;
import suggestion.metadata.MetadataSuggestions;
import suggestion.metadata.MetadataSuggestor;
import suggestion.query.QuerySuggestions;
import webapp.Repository.DatasetRepository;
import webapp.Repository.QuestionsRepository;
import webapp.Repository.TranslationsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;
import webapp.services.DatasetService;
import webapp.services.QuestionsService;
import webapp.services.TranslationsService;
import webapp.services.UserService;

import java.io.IOException;
import java.util.*;

@Controller
public class DatasetController {

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    TranslationsRepository translationsRepository;

    @Autowired
    DatasetRepository datasetRepository;

    @Autowired
    DatasetService datasetService;

    @Autowired
    QuestionsService questionsService;

    @Autowired
    TranslationsService translationsService;

    @Autowired
    UserService userService;

    @Autowired
    WriteQaldDataset w;

    @Autowired
    WriteJsonFileFromDataset downloadGenerator;


    Suggestions suggestions = new Suggestions();
    KeyWordSuggestor k = new KeyWordSuggestor();
    MetadataSuggestor m = new MetadataSuggestor();

    @RequestMapping(value = "/datasetlist", method = RequestMethod.GET)
    public ModelAndView datasetList() {
        ModelAndView model = new ModelAndView("/datasetlist");
        model.addObject("Datasets", datasetService.getAllDatasets());
        model.addObject("Title", "QUANT- Dataset Übersicht");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        return model;
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
    public String newDataset(@RequestParam ("file") MultipartFile file,
                                      @RequestParam("endpoint") String endpoint,
                                @RequestParam("defaultLanguage") String defaultLanguage,
                                @RequestParam("datasetName") String datasetName,
                                RedirectAttributes attributes)
    {
        ModelAndView model = new ModelAndView("/newDataset");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);

        try {

            if (!file.isEmpty()){

            w.datsetWriter(user, file, endpoint, defaultLanguage);
            return "redirect:/datasetlist";
            }
            else {
                w.emptyDatasetWriter(user, datasetName, endpoint, defaultLanguage);
                return "redirect:/datasetlist";

            }
        }
        catch (Exception e){
            return "redirect:/newDataset";
        }

    }


    @RequestMapping(value = "/questionslist/{id}", method = RequestMethod.GET)
    public ModelAndView questionList(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/questionslist");
        Dataset d = datasetService.findDatasetByID(id);
        List<Questions> qL = questionsService.findByDatasetQuestion_IdAndVersionAndRemoved(id, 0, false);
        model.addObject("Questions", qL);
        model.addObject("DatasetName", datasetService.findDatasetByID(id).getName());
        model.addObject("Title", "QUANT - Dataset Questions");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);

        return model;
    }



    @RequestMapping(value = "/anotate/{id}", method = RequestMethod.GET)
    public ModelAndView anotate(@PathVariable("id") long id) {
        ModelAndView model = new ModelAndView("/anotate");
        Questions q = questionsService.findDistinctById(id);
        model.addObject("Question", q);
        model.addObject("GoldenAnswer", q.getAnswerAsString());
        model.addObject("nextQuestion", q.getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(id).getDatasetQuestion().getId())));
        model.addObject("formQuestion", new Questions());
        model.addObject("LanguageKeys", translationsService.getQuestionsByLang(q).keySet());

        HashMap<String, String> questionStrings = translationsService.getQuestionsByLang(q);
        model.addObject("TranslationMap", questionStrings);

        HashMap<String, String> keywordMap = translationsService.getKeywordsByLang(q);
        model.addObject("KeywordMap", keywordMap);

        ArrayList<String> lang = translationsService.getLanguages(q);
        model.addObject("Language", lang);
        String dL = q.getDatasetQuestion().getDefaultLanguage();
        String defaultLang ="";
        if (!"".equals(dL))
        {
            defaultLang = dL;
        }
        else {
            defaultLang = lang.get(0);
        }
        model.addObject("DefaultLanguage", defaultLang);


        QuerySuggestions qs =new QuerySuggestions();
        if (!q.getAnswer().isEmpty()) {
           String setElement = (String)q.getAnswer().iterator().next();
           qs= suggestions.gernerateQuerySuggestions(q.getSparqlQuery(),q.getDatasetQuestion().getEndpoint(),setElement);
        }

        Set<String> set=new HashSet();
        qs.getAnswers().ifPresent(rs -> {
            while (rs.hasNext()){
                        String var = rs.getResultVars().get(0);
                        set.add(rs.next().get(var).toString());
                    }
            });

        qs.getBooleanAnswer().ifPresent(val->set.add(val.toString()));

       model.addObject("Suggestion", qs);
       model.addObject("EndpointAnswer", String.join("\n", set));


       Map<String, String> keywordSuggestionsMap = new HashMap<String, String>();

       for(String item: lang) {

           if (questionStrings.containsKey(item) && k.hasStopwords(item) && keywordMap.get(item).isEmpty() )
           {
               String keywordString = String.join(",", k.suggestKeywords(questionStrings.get(item), item));
               keywordSuggestionsMap.put(item,keywordString);

           }

       }
       model.addObject("KeywordSuggestion", keywordSuggestionsMap);
       MetadataSuggestions s = m.getMetadataSuggestions(q.getSparqlQuery(),q.getDatasetQuestion().getEndpoint() );
       model.addObject("MetadataSuggestion", s);


       return model;
    }


    @RequestMapping(value = "/anotate/{id}", method=RequestMethod.POST)
    public String newVersion(@PathVariable("id") long id, @RequestParam("answertype") String answertype,
                             @RequestParam("optscope") boolean outOfScope,
                                   @RequestParam("optaggregation")boolean aggregation,
                             @RequestParam("optdbpedia")boolean onlydb,
                                   @RequestParam("opthybrid") boolean hybrid,
                             @RequestParam("sparql") String sparqlQuery,
                                   @RequestParam("file_answer") Set<String> answer,
                             @RequestParam("trans_lang") List<String> trans_lang,
                                   @RequestParam("trans_question") List<String> trans_question,
                                   @RequestParam("trans_keywords") List<String> trans_keywords)
        {
        ModelAndView model=new ModelAndView("/newVersion");
        long nextQuestion = questionsService.findDistinctById(id).getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(id).getDatasetQuestion().getId()));
        Questions q = questionsService.findDistinctById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        Dataset dataset = q.getDatasetQuestion() ;
        long questionSetId = q.getQuestionSetId();
        boolean original =false;
        Questions v = questionsRepository.findTop1VersionByQuestionSetIdOrderByVersionDesc(questionSetId);
        int version = v.getVersion() +1;

        try {
            // save Question in neuer Version
            Questions newQuestionVersion = new Questions(dataset, answertype, aggregation, onlydb, hybrid, original, false, true, user, version, outOfScope, questionSetId, sparqlQuery, answer);
            questionsService.saveQuestions(newQuestionVersion);

            // dann Schleife über "trans_lang" - jedes Element erzeugt neuen Datensatz


                for (int i = 0; i < trans_lang.size(); i++) {
                    List<String> keywords = null;
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
                        Translations translations = new Translations(newQuestionVersion, trans_lang.get(i), keywords, trans_question.get(i));
                        translationsService.saveTranslations(translations);
                    }
                }


            q.setAnotated(true);
            questionsService.saveQuestions(q);
            System.out.println( "Successfully saved new question version to Database!");

            if (questionsService.findDistinctById(nextQuestion).getVersion()==0)
            {
                return "redirect:/anotate/"+nextQuestion;
            }
            else {
                model.addObject("successMessage", "This was the last question!");
                return "redirect:/anotate/"+id;
            }

        }
        catch(Exception e) {
            model.addObject("errorMessage","Something went wrong!");
            return "redirect:/anotate/"+id;
        }
    }


    @RequestMapping(value="manageDataset/{id}", method = RequestMethod.GET)
    public ModelAndView manageDataset(@PathVariable ("id") long id){
        ModelAndView model = new ModelAndView("/manageDataset");
        model.addObject("Dataset", datasetService.findDatasetByID(id));
        model.addObject("Questions", questionsService.findAllQuestionsByDatasetQuestion_Id(id));

        return model;
    }

    @RequestMapping(value = "/manageDataset/{id}", method=RequestMethod.POST)
    public String deleteQuestion(@RequestParam("deleteId") long deleteId,
                                 @PathVariable("id") long datasetId,
                                 RedirectAttributes attributes)
    {
        ModelAndView model=new ModelAndView("/deleteQuestion");
        Questions q = questionsService.findDistinctById(deleteId);
        List<Translations> t = translationsRepository.findByQid(q);

        System.out.println("Question to delete: " + deleteId);

        try {
            if(q.isActiveVersion() || q.isOriginal()) {
                attributes.addFlashAttribute("error", "Deleting a question, that is marked as 'active question' or is a original question, is not allowed!");
                System.out.println("is active or original");
                return "redirect:/manageDataset/"+ datasetId;
            }
            else {
                for (Translations item :t){

                    translationsRepository.delete(item);}

                questionsRepository.delete(q);
                attributes.addFlashAttribute("success", "The question was successfully deleted!");
                return "redirect:/manageDataset/" + datasetId;
            }
    }
        catch(Exception e) {
            attributes.addFlashAttribute("error", "An error occured while deleting the question!");
            return "redirect:/manageDataset/" + datasetId;
    }
    }
    @RequestMapping(path = "/download/{id}", method = RequestMethod.GET)
    public ResponseEntity<ByteArrayResource> download(@PathVariable ("id") long id) throws IOException {

        // ...
        byte[] file = downloadGenerator.generateJsonFileFromDataset(id);
        ByteArrayResource resource = new ByteArrayResource(file);
        HttpHeaders headers = new HttpHeaders(); headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=datasetdownload_"+id+".json");
        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(file.length)
                .contentType(MediaType.parseMediaType("application/octet-stream"))
                .body(resource);
    }



}


