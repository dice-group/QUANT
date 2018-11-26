package webapp.Repository;

import webapp.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QuestionsRepository extends JpaRepository<Questions,Integer>{


}
