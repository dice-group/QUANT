package generateJsonTest;


import datahandler.WriteJsonFileFromDataset;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.Application;
import webapp.Repository.DatasetRepository;
import webapp.Repository.QuestionsRepository;
import webapp.model.Dataset;

import java.util.List;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringRunner.class)
//@DataJpaTest
@SpringBootTest(classes = Application.class)
public class GenerateJsonTest {

    @Autowired WriteJsonFileFromDataset writer;
    @Autowired
    DatasetRepository datasetRepository;

    @Test
    public void Questions2JsonTest(){
        List<Dataset> ds = datasetRepository.findAll();
        if(ds.size()>0) {
            long datasetId = ds.get(0).getId();
            String s = new String(writer.generateJsonFileFromDataset(datasetId));
            assertNotNull(s);
        }
    }
}
