package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.Repository.QuestionsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;

import java.util.*;

@Service
public class QuestionsServiceImpl implements QuestionsService {

    @Autowired
    QuestionsRepository questionsRepository;


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
}
