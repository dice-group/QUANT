package JPATest;

import com.fasterxml.jackson.databind.ObjectMapper;
import datahandler.ReadQald;
import org.aksw.qa.commons.load.Dataset;
import org.json.simple.parser.ParseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.Application;

import java.io.*;

@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestReadQald {

    @Test
    public void ReadQaldTest() {


        try {
            InputStream stream = new FileInputStream(new File("src/test/resources/qualdTest.json"));

            ReadQald readQald = new ReadQald();
            readQald.readJson(stream);

        } catch (
                FileNotFoundException e) {
            e.printStackTrace();

        } catch (
                IOException e) {
            e.printStackTrace();

        }

    }
}
