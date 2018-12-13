package JPATest;

import datahandler.WriteQaldDataset;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
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
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestWriteQaldDataset {


    @Autowired
    private UserRepository userRepository;
    @Autowired
    WriteQaldDataset w;

    @Test
    public void creationTest() {

        User peter = new User("peter@heidi.de", "herbert", Role.ADMIN);
        userRepository.save(peter);

        File file = new File("src/test/resources/qualdTest.json");


        w.qaldWriter(peter, file);




    }
    }