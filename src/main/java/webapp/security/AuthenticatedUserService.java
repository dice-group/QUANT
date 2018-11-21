package webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import webapp.Repository.UserRepository;
import webapp.model.User;

import javax.transaction.Transactional;

@Service
public class AuthenticatedUserService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            throw new UsernameNotFoundException("The user " + email + " does not exist");
        }
        AuthenticatedUser aUser= new AuthenticatedUser(user);
        return new AuthenticatedUser(aUser);
    }
}
