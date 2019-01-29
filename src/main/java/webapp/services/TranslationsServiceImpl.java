package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.Repository.TranslationsRepository;
import webapp.model.Translations;

import java.util.List;

@Service
public class TranslationsServiceImpl implements TranslationsService {

    @Autowired
    TranslationsRepository translationsRepository;

    @Override
    public String saveTranslations(Translations translations){

        translationsRepository.save(translations);
        return "Translations Successfully saved";
    }

  //  @Override
  //  public List<Translations> findTranslationsByQid_ID(long id)  {return translationsRepository.findTranslationsByQid_ID(id);}
}
