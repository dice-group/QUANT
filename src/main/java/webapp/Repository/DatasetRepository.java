package webapp.Repository;

import webapp.model.Dataset;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DatasetRepository extends JpaRepository<Dataset,Integer>{


}
