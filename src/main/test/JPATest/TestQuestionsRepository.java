package JPATest;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import webapp.Application;
import webapp.Repository.DatasetRepository;
import webapp.Repository.QuestionsRepository;
//import webapp.Repository.TranslationsRepository;
import webapp.Repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.*;
//import webapp.model.Translations;

import java.util.List;


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
   // @Autowired
    //private TranslationsRepository translationsRepository;

    BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void creationTest(){

        User peter = new User("peter@heidi.de", bCryptPasswordEncoder.encode("herbert"), Role.ADMIN);
        userRepository.save(peter);

        User marie = new User("marie@gmail.com", bCryptPasswordEncoder.encode("herbert"), Role.USER);
        userRepository.save(marie);

        Dataset d = new Dataset(peter, bCryptPasswordEncoder.encode("herbert"), "Test Dataset");
        datasetRepository.save(d);

        Dataset f = new Dataset(marie, "quald5", "Dataset Quald5 vom 12.2.2222");
        datasetRepository.save(f);

        Questions q = new Questions(d,"String",false,false,false,true, peter,1);
        questionsRepository.save(q);

        //Translations frage = new Translations( q,"de", "Auto, BMW, schnell", "Wer baut den M5?");
        //translationsRepository.save(frage);

        List<Dataset> datasets=datasetRepository.findAll();
        //List<Translations> translations = translationsRepository.findAll();
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

        }

       /* for(Translations item: translations) {
            System.out.println("Translations Output");
            System.out.println(item.getLang());
        }*/
    }
}
