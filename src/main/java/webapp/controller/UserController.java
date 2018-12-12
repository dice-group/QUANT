package webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import webapp.services.UserService;


@RestController
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView model=new ModelAndView("/index");
        return model;
    }

    @RequestMapping("/dashboard")
    public ModelAndView admin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model=new ModelAndView("/dashboard");
        auth.getPrincipal();
        model.addObject("user",auth.getName());
        return model;

    }

    @RequestMapping("/register")
    public ModelAndView addNewUser(){
            ModelAndView model=new ModelAndView("register");
            return model;
    }
    @RequestMapping(value="/register", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam("email")String email,@RequestParam("password")String password,
                                 @RequestParam("confirm-password")String confirmPassword,@RequestParam("role")String role){
        String message=userService.addUser(email,password,confirmPassword,role);
        ModelAndView model=new ModelAndView("/register");
        if(!"User successfully added".equals(message))
            model.addObject("errorMessage",message);
        else model.addObject("successMessage",message);
        return model;
    }

    @RequestMapping("/changePassword")
    public ModelAndView modifyPassword(){
        ModelAndView model=new ModelAndView("changePassword");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addObject("logedInAs",auth.getName());
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
        return model;
    }
    @RequestMapping("/changeEmail")
    public ModelAndView modifyEmail(){
        ModelAndView model=new ModelAndView("changeEmail");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addObject("logedInAs",auth.getName());
        return model;
    }
    @RequestMapping(value="/changeEmail", method = RequestMethod.POST)
    public ModelAndView changeEmail(@RequestParam("new-email")String newEmail,@RequestParam("password")String password){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.changeEmail(userService.getByEmail(auth.getName()),newEmail,password);
        ModelAndView model=new ModelAndView("/register");
        if(!"Email successfully changed".equals(message))
            model.addObject("errorMessage",message);
        else model.addObject("successMessage",message);
        return model;
    }

    @RequestMapping(value="/userlist",method = RequestMethod.GET)
    public ModelAndView userList(){

        ModelAndView model = new ModelAndView("/userlist");
        model.addObject("Users",userService.getAllUsers());
        return model;
    }
    @RequestMapping(value="/editUser", method = RequestMethod.POST)
    public ModelAndView editUser(@RequestParam("user-name")String username,@RequestParam("id-input")String id,
                                       @RequestParam("role")String role,@RequestParam("admin-password")String adminPassword){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.modifyUser(userService.getByEmail(auth.getName()),Integer.parseInt(id),username,adminPassword,role);
        ModelAndView model=new ModelAndView("redirect:/userlist");
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
