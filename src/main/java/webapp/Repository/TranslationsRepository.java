package webapp.Repository;


import webapp.model.Translations;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TranslationsRepository extends JpaRepository<Translations,Integer>{


 //   List<Translations> findTranslationsByQid_ID(long id);
}
