package webapp.Repository;

import webapp.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Component;

import java.util.List;
public interface UserRepository extends JpaRepository<User,Integer>{

        User findByEmail(String lastName);

}
