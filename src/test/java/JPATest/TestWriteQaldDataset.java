package JPATest;

import datahandler.WriteQaldDataset;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import webapp.Application;
import webapp.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.*;
import java.io.File;


@RunWith(SpringRunner.class)
@DataJpaTest()
@SpringBootTest(classes = Application.class)

public class TestWriteQaldDataset {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    WriteQaldDataset w;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void creationTest() {
       // User peter= new User("test@test.com",bCryptPasswordEncoder.encode("password"),Role.ADMIN);
        User peter = new User("peter@heidi.de", bCryptPasswordEncoder.encode("herbert"), Role.ADMIN);
        userRepository.save(peter);

        //File file = new File("src/test/resources/qaldTest.json");
        File file = new File("src/test/resources/wikidata-train-7.json");
      //  File file2 = new File("src/test/resources/qald-8-train-multilingual.json");
        String endpoint = "https://query.wikidata.org/sparql";
        String defaultLanguage="en";

     //   w.qaldWriter(peter, file);
        w.qaldWriter(peter,file, endpoint, defaultLanguage);




    }
    }