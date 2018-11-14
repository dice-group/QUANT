package webapp;

import org.springframework.web.bind.annotation.RequestMapping;
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
}
