package JPATest;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import webapp.Application;
import webapp.Repository.DatasetRepository;
import webapp.Repository.QuestionsRepository;
import webapp.Repository.QuestionRepository;
import webapp.Repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.Dataset;
import webapp.model.User;
import webapp.model.Role;
import webapp.model.Questions;
import webapp.model.Question;

import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestQuestionsRepository {


    private DatasetRepository datasetRepository;

    private UserRepository userRepository;

    private QuestionsRepository questionsRepository;

    private QuestionRepository questionRepository;


    @Test
    public void creationTest(){

        User peter = new User("peter@heidi.de", "herbert", Role.ADMIN);
        userRepository.save(peter);

        User marie = new User("marie@gmail.com", "SonnenscheininderNacht", Role.USER);
        userRepository.save(marie);

        Dataset d = new Dataset(peter, "qald1", "Test Dataset");
        datasetRepository.save(d);

        Dataset f = new Dataset(marie, "quald5", "Dataset Quald5 vom 12.2.2222");
        datasetRepository.save(f);

        Questions q = new Questions(d,"String",false,false,false,true, peter,1);
        questionsRepository.save(q);

        Question frage = new Question ( q,"de", "Auto, BMW, schnell", "Wer baut den M5?");
        questionRepository.save(frage);

        List<Dataset> datasets=datasetRepository.findAll();
        List<Question> question = questionRepository.findAll();
        List<Questions> questions = questionsRepository.findAll();
        for(Dataset dataset:datasets) {
            System.out.println("----------------");
            System.out.println(dataset.getName() + " by "+ dataset.getDatasetUser().getEmail());
            System.out.println(dataset.getName() + " Password-Hash: "+ dataset.getDatasetUser().getPassword());

            System.out.println("----------------");
        }
        for(Questions item:questions){
            System.out.println("Questions Output");
            System.out.println(item.getTimestamp());

        }

        for(Question item:question) {
            System.out.println("Question Output");
            System.out.println(item.getLang());
        }
    }
}
