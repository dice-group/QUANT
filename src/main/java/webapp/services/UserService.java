package webapp.services;


import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import webapp.model.User;

import java.util.List;

public interface UserService {

    String addUser(String email, String password, String confirmpassword, String role);

    List<User>getAllUsers();

    User getByEmail(String email);

    String modifyUser(Authentication admin, int id, String email, String adminPassword, String role);

    String modifyUserPassword(User admin, int id, String newPassword, String confirmNewPassword, String adminPassword);

    String changeEmail(User user,String newEmail, String password);

    String changePassword(User user, String oldPassword, String newPassword, String confirmPassword);


    ResponseEntity<?> deactivateUser(User user, User principal);

    ResponseEntity<?> activateUser(User user);
}
