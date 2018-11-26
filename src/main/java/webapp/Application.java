package webapp;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

import webapp.Repository.UserRepository;
import webapp.model.Role;
import webapp.model.User;

@SpringBootApplication
public class Application {

    @Autowired
    private UserRepository userRepository;

  //  @Autowired
  // private BCryptPasswordEncoder bCryptPasswordEncoder;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    @Bean
    InitializingBean sendDatabase() {
        return () -> {
           // userRepository.save(new User("test@test.com",bCryptPasswordEncoder.encode("password"),Role.ADMIN));
            userRepository.save(new User("test@test.com","password",Role.ADMIN));
        };
    }
}
