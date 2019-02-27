package webapp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.springframework.context.annotation.ComponentScan;


@SpringBootApplication
@ComponentScan(basePackages = {"datahandler", "webapp"})
public class Application {

    //  @Autowired
    // private UserRepository userRepository;

    // private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);

    }
    // @Bean
    //  InitializingBean sendDatabase() {
    //      return () -> {
    //  userRepository.deleteAll();
    //  User u = new User("test@test.com",bCryptPasswordEncoder.encode("password"),Role.ADMIN);
    //  userRepository.save(u);
    //      };
    //  }
}
