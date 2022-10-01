package toy.ktx.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.time.LocalDateTime;

@Controller
public class HomeController {

    @GetMapping("/")
    public String getHome(Model model){
        model.addAttribute("minDateTime", LocalDateTime.now());
        model.addAttribute("maxDateTime", LocalDateTime.now().plusDays(30));
        return "index";
    }

    @PostMapping("/schedule")
    @ResponseBody
    public String getSchedule() {
        return "200";
    }
}
