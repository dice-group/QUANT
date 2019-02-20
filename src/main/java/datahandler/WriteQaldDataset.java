package datahandler;

import org.aksw.qa.commons.datastructure.IQuestion;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import webapp.services.TranslationsServiceImpl;
import webapp.model.Dataset;
import webapp.model.Questions;
import webapp.model.Translations;
import webapp.model.User;
import webapp.services.DatasetServiceImpl;
import webapp.services.QuestionsServiceImpl;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Component
public class WriteQaldDataset {


    @Autowired
    DatasetServiceImpl datasetService;
    @Autowired
    QuestionsServiceImpl questionsService;
    @Autowired
    TranslationsServiceImpl translationsService;

    public File convert(MultipartFile file) throws IllegalStateException, IOException
    {
        File convFile = new File(file.getOriginalFilename());
        convFile.createNewFile();
        FileOutputStream fStream = new FileOutputStream(convFile);
        fStream.write(file.getBytes());
        fStream.close();
        return convFile;
    }

    public void emptyDatasetWriter(User user, String name, String endpoint, String defaultLanguage)
    {
        try {
            Dataset dataset = new Dataset(user, name, endpoint, defaultLanguage);
            datasetService.saveDataset(dataset);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void datsetWriter(User user, MultipartFile file, String endpoint, String defaultLanguage)
    {
        try {
            qaldWriter(user, convert(file), endpoint, defaultLanguage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void qaldWriter(User user, File file, String endpoint, String defaultLanguage) {

        //get Dataset ID and save to database
        try {
            JSONParser jsonParser = new JSONParser();
            JSONObject jsonObject = (JSONObject) jsonParser.parse(new FileReader(file));
            JSONObject g = (JSONObject) jsonObject.get("dataset");
            String datasetId = (String) g.get("id");
            Dataset dataset = new Dataset(user, datasetId, endpoint, defaultLanguage);
            datasetService.saveDataset(dataset);

            //get Questions and save to database
            ReadQaldDataset readQaldDataset = new ReadQaldDataset();
            List<IQuestion> questions = readQaldDataset.readJson(file);

            for (IQuestion d: questions) {
                Questions q = new Questions(dataset, d.getAnswerType(), d.getAggregation(), d.getOnlydbo(), d.getHybrid(),
                        true, true, false, user, 0, false, d.getSparqlQuery(), d.getGoldenAnswers(), d.getId());
                questionsService.saveQuestions(q);
                q.setQuestionSetId(q.getId());
                questionsService.saveQuestions(q);

                Set<String> keys=d.getLanguageToQuestion().keySet();
                for(String key:keys){
                    String frage;
                    List keywords;

                    if(d.getLanguageToQuestion().get(key) != null)
                    {frage = d.getLanguageToQuestion().get(key);}
                    else {frage = "";}

                    if(d.getLanguageToKeywords().get(key) != null)
                    { keywords = d.getLanguageToKeywords().get(key);}
                    else { keywords = new ArrayList();}

                   // Translations t = new Translations(q,key,d.getLanguageToKeywords().get(key),d.getLanguageToQuestion().get(key));

                    Translations t = new Translations(q, key, keywords, frage);
                    translationsService.saveTranslations(t);

                }


            }
            System.out.println( "Successfully saved Dataset to Database");
        }

         catch (ParseException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
