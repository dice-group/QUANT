package JPATest;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import webapp.Application;
import webapp.repository.DatasetRepository;
import webapp.repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.Dataset;
import webapp.model.User;
import webapp.model.Role;

import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestDatasetRepository {
    @Autowired
    private DatasetRepository datasetRepository;
    @Autowired
    private UserRepository userRepository;


    @Test
    public void creationTest(){

       User peter = new User("peter@heidi.de", "herbert", Role.ADMIN);
       userRepository.save(peter);

        User marie = new User("marie@gmail.com", "SonnenscheininderNacht", Role.USER);
        userRepository.save(marie);

       Dataset d = new Dataset(peter, "qald1");
       datasetRepository.save(d);

       Dataset f = new Dataset(marie, "qald5");
       datasetRepository.save(f);

        List<Dataset> datasets=datasetRepository.findAll();
        for(Dataset dataset:datasets) {
            System.out.println("----------------");
            System.out.println(dataset.getName() + " by "+ dataset.getDatasetUser().getEmail());
            System.out.println(dataset.getName() + " Password-Hash: "+ dataset.getDatasetUser().getPassword());
            System.out.println("----------------");
    }
    }
}
