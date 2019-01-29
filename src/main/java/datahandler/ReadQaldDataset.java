package datahandler;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Component
public class ReadQaldDataset {

    //Input als Stream
    public static List<IQuestion> readJsonStream(InputStream data) {
        List<IQuestion> out = null;
        try {
            QaldJson json = (QaldJson) ExtendedQALDJSONLoader.readJson((data), QaldJson.class);
            out = EJQuestionFactory.getQuestionsFromQaldJson(json);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out;
        }

    //Input als File
    public static List<IQuestion> readJson(File data) {
        List<IQuestion> out = null;
        try {
            QaldJson json = (QaldJson) ExtendedQALDJSONLoader.readJson(data);
            out = EJQuestionFactory.getQuestionsFromQaldJson(json);

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return out;
    }

}

