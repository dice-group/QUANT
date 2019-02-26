package webapp.Repository;

import webapp.model.Questions;
import webapp.model.Translations;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public interface TranslationsRepository extends JpaRepository<Translations,Integer>{

    List<Translations> findByQid(Questions q);
    ArrayList<String> findLangByQid(Questions q);

    Optional<Translations> findByQidAndLang(Questions q, String lang);

}
