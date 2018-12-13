package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.Repository.QuestionsRepository;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.User;

@Service
public class QuestionsServiceImpl implements QuestionsService {

    @Autowired
    QuestionsRepository questionsRepository;

    @Override
    public String saveQuestions(Questions questions){
        questionsRepository.save(questions);
        return "Questions successfully saved";


    }
}
