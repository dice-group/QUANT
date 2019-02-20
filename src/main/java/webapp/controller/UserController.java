package webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import webapp.model.User;
import webapp.services.UserService;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/signIn")
    public ModelAndView login() {
        ModelAndView model=new ModelAndView("/signIn");
        return model;
    }

    @RequestMapping("/")
    public  ModelAndView index(){
        ModelAndView model=new ModelAndView("redirect:/datasetlist");
        return model;

    }

    @RequestMapping("/register")
    public ModelAndView addNewUser(){
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            ModelAndView model=new ModelAndView("register");
            User user = userService.getByEmail(auth.getName());
            model.addObject("User",user);
            return model;
    }
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam("email")String email,@RequestParam("password")String password,
                                 @RequestParam("confirm-password")String confirmPassword,@RequestParam("role")String role){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.addUser(email,password,confirmPassword,role);
        ModelAndView model=new ModelAndView("/register");
        if(!"User successfully added".equals(message))
            model.addObject("errorMessage",message);
        else model.addObject("successMessage",message);
        auth.getPrincipal();
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        return model;
    }

    @RequestMapping("/changePassword")
    public ModelAndView modifyPassword(){
        ModelAndView model=new ModelAndView("changePassword");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addObject("logedInAs",auth.getName());
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        return model;
    }
    @RequestMapping(value="/changePassword", method = RequestMethod.POST)
    public ModelAndView changePassword(@RequestParam("old-password")String oldPassword,@RequestParam("new-password")String newPassword,
                                 @RequestParam("confirm-password")String confirmPassword){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.changePassword(userService.getByEmail(auth.getName()),oldPassword,newPassword,confirmPassword);
        ModelAndView model=new ModelAndView("/register");
        if(!"Password successfully changed".equals(message))
            model.addObject("errorMessage",message);
        else model.addObject("successMessage",message);
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        return model;
    }
    @RequestMapping("/changeEmail")
    public ModelAndView modifyEmail(){
        ModelAndView model=new ModelAndView("changeEmail");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addObject("logedInAs",auth.getName());User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);

        return model;
    }
    @RequestMapping(value="/changeEmail", method = RequestMethod.POST)
    public ModelAndView changeEmail(@RequestParam("new-email")String newEmail,@RequestParam("password")String password){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.changeEmail(userService.getByEmail(auth.getName()),newEmail,password);
        ModelAndView model=new ModelAndView("/register");
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        if(!"Email successfully changed".equals(message))
            model.addObject("errorMessage",message);
        else model.addObject("successMessage",message);
        return model;
    }

    @RequestMapping(value="/userlist",method = RequestMethod.GET)
    public ModelAndView userList(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model = new ModelAndView("/userlist");
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        model.addObject("Users",userService.getAllUsers());
        return model;
    }
    @RequestMapping(value="/editUser", method = RequestMethod.POST)
    public ModelAndView editUser(@RequestParam("user-name")String username,@RequestParam("id-input")String id,
                                       @RequestParam("role")String role,@RequestParam("admin-password")String adminPassword){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.modifyUser(userService.getByEmail(auth.getName()),Integer.parseInt(id),username,adminPassword,role);
        ModelAndView model=new ModelAndView("redirect:/userlist");
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        model.addObject("Users",userService.getAllUsers());
        model.addObject("message",message);
        return(model);

    }
    @RequestMapping(value="/resetPassword", method = RequestMethod.POST)
    public ModelAndView resetPassword(@RequestParam("id-input")String id,@RequestParam("new-password")String newPassword,@RequestParam("confirm-new-password")String confirmNewPassword,
                                      @RequestParam("admin-password")String adminPassword){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.modifyUserPassword(userService.getByEmail(auth.getName()),Integer.parseInt(id),newPassword,confirmNewPassword,adminPassword);
        ModelAndView model=new ModelAndView("redirect:/userlist");
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        model.addObject("Users",userService.getAllUsers());
        model.addObject("message",message);
        return(model);

    }
    @RequestMapping(value="/setActivation", method = RequestMethod.POST)
    public @ResponseBody ResponseEntity<?> setActivated(@RequestParam("name") String name, @RequestParam("action") String action){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ResponseEntity<?> result;

        if("activate".equals(action)) {
            result = userService.activateUser(userService.getByEmail(name));
        }
        else result = userService.deactivateUser(userService.getByEmail(name), userService.getByEmail(auth.getName()));
        return result;
    }


}
