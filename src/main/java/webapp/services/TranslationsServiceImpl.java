package webapp.services;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.repository.TranslationsRepository;
import webapp.model.Questions;
import webapp.model.Translations;

import java.util.*;


@Service
public class TranslationsServiceImpl implements TranslationsService {

    @Autowired
    TranslationsRepository translationsRepository;


    @Override
    public String saveTranslations(Translations translations){

        translationsRepository.save(translations);
        return "Translations Successfully saved";
    }


    @Override
    public HashMap<String,String> getQuestionsByLang(Questions questions)
    {
        List<Translations> translationsList = translationsRepository.findByQid(questions);
        HashMap questionMap = new HashMap();

        for(Translations item:translationsList) {
            questionMap.put( item.getLang() , item.getQuestionString());
        }
        return  questionMap;
    }

    @Override
    public HashMap<String, String> getKeywordsByLang(Questions questions)
    {
        List<Translations> translationsList = translationsRepository.findByQid(questions);
        HashMap keywordMap = new HashMap();

        for(Translations item:translationsList)
        {
            keywordMap.put(item.getLang(), item.getKeywordsAsString());
        }

        return keywordMap;
    }

    @Override
    public ArrayList<String> getLanguages(Questions questions)
    {
        List<Translations> languages = translationsRepository.findByQid(questions);
        ArrayList<String>sortedLanguages =new ArrayList<>();
        for (Translations item:languages) {
            sortedLanguages.add(item.getLang());
        }
        Collections.sort(sortedLanguages);
        return sortedLanguages;
        }
}
