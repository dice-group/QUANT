package JPATest;


import datahandler.ReadQaldDataset;
import org.aksw.qa.commons.datastructure.IQuestion;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.Application;

import java.io.*;
import java.util.List;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestReadQaldDataset {

    @Test
    public void ReadQaldTest() {


        try {
            //InputStream stream = new FileInputStream(new File("src/test/resources/qualdTest.json"));

            File file = new File("src/test/resources/qualdTest.json");

            //Get dataset-ID
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));
            JSONObject g = (JSONObject) jsonObject.get("dataset");
            String datasetId = (String) g.get("id");
            System.out.println(datasetId);

            //Get questions
            ReadQaldDataset readQaldDataset = new ReadQaldDataset();
            List<IQuestion> questions = readQaldDataset.readJson(file);
           // List<IQuestion> questions = readQaldDataset.readJsonStream(stream);
            for (IQuestion d: questions) {
                System.out.println(d.getLanguageToKeywords());
            }

        } catch (
                FileNotFoundException e) {
            e.printStackTrace();

        } catch (
                IOException e) {
            e.printStackTrace();

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }
}
