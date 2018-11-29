package datahandler;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.aksw.qa.commons.load.json.EJQuestionFactory;
import org.aksw.qa.commons.load.json.ExtendedQALDJSONLoader;
import org.aksw.qa.commons.load.json.QaldJson;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;


public class ReadQald {

    public static List<IQuestion> readJson(InputStream data) {
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


    }

