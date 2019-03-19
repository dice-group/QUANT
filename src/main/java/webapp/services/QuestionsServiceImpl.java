package webapp.services;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Service;
import webapp.model.Dataset;
import webapp.repository.QuestionsRepository;
import webapp.model.Questions;
import webapp.model.Translations;

import java.util.*;

@Service
public class QuestionsServiceImpl implements QuestionsService {

    @Autowired
    QuestionsRepository questionsRepository;

    @Autowired
    QuestionsService questionsService;


    @Override
    public String saveQuestions(Questions questions){
        questionsRepository.save(questions);
        return "Questions successfully saved";

    }

    @Override
    public List<Questions> getAllQuestions() {return questionsRepository.findAll();}

    @Override
    public List<Questions> findAllQuestionsByDatasetQuestion_Id(long id) {return questionsRepository.findAllQuestionsByDatasetQuestion_Id(id);}

    @Override
    public List<Questions> findAllQuestionsByDatasetQuestion_IdAndActivation(long id,boolean activation) {return questionsRepository.findQuestionsByDatasetQuestion_IdAndActiveVersion(id,activation);}

    @Override
    public List<Questions> findQuestionsByDatasetQuestion_Id(long id) {return questionsRepository.findQuestionsByDatasetQuestion_Id(id);}

    @Override
    public  List<Questions> findQuestionsByQuestionSetId(long id){return questionsRepository.findQuestionsByQuestionSetId(id);}

    @Override
    public Questions findDistinctById(long id) {return questionsRepository.findDistinctById(id);}

    @Override
    public List<Questions> findQuestionsByDatasetQuestionIdAndQuestionSetId(long setId, long id) {return questionsRepository.findQuestionsByDatasetQuestionIdAndQuestionSetId(setId, id);}

    @Override
    public List<Questions> findByDatasetQuestion_IdAndVersionAndRemoved(long id, int version, boolean removed) {return questionsRepository.findByDatasetQuestion_IdAndVersionAndRemoved(id, version, removed);}


    public  Map<String,List<String>> generateMergingTranslationsMap(long setId, long id) {
        List<Questions>versions = questionsRepository.findQuestionsByDatasetQuestionIdAndQuestionSetId(setId, id);
        Map<String,List<String>> mergingTranslationsMap =new HashMap<String, List<String>>();
        for(Questions version:versions){
            Set<String> notMatchedKeys = new HashSet<>();
            notMatchedKeys.addAll(mergingTranslationsMap.keySet());
            for(Translations translation:version.getTranslationsList()){
                if(mergingTranslationsMap.containsKey(translation.getLang())){
                    mergingTranslationsMap.get(translation.getLang()).add(translation.getQuestionString());
                    notMatchedKeys.remove(translation.getLang());
                }
                else{
                    mergingTranslationsMap.put(translation.getLang(), new ArrayList<>());
                    for(int i=0;i<versions.indexOf(version);i++)
                        mergingTranslationsMap.get(translation.getLang()).add(null);
                    mergingTranslationsMap.get(translation.getLang()).add(translation.getQuestionString());
                }
            }
            for(String key:notMatchedKeys)
                mergingTranslationsMap.get(key).add(null);
        }
        return mergingTranslationsMap;
    }

    @Override
    public String updateQuestions(Questions aQ, String answertype, boolean aggregation, boolean onlydb, boolean hybrid, boolean outOfScope, String sparqlQuery, Set<String> answer)
    {
        aQ.setAnswertype(answertype);
        aQ.setAggregation(aggregation);
        aQ.setOnlydb(onlydb);
        aQ.setHybrid(hybrid);
        aQ.setOutOfScope(outOfScope);
        aQ.setSparqlQuery(sparqlQuery);
        aQ.setAnswer(answer);

        questionsService.saveQuestions(aQ);

        return "Question version successfully updated!";
    }

    @Override
    public Questions findQuestionSetIdById(long id)
    {
        return questionsRepository.findQuestionSetIdById(id);
    }

    @Override
    public String getBeautifiedQuery(String query){
        try {
            Query q = QueryFactory.create(query);
            return q.toString();
        }catch (Exception e){
            System.out.println("Query is not correct and could not be beautified");
            return query;
        }

    }

}
