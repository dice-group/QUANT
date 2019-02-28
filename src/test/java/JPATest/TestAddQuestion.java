package JPATest;

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

import java.util.HashSet;
import java.util.Set;


@RunWith(SpringRunner.class)
//@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestAddQuestion {

    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private UserRepository userRepository;
    /*@Autowired
    private QuestionsRepository questionsRepository;
    @Autowired
    private TranslationsRepository translationsRepository;*/

    //Outdated Test
    @Test
    public void creationTest() {

        Dataset d = datasetRepository.findDatasetById(2);
        User peter = userRepository.findByEmail("peter@heidi.de");

        Set<String> answers = new HashSet<String>();
        answers.add("Baum");
        answers.add("Straße");

        Questions q = new Questions(d,"String",false,false,false,true, true, false, peter,1, false,1, "Select *", answers);
        /*
        questionsRepository.save(q);
        List l = new ArrayList<String>();
        l.add("M5");
        l.add("bmw");
        Translations frage = new Translations( q,"de", l, "Wer baut den BMW M5?");
        translationsRepository.save(frage);
        */
    }
}
