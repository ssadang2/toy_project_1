package toy.ktx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome(Model model, HttpServletRequest request){
        model.addAttribute("minDateTime", LocalDateTime.now());
        model.addAttribute("maxDateTime", LocalDateTime.now().plusDays(30));

        if(request.getSession(false) == null) {
            model.addAttribute("notLogin", true);
            return "index";
        }
        model.addAttribute("login", true);
        return "index";
    }

    @PostMapping("/schedule")
    @ResponseBody
    public String getSchedule() {
        return "200";
    }
}
