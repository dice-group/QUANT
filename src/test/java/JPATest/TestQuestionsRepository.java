package JPATest;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import webapp.Application;
import webapp.repository.DatasetRepository;
import webapp.repository.QuestionsRepository;
import webapp.repository.TranslationsRepository;
import webapp.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.*;
import webapp.model.Translations;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestQuestionsRepository {

    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private TranslationsRepository translationsRepository;


    @Test
    public void creationTest(){

        User peter = new User("peter@heidi.de", "herbert", Role.ADMIN);
        userRepository.save(peter);

        User marie = new User("marie@gmail.com", "SonnenscheininderNacht", Role.USER);
        userRepository.save(marie);

        Dataset d = new Dataset(peter, "qald1");
        datasetRepository.save(d);

        Dataset f = new Dataset(marie, "quald5");
        datasetRepository.save(f);

        Set<String> answers = new HashSet<String>();
        answers.add("Baum");
        answers.add("Straße");

        Questions q = new Questions(d,"String",false,false,false,true, false, true, peter,1, false,1, "SELECT *",answers);
        questionsRepository.save(q);

        List l = new ArrayList<String>();
        l.add("M5");
        l.add("bmw");
        Translations frage = new Translations( q,"de", l, "Wer baut den BMW M5?");
        translationsRepository.save(frage);

        List<Dataset> datasets=datasetRepository.findAll();
        List<Translations> translations = translationsRepository.findAll();
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
            System.out.println(item.getId());
            System.out.println(item.getTranslationsList());

        }

        for(Translations item: translations) {
            System.out.println("Translations Output");
            System.out.println(item.getLang());
        }
    }
}
