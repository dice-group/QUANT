package webapp.Repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import webapp.model.Translations;

@Service
public class TranslationsServiceImpl implements TranslationsService {

    @Autowired
    TranslationsRepository translationsRepository;

    @Override
    public String saveTranslations(Translations translations){

        translationsRepository.save(translations);
        return "Translations Successfully saved";
    }
}
