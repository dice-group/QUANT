package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import webapp.Repository.UserRepository;
import webapp.model.Role;
import webapp.model.User;

import java.util.List;

@Service
public class UserServiceImpl implements UserService{

    @Autowired
    UserRepository userRepository;

    private BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
    @Override
    public String addUser(String email, String password, String confirmpassword, String role) {
        if(userRepository.findByEmail(email)!=null)
            return "User already exists";
        if(!password.equals(confirmpassword))
            return "Password does not match confirmpassword";
        userRepository.save(new User(email,bCryptPasswordEncoder.encode(password), Role.valueOf(role)));
        return "User successfully added";
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User getByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public void modifyUser(int id, String email, String password, String role) {
        User user = userRepository.getOne(id);
        user.setEmail(email);
        user.setPassword(bCryptPasswordEncoder.encode(password));
        user.setRole(Role.valueOf(role));
    }

    @Override
    public String changeEmail(User user, String newEmail, String password) {
        if(userRepository.findByEmail(newEmail)!=null)
            return "Mail Address already in use";
        if(!bCryptPasswordEncoder.matches(password,user.getPassword()))
            return "wrong Password";
        user.setEmail(newEmail);
        userRepository.save(user);
        return "Email successfully changed";
    }

    @Override
    public String changePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        String encryptedPassword=bCryptPasswordEncoder.encode(oldPassword);
        if(!bCryptPasswordEncoder.matches(oldPassword,user.getPassword()))
            return "wrong Password";
        if (!newPassword.equals(confirmPassword))
            return "new Password does not match confirm password";
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return "Password successfully changed";
    }



    @Override
    public void deactivateUser(String email, String password, String confirmpassword, String role) {

    }

    @Override
    public void activateUser(String email, String password, String confirmpassword, String role) {

    }
}
