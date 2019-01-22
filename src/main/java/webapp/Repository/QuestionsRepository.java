package webapp.Repository;

import webapp.model.Dataset;
import webapp.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions,Integer>{

        List<Questions> findQuestionsByDatasetQuestion_Id(long id);

        List<Questions> findQuestionsByQuestionSetId(String id);

        List<Questions> findAll();

        List<Questions> findAllQuestionsByDatasetQuestion_Id(long id);

        List<Questions> findQuestionsByDatasetQuestionIdAndQuestionSetId(long setId, String id);
        Questions findDistinctById(long id);

}
