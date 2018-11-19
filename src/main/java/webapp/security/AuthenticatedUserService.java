package webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import webapp.Repository.UserRepository;
import webapp.model.User;
@Service
public class AuthenticatedUserService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByEmail(username);
        if (user == null) {
            throw new UsernameNotFoundException("The user " + username + " does not exist");
        }
        return new AuthenticatedUser(new AuthenticatedUser(user));
    }
}
