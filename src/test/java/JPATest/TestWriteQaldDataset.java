package JPATest;

import datahandler.WriteQaldDataset;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import webapp.Application;
import webapp.Repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.*;
import java.io.File;


@RunWith(SpringRunner.class)
//@DataJpaTest()
@SpringBootTest(classes = Application.class)

public class TestWriteQaldDataset {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    WriteQaldDataset w;
    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    @Test
    public void creationTest() {
       // User u = new User("test@test.com",bCryptPasswordEncoder.encode("password"),Role.ADMIN);
        User peter2 = new User("peter2@heidi.de", bCryptPasswordEncoder.encode("herbert"), Role.ADMIN);
        userRepository.save(peter2);

        //File file = new File("src/test/resources/qaldTest.json");
        File file = new File("src/test/resources/wikidata-train-7.json");
        File file2 = new File("src/test/resources/qald-8-train-multilingual.json");

     //   w.qaldWriter(peter, file);
        w.qaldWriter(peter2,file2);




    }
    }