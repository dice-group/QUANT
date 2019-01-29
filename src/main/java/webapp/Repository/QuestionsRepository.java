package webapp.Repository;

import webapp.model.Dataset;
import webapp.model.Questions;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface QuestionsRepository extends JpaRepository<Questions,Integer>{

        List<Questions> findQuestionsByDatasetQuestion_Id(long id);

        List<Questions> findByDatasetQuestion_IdAndVersionAndRemoved(long id, int version, boolean removed);

        List<Questions> findQuestionsByQuestionSetId(long id);

        List<Questions> findAll();

        List<Questions> findAllQuestionsByDatasetQuestion_Id(long id);

        List<Questions> findQuestionsByDatasetQuestionIdAndQuestionSetId(long setId, long id);
        Questions findDistinctById(long id);
        Questions findTop1VersionByQuestionSetIdOrderByVersionDesc(long Id);


}
