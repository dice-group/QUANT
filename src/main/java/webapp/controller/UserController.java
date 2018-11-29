package webapp.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class UserController {

    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView model=new ModelAndView("index");
        return model;
    }

    @RequestMapping("admin")
    public ModelAndView admin(){
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        ModelAndView model=new ModelAndView("admin");
        auth.getPrincipal();
        model.addObject("user",auth.getName());
        return model;

    }

    @RequestMapping("register")
    public ModelAndView addNewUser(){
        ModelAndView model=new ModelAndView("register");
        return model;
    }
    @RequestMapping(value="register", method = RequestMethod.POST)
    public ModelAndView register(@RequestParam("email")String email,@RequestParam("password")String password,
                                 @RequestParam("confirm-password")String passwordConfirm,@RequestParam("role")String role){
        ModelAndView model=new ModelAndView("register");
        return model;
    }
    @RequestMapping(value="/testString", method = RequestMethod.GET)
    public String testString(){
        //ModelAndView model=new ModelAndView("register");
        return "TestString";
    }

}
