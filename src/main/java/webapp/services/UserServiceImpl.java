package webapp.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import webapp.repository.UserRepository;
import webapp.model.Role;
import webapp.model.User;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    public String modifyUser(Authentication admin, int id, String email, String adminPassword, String role) {
        User adminUser =userRepository.findByEmail(admin.getName());
        if(!bCryptPasswordEncoder.matches(adminPassword,adminUser.getPassword()))
            return "-1";
        User user = userRepository.getOne(id);
        if (user.getId()==adminUser.getId())
            admin.setAuthenticated(false);
        user.setEmail(email);
        user.setRole(Role.valueOf(role));
        userRepository.save(user);
        return "1";
    }

    @Override
    public String modifyUserPassword(User admin, int id, String newPassword, String confirmNewPassword, String adminPassword) {
        if(!bCryptPasswordEncoder.matches(adminPassword,admin.getPassword()))
            return "-1";
        if(!newPassword.equals(confirmNewPassword))
            return "-2";
        User user = userRepository.getOne(id);
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return "1";
    }

    @Override
    public String changeEmail(User user, String newEmail, String password) {
        if(userRepository.findByEmail(newEmail)!=null)
            return "-2";
        if(!bCryptPasswordEncoder.matches(password,user.getPassword()))
            return "-1";
        user.setEmail(newEmail);
        userRepository.save(user);
        return "1";
    }

    @Override
    public String changePassword(User user, String oldPassword, String newPassword, String confirmPassword) {
        if(!bCryptPasswordEncoder.matches(oldPassword,user.getPassword()))
            return "-1";
        if (!newPassword.equals(confirmPassword))
            return "-2";
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);
        return "1";
    }



    @Override
    public ResponseEntity<?> deactivateUser(User user, User principal) {
        if(user.getId()==principal.getId()){
            return new ResponseEntity<>("Self deactivation not allowed",HttpStatus.NOT_MODIFIED);
        }
        user.setActivated(false);
        userRepository.save(user);

        return new ResponseEntity<>("User successfully deactivated",HttpStatus.OK);
    }

    @Override
    public ResponseEntity<?> activateUser(User user) {
        Map<String, Object>result = new HashMap<String, Object>();
        user.setActivated(true);
        userRepository.save(user);
        result.put("result","success");
        result.put("message","User successfully activated");
        return new ResponseEntity<>("User successfully activated",HttpStatus.OK);
    }
}
