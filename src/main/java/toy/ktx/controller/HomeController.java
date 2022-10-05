package toy.ktx.controller;

import com.sun.xml.bind.v2.TODO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import toy.ktx.domain.Member;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.dto.ScheduleForm;

import javax.validation.Valid;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;

@Controller
@Slf4j
public class HomeController {

    @GetMapping("/")
    public String getHome(Model model,
                          @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                          @ModelAttribute ScheduleForm scheduleForm){

        scheduleForm.setDepartureDate(LocalDate.now().toString());

        model.addAttribute("minDateTime", LocalDateTime.now());
        model.addAttribute("maxDateTime", LocalDateTime.now().plusDays(30));

        if(member == null) {
            model.addAttribute("notLogin", true);
            return "index";
        }

        model.addAttribute("login", true);
        return "index";
    }

    @PostMapping("/schedule")
    public String getSchedule(@Valid @ModelAttribute ScheduleForm scheduleForm,
                              BindingResult bindingResult,
                              Model model,
                              @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        if(scheduleForm.getRound() == true && !StringUtils.hasText(scheduleForm.getArrivalDate())) {
            bindingResult.reject("noArrivalDate", null);
        }

        //TODO 출발 날짜가 도착 날짜보다 느린 object Error를 추가해줘야 됨
        //TODO 그리고 체크박스 스위치 재렌더링 할 때 고장나는 거 손봐야 됨

        if(scheduleForm.getToddler() == null && scheduleForm.getKids() == null && scheduleForm.getAdult() == null && scheduleForm.getSenior() == null) {
            bindingResult.reject("passenger", null);
        }

        if(!Arrays.asList(StationsConst.stations).contains(scheduleForm.getDeparturePlace())
            || !Arrays.asList(StationsConst.stations).contains(scheduleForm.getArrivalPlace())) {
            bindingResult.reject("noStation", null);
        }

        if(bindingResult.hasErrors()) {
            if(member == null) {
                model.addAttribute("notLogin", true);
                return "index";
            }
            model.addAttribute("login", true);
            return "index";
        }

        return "schedule";
    }

    @GetMapping("/reservation")
    @ResponseBody
    public String doReservation() {
        return "200";
    }
}
