package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.Repository.QuestionsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.User;

import java.util.List;

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
}
