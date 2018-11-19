package webapp;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;

@RestController
public class TestController {
    @RequestMapping("/")
    public ModelAndView index() {
        ModelAndView model=new ModelAndView("index");
        model.addObject("message","view Message");
        return model;
    }
    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ModelAndView login() {
        ModelAndView model=new ModelAndView("login");
        return model;
    }
}
