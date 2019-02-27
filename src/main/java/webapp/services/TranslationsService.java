package webapp.services;

import webapp.model.Questions;
import webapp.model.Translations;

import java.util.ArrayList;
import java.util.HashMap;

public interface TranslationsService {

    String saveTranslations (Translations translations);

    HashMap<String,String> getQuestionsByLang(Questions questions);

    HashMap<String,String> getKeywordsByLang(Questions questions);

    ArrayList<String> getLanguages(Questions questions);
   // List<Translations> findTranslationsByQid_ID(long id);
}
