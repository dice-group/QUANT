package webapp.services;


import org.aksw.qa.commons.datastructure.Question;
import webapp.model.Dataset;
import webapp.model.Questions;
import java.util.List;


public interface QuestionsService {

    String saveQuestions(Questions questions);

    List<Questions> getAllQuestions();
    List<Questions> findAllQuestionsByDatasetQuestion_Id(long id);
    List<Questions> findQuestionsByDatasetQuestion_Id(long id);
    List<Questions> findQuestionsByQuestionSetId(String id);
    List<Questions> findQuestionsByDatasetQuestionIdAndQuestionSetId(long setId, String id);
    Questions findDistinctById(long id);
}
