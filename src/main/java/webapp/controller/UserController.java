package webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import webapp.model.User;
import webapp.services.UserService;


@Controller
public class UserController {
    @Autowired
    UserService userService;

    @RequestMapping("/signIn")
    public ModelAndView login(RedirectAttributes attributes) {
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
    public ModelAndView modifyPassword(RedirectAttributes attributes){
        ModelAndView model=new ModelAndView("changePassword");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addObject("logedInAs",auth.getName());
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        return model;
    }
    @RequestMapping(value="/changePassword", method = RequestMethod.POST)
    public String changePassword(@RequestParam("old-password")String oldPassword,@RequestParam("new-password")String newPassword,
                                 @RequestParam("confirm-password")String confirmPassword, RedirectAttributes attributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.changePassword(userService.getByEmail(auth.getName()),oldPassword,newPassword,confirmPassword);

        if("-1".equals(message)){
            attributes.addFlashAttribute("error", "Wrong old password");
            return "redirect:/changePassword";
        }
        if("-2".equals(message)) {
            attributes.addFlashAttribute("error", "new passwords do not match");
            return "redirect:/changePassword";
        }
        attributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/changePassword";

    }
    @RequestMapping("/changeEmail")
    public ModelAndView modifyEmail(RedirectAttributes attributes){
        ModelAndView model=new ModelAndView("changeEmail");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        model.addObject("logedInAs",auth.getName());User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);

        return model;
    }
    @RequestMapping(value="/changeEmail", method = RequestMethod.POST)
    public String changeEmail(@RequestParam("new-email")String newEmail,@RequestParam("password")String password, RedirectAttributes attributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.changeEmail(userService.getByEmail(auth.getName()),newEmail,password);
        if("-1".equals(message))
        {
            attributes.addFlashAttribute("error", "Wrong password");
            return "redirect:/changeEmail";
        }
        if ("-2".equals(message))
        {
            attributes.addFlashAttribute("error", "Email address already in use.");
            return "redirect:/changeEmail";
        }

        //auth.setAuthenticated(false);
        attributes.addFlashAttribute("success", "Email address changed successfully.");
        return "redirect:/signIn?logout";
    }

    @RequestMapping(value="/userlist",method = RequestMethod.GET)
    public ModelAndView userList(RedirectAttributes attributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model = new ModelAndView("/userlist");
        User user = userService.getByEmail(auth.getName());
        model.addObject("User",user);
        model.addObject("Users",userService.getAllUsers());
        return model;
    }
    @RequestMapping(value="/editUser", method = RequestMethod.POST)
    public String editUser(@RequestParam("user-name")String username,@RequestParam("id-input")String id,
                                       @RequestParam("role")String role,@RequestParam("admin-password")String adminPassword,  RedirectAttributes attributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.modifyUser(auth,Integer.parseInt(id),username,adminPassword,role);
        if ("-1".equals(message))
        {
            attributes.addFlashAttribute("error", "Wrong password");
            return "redirect:/userlist";
        }

        attributes.addFlashAttribute("success", "Successfully changed user details.");
        return "redirect:/userlist";

    }
    @RequestMapping(value="/resetPassword", method = RequestMethod.POST)
    public String resetPassword(@RequestParam("id-input")String id,@RequestParam("new-password")String newPassword,@RequestParam("confirm-new-password")String confirmNewPassword,
                                      @RequestParam("admin-password")String adminPassword, RedirectAttributes attributes){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String message=userService.modifyUserPassword(userService.getByEmail(auth.getName()),Integer.parseInt(id),newPassword,confirmNewPassword,adminPassword);
        if("-1".equals(message)){
            attributes.addFlashAttribute("error", "Wrong admin password");
            return "redirect:/userlist";
        }
        if("-2".equals(message)) {
            attributes.addFlashAttribute("error", "new passwords do not match");
            return "redirect:/userlist";
        }
        attributes.addFlashAttribute("success", "Password changed successfully.");
        return "redirect:/userlist";

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
