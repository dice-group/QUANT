package webapp.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import webapp.repository.UserRepository;
import webapp.model.User;


@Service
public class AuthenticatedUserService implements UserDetailsService {


    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email);
        if (user == null||!user.isActivated()) {
            throw new UsernameNotFoundException("The user " + email + " does not exist or is deactivated");
        }
        AuthenticatedUser aUser= new AuthenticatedUser(user);
        return new AuthenticatedUser(aUser);
    }
}
