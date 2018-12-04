package webapp.services;


import webapp.model.User;

import java.util.List;

public interface UserService {

    String addUser(String email, String password, String confirmpassword, String role);

    List<User>getAllUsers();

    User getByEmail(String email);

    void modifyUser(int id,String email, String password, String role);

    String changeEmail(User user,String newEmail, String password);

    String changePassword(User user, String oldPassword, String newPassword, String confirmPassword);


    void deactivateUser(String email, String password, String confirmpassword, String role);

    void activateUser(String email, String password, String confirmpassword, String role);
}
