package datahandler;


import com.fasterxml.jackson.core.JsonProcessingException;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.datastructure.Question;
import org.aksw.qa.commons.load.json.EJDataset;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.json.simple.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.services.QuestionsService;
import org.aksw.qa.commons.load.json.QaldJson;

import java.util.*;

@Component
@Transactional
public class WriteJsonFileFromDataset {


    @Autowired
    QuestionsService questionsService;


    public byte[] generateJsonFileFromDataset(long id){
        List<Questions> questionsList= questionsService.findAllQuestionsByDatasetQuestion_IdAndActivation(id,true);
        List<IQuestion>iqs = new ArrayList<>() ;
        for(Questions questions:questionsList) {
            IQuestion iq = new Question();
            iq.setAggregation(questions.isAggregation());
            iq.setAnswerType(questions.getAnswertype());
            iq.setGoldenAnswers(questions.getAnswer());
            iq.setHybrid(questions.isHybrid());
            iq.setId(""+questions.getId());
            iq.setOnlydbo(questions.isOnlydb());
            iq.setOutOfScope(questions.isOutOfScope());
            if(!questions.isHybrid())
                iq.setSparqlQuery(questions.getSparqlQuery());
            else iq.setPseudoSparqlQuery(questions.getSparqlQuery());
            Map<String,List<String>>languageToKeyWords = new HashMap<>();
            Map<String,String>languageToQuestion = new HashMap<>();
            for(Translations t:questions.getTranslationsList()) {
                if(!t.getKeywords().isEmpty())
                    languageToKeyWords.put(t.getLang(), t.getKeywords());
                if(!t.getQuestionString().isEmpty())
                    languageToQuestion.put(t.getLang(),t.getQuestionString());
            }
            iq.setLanguageToKeywords(languageToKeyWords);
            iq.setLanguageToQuestion(languageToQuestion);
            iqs.add(iq);
        }
        QaldJson s = EJQuestionFactory.getQaldJson(iqs);
        s.setDataset(new EJDataset().setId("quantDataset_"+questionsList.get(0).getQuestionSetId()));
        try {
            byte[]b=ExtendedQALDJSONLoader.writeJson(s);
            return  b;
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
