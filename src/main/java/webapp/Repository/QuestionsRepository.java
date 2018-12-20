package webapp.Repository;

import webapp.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions,Integer>{

        List<Questions> findQuestionsByDatasetQuestion_Id(long id);

        List<Questions> findQuestionsByQuestionSetId(String id);

        Questions findDistinctById(long id);
}
