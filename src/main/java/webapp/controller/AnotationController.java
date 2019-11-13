package webapp.controller;

import com.google.common.collect.Sets;
import datahandler.WriteJsonFileFromDataset;
import datahandler.WriteQaldDataset;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import suggestion.Suggestions;
import suggestion.keywords.KeyWordSuggestor;
import suggestion.metadata.MetadataSuggestions;
import suggestion.metadata.MetadataSuggestor;
import suggestion.query.QuerySuggestions;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;
import webapp.repository.DatasetRepository;
import webapp.repository.QuestionsRepository;
import webapp.repository.TranslationsRepository;
import webapp.services.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Instant;
import java.util.*;


@Controller
public class AnotationController {

    private static final Logger logger = Logger.getLogger(AnotationController.class.getName());

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

    @Autowired
    SuggestionsLogger suggestionsLogger;

    @Value("${suggestions.logging.active}")
    private boolean loggingActive;


    Suggestions suggestions = new Suggestions();
    KeyWordSuggestor k = new KeyWordSuggestor();
    MetadataSuggestor m = new MetadataSuggestor();


    @RequestMapping(value = "/anotate/{id}", method = RequestMethod.GET)
    public ModelAndView anotate(@PathVariable("id") long id,
                                RedirectAttributes attributes) {
        ModelAndView model = new ModelAndView("/anotate");
        Instant zeit = Instant.now();
        long milli = zeit.toEpochMilli();
        model.addObject("beginn",milli);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        model.addObject("User", user);
        Questions q = questionsService.findDistinctById(id);
        Questions x = questionsRepository.findTop1QuestionByQuestionSetIdAndAnotatorUserAndVersionGreaterThan(q.getId(), user, 0);
        if (x != null) {
            q = x;
        }
        model.addObject("Question", q);
        q.setSparqlQuery(questionsService.getBeautifiedQuery(q.getSparqlQuery()));
        model.addObject("LanguageKeys", translationsService.getQuestionsByLang(q).keySet());
        HashMap<String, String> questionStrings = translationsService.getQuestionsByLang(q);
        model.addObject("TranslationMap", questionStrings);
        ArrayList<String> lang = translationsService.getLanguages(q);
        model.addObject("Language", lang);
        String dL = q.getDatasetQuestion().getDefaultLanguage();
        String defaultLang = "";
        if (!"".equals(dL)) {
            defaultLang = dL;
        } else {
            defaultLang = lang.get(0);
        }
        model.addObject("DefaultLanguage", defaultLang);
        model.addObject("nextQuestion", q.getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(id).getDatasetQuestion().getId())));
        model.addObject("formQuestion", new Questions());

        if(q.getSparqlQuery() != null) {
            model.addObject("GoldenAnswer", q.getAnswerAsString());
            HashMap<String, String> keywordMap = translationsService.getKeywordsByLang(q);
            model.addObject("KeywordMap", keywordMap);

            QuerySuggestions qs = new QuerySuggestions();
            if (!q.getAnswer().isEmpty()) {
                String setElement = (String) q.getAnswer().iterator().next();
                qs = suggestions.generateQuerySuggestions(q.getSparqlQuery(), q.getDatasetQuestion().getEndpoint(), setElement);
            }
            Set<String> set = new HashSet();
            qs.getAnswers().ifPresent(rs -> {
                while (rs.hasNext()) {
                    String var = rs.getResultVars().get(0);
                    set.add(rs.next().get(var).toString());
                }
            });
            qs.getBooleanAnswer().ifPresent(val -> set.add(val.toString()));
            model.addObject("Suggestion", qs);
            System.out.println(set);
            model.addObject("EndpointAnswer", String.join("\n", set));
            Map<String, String> keywordSuggestionsMap = new HashMap<String, String>();
            for (String item : lang) {

                if (questionStrings.containsKey(item) && k.hasStopwords(item) && keywordMap.get(item).isEmpty()) {
                    String keywordString = String.join(",", k.suggestKeywords(questionStrings.get(item), item));
                    keywordSuggestionsMap.put(item, keywordString);
                }
            }
            model.addObject("KeywordSuggestion", keywordSuggestionsMap);
            MetadataSuggestions s = m.getMetadataSuggestions(q.getSparqlQuery(), q.getDatasetQuestion().getEndpoint());
            model.addObject("MetadataSuggestion", s);
        }
        else {
            MetadataSuggestions s = new MetadataSuggestions();
            model.addObject("MetadataSuggestion", s);
        }
        return model;
    }


    @RequestMapping(value = "/anotate/{id}", method = RequestMethod.POST)
    public String newVersion(@PathVariable("id") long id,
                             @RequestParam("js_duration") String js_duration,
                             @RequestParam("beginn") String beginn,
                             @RequestParam("answertype") String answertype,
                             @RequestParam("optscope") boolean outOfScope,
                             @RequestParam("optaggregation") boolean aggregation,
                             @RequestParam("optdbpedia") boolean onlydb,
                             @RequestParam("opthybrid") boolean hybrid,
                             @RequestParam("sugg_answertype") String sugg_answertype,
                             @RequestParam("sugg_optscope") boolean sugg_outOfScope,
                             @RequestParam("sugg_optaggregation") boolean sugg_aggregation,
                             @RequestParam("sugg_optdbpedia") boolean sugg_onlydb,
                             @RequestParam("sugg_opthybrid") boolean sugg_hybrid,
                             @RequestParam("sparql") String sparqlQuery,
                             @RequestParam("sugg_sparql") String sugg_sparqlQuery,
                             @RequestParam("sparql_loaded") boolean sparqlLoaded,
                             @RequestParam("file_answer") String answerString,
                             @RequestParam("trans_lang") List<String> trans_lang,
                             @RequestParam("trans_question") List<String> trans_question,
                             @RequestParam("trans_keywords") List<String> trans_keywords,
                             @RequestParam("sugg_trans_keywords") List<String> sugg_trans_keywords,
                             RedirectAttributes attributes) {
        Instant zeit = Instant.now();
        long ende = zeit.toEpochMilli();
        long start = Long.parseLong(beginn);
        long duration2;
        long duration = 0;

        try{
            duration = Long.parseLong(js_duration);
            System.out.println("Zeitdauer laut Javascript: " + duration + " Millisekunden");
            duration2 = ende - start;
            System.out.println("Zeitraum laut Controller: " + duration2 + " Millisekunden");
        }
        catch (Exception e)
        {
            duration = -1;
            duration2 = -1;
            System.out.println("Die Zeit konnte nicht gemessen werden.");
        }

        Questions q = questionsService.findDistinctById(id);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String username = auth.getName();
        User user = userService.getByEmail(username);
        Dataset dataset = q.getDatasetQuestion();
        long questionSetId = q.getQuestionSetId();
        boolean original = false;
        Questions v = questionsRepository.findTop1VersionByQuestionSetIdOrderByVersionDesc(questionSetId);
        int version = v.getVersion() + 1;
        Questions anotatedVersion = questionsRepository.findTop1QuestionByQuestionSetIdAndAnotatorUserAndVersionGreaterThan(q.getQuestionSetId(), user, 0);
        Set<String> answer = new HashSet<>(Arrays.asList(answerString.split("\r\n")));
        String dL = q.getDatasetQuestion().getDefaultLanguage();
        long qSetId = questionsService.findDistinctById(id).getQuestionSetId();
        long nextQuestion2 = questionsService.findQuestionSetIdById(qSetId).getNext(questionsService.findAllQuestionsByDatasetQuestion_Id(questionsService.findDistinctById(id).getDatasetQuestion().getId()));
        long nextQuestion = questionsService.findQuestionSetIdById(qSetId).getNext(questionsRepository.findByIdEqualsQuestionSetId(q.getDatasetQuestion()));
        // long idTest = questionsRepository.findAllQuestions()

        String redirect;

        System.out.println("nextQuestion: " + nextQuestion + " nextQuestion2: " +nextQuestion2);
        if (!trans_lang.contains(dL)) {
            attributes.addFlashAttribute("error", "There must be at least a translation in the default language'" + dL + "'!");
            redirect = "redirect:/anotate/" + id;
        } else {

            if (anotatedVersion != null) {
                System.out.println("found anotated version:" + anotatedVersion.getId());
                questionsService.updateQuestions(anotatedVersion, answertype, aggregation, onlydb, hybrid, outOfScope, sparqlQuery, answer);
                List<Translations> t = translationsRepository.findByQid(anotatedVersion);
                for (Translations item : t) {
                    translationsRepository.delete(item);
                }


                for (int i = 0; i < trans_lang.size(); i++) {
                    List<String> keywords = null;
                    if (trans_keywords.size()>0 && !trans_keywords.get(i).isEmpty()) {
                        if (trans_lang.size() > 1) {
                            keywords = Arrays.asList(trans_keywords.get(i).split(",\\s?"));
                        } else {
                            keywords = trans_keywords;
                        }
                    }
                    if (!"".equals(trans_lang.get(i)) && !"".equals(trans_question.get(i))) {
                        if (keywords == null)
                        {
                            Translations translations = new Translations(anotatedVersion, trans_lang.get(i), trans_question.get(i));
                            translationsService.saveTranslations(translations);
                        }
                        else {
                            Translations translations = new Translations(anotatedVersion, trans_lang.get(i), keywords, trans_question.get(i));
                            translationsService.saveTranslations(translations);
                        }
                    }
                }
             // if last question stop!
                if(nextQuestion ==-1) {
                    attributes.addFlashAttribute("success", "Updated anotated question. No more questions in list!");
                    redirect = "redirect:/questionslist/" + dataset.getId();
                }
                else {
                    attributes.addFlashAttribute("success", "Updated anotated question!");
                    redirect = "redirect:/anotate/" + nextQuestion;
                }
            }

            else {

                try {
                    // save Question in neuer Version
                    Questions newQuestionVersion = new Questions(dataset, answertype, aggregation, onlydb, hybrid, original, false, true, user, version, outOfScope, questionSetId, sparqlQuery, answer);
                    questionsService.saveQuestions(newQuestionVersion);

                    for (int i = 0; i < trans_lang.size(); i++) {
                        List<String> keywords = null;
                        if (trans_keywords.size()>0 && !trans_keywords.get(i).isEmpty()) {
                            if (trans_lang.size() > 1) {
                                keywords = Arrays.asList(trans_keywords.get(i).split(",\\s?"));
                            } else {
                                keywords = trans_keywords;
                            }
                        }
                        if (!"".equals(trans_lang.get(i)) && !"".equals(trans_question.get(i))) {
                            if(keywords==null)
                            {
                                Translations translations = new Translations(newQuestionVersion, trans_lang.get(i), trans_question.get(i));
                                translationsService.saveTranslations(translations);
                            }
                            else
                            {
                                Translations translations = new Translations(newQuestionVersion, trans_lang.get(i), keywords, trans_question.get(i));
                                translationsService.saveTranslations(translations);
                            }

                        }
                    }

                    System.out.println("Successfully saved new question version to Database!");

                    if (nextQuestion ==-1)
                    {
                        attributes.addFlashAttribute("success", "No more questions!");
                        redirect = "redirect:/questionslist/" + dataset.getId();
                    }
                    else  //(questionsService.findDistinctById(nextQuestion).getVersion() == 0)
                    {
                        redirect = "redirect:/anotate/" + nextQuestion;
                    }


                } catch (Exception e) {
                    attributes.addFlashAttribute("error", "Something went wrong!");
                    redirect = "redirect:/anotate/" + id;
                }
            }
        }

        // Logging the annotation stats in the CSV
        if(loggingActive) {
            long finalDuration = duration;
            boolean sparqlSuggested = sugg_sparqlQuery != null && !sugg_sparqlQuery.trim().isEmpty();

            Map<String, Object> loggingParameters = new HashMap<String, Object>(){{
                put(SuggestionsLogger.HEADER.QUESTION_ID.getName(), questionSetId);
                put(SuggestionsLogger.HEADER.ANSWER_TYPE.getName(), answertype.equalsIgnoreCase(sugg_answertype));
                put(SuggestionsLogger.HEADER.OUT_OF_SCOPE.getName(), outOfScope == sugg_outOfScope);
                put(SuggestionsLogger.HEADER.AGGREGATION.getName(), aggregation == sugg_aggregation);
                put(SuggestionsLogger.HEADER.DBPEDIA_ONLY.getName(), onlydb == sugg_onlydb);
                put(SuggestionsLogger.HEADER.HYBRID.getName(), hybrid == sugg_hybrid);
                put(SuggestionsLogger.HEADER.SPARQL_SUGGESTED.getName(), sparqlSuggested);
                put(SuggestionsLogger.HEADER.SPARQL_LOADED.getName(), sparqlSuggested ? sparqlLoaded : "");
                put(SuggestionsLogger.HEADER.SPARQL_UNCHANGED.getName(), sparqlSuggested && sparqlLoaded ? sparqlQuery.equalsIgnoreCase(sugg_sparqlQuery) : "");
                put(SuggestionsLogger.HEADER.TIME_TAKEN_MILLIS.getName(), finalDuration);
                put(SuggestionsLogger.HEADER.TIMESTAMP.getName(), new Date().toString());
            }};

            // keywords suggestions
            for(String lang : translationsService.getLanguages(q)) // Get all the supported languages
            {
                SuggestionsLogger.HEADER forLang = SuggestionsLogger.HEADER.getForLang(lang); // Get the HEADER reference for the language
                if(forLang != null && !sugg_trans_keywords.isEmpty()) {
                    if (trans_lang.contains(lang)) { // Check if the supported lang was submitted through request params
                        int index = trans_lang.indexOf(lang);

                        if("".equals(sugg_trans_keywords.get(index))) { // Check if the system provided suggestions for the language
                            loggingParameters.put(forLang.getName(), "");
                        }
                        else { // If the suggestions were provided by the system then compare the submitted keywords against suggested keywords
                            Set<String> submitted = this.cleanupKeywords(trans_keywords.get(index).trim().split(","));
                            Set<String> suggested = this.cleanupKeywords(sugg_trans_keywords.get(index).trim().split(","));

                            int pickedSuggestions = Sets.intersection(submitted, suggested).size();
                            int totalSuggested = suggested.size();

                            loggingParameters.put(forLang.getName(), new BigDecimal((double)pickedSuggestions/totalSuggested).setScale(2, RoundingMode.HALF_UP).doubleValue());
                        }
                    } else { // If not, log an empty value
                        loggingParameters.put(forLang.getName(), "");
                    }
                } else {
                    logger.warn("Header not found for language: " + lang + ". Suggestions stats will not be logged. Please add the header for the language in SuggestionsLogger.HEADER");
                }
            }

            suggestionsLogger.log(q, loggingParameters);
        }

        return redirect;

    }

    private Set<String> cleanupKeywords(String[] keywords)
    {
        Set<String> keywordsSet = new HashSet<>();
        for(String kw : keywords)
        {
            String trimmed = kw.trim();
            if(!trimmed.isEmpty())
                keywordsSet.add(trimmed);
        }
        return keywordsSet;
    }

}
