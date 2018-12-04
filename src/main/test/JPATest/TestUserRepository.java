package JPATest;

import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import webapp.Application;
import webapp.Repository.UserRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import webapp.model.Role;
import webapp.model.User;

import java.util.List;


@RunWith(SpringRunner.class)
@DataJpaTest
@SpringBootTest(classes = Application.class)
public class TestUserRepository {
    @Autowired
    private UserRepository userRepository;
    BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
    @Test
    public void creationTest(){
        User u = new User("test@test.de",bCryptPasswordEncoder.encode("password"), Role.ADMIN);
        userRepository.save(u);
        List<User> users=userRepository.findAll();
        for(User user:users)
            System.out.println(user.getEmail());
    }
}
