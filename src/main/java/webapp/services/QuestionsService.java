package webapp.services;


import webapp.model.Questions;
import java.util.List;
import java.util.Map;
import java.util.Set;


public interface QuestionsService {

    String saveQuestions(Questions questions);
    String updateQuestions(Questions questions, String answertype, boolean aggregation, boolean onlydb, boolean hybrid,boolean outOfScope, String sparqlQuery, Set<String> answer);
    List<Questions> getAllQuestions();
    List<Questions> findAllQuestionsByDatasetQuestion_Id(long id);
    Questions findQuestionSetIdById(long id);
    List<Questions> findQuestionsByDatasetQuestion_Id(long id);
    List<Questions> findQuestionsByQuestionSetId(long id);
    List<Questions> findQuestionsByDatasetQuestionIdAndQuestionSetId(long setId, long id);
    List<Questions> findAllQuestionsByDatasetQuestion_IdAndActivation(long id,boolean activation);
    List<Questions> findByDatasetQuestion_IdAndVersionAndRemoved(long id, int version, boolean removed);
    Questions findDistinctById(long id);
    public Map<String,List<String>> generateMergingTranslationsMap(long setId, long id);
    public String getBeautifiedQuery(String query);


}
