package toy.ktx.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Member;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.ScheduleForm;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;

@Controller
@Slf4j
public class ScheduleController {

    @PostMapping("/schedule")
    public String getSchedule(@Valid @ModelAttribute ScheduleForm scheduleForm,
                              BindingResult bindingResult,
                              @ModelAttribute DeployForm deployForm,
                              Model model,
                              @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        LocalDateTime after = null;
        LocalDateTime before = null;

        if(!StringUtils.hasText(scheduleForm.getDateOfGoing())) {
            bindingResult.reject("noDepartureDate", null);
        }

        if(scheduleForm.getRound() == true && !StringUtils.hasText(scheduleForm.getDateOfLeaving())) {
            bindingResult.reject("noArrivalDate", null);
        }

        if(scheduleForm.getDateOfGoing() != "") {
            String dateTimeOfGoing = scheduleForm.getDateOfGoing() + " " + scheduleForm.getTimeOfGoing();
            before = getLocalDateTime(dateTimeOfGoing);
        }

        if(before.isBefore(LocalDateTime.now()) == true && before.getHour() != LocalDateTime.now().getHour()) {
            bindingResult.reject("late", null);
        }

        if(scheduleForm.getRound() == true && scheduleForm.getDateOfLeaving() != "") {
            log.info("아 시발={}", !StringUtils.hasText(scheduleForm.getDateOfLeaving()));
            String dateTimeOfLeaving = scheduleForm.getDateOfLeaving() + " " + scheduleForm.getTimeOfLeaving();
            after = getLocalDateTime(dateTimeOfLeaving);
        }

        if(scheduleForm.getRound() == true && after != null && before.isAfter(after)) {
            log.info("여기 걸림?");
            bindingResult.reject("leavingIsBeforeGoing", null);
        }

        if(scheduleForm.getToddler() == null && scheduleForm.getKids() == null && scheduleForm.getAdult() == null && scheduleForm.getSenior() == null) {
            bindingResult.reject("passenger", null);
        }

        if(!Arrays.asList(StationsConst.stations).contains(scheduleForm.getDeparturePlace())
                || !Arrays.asList(StationsConst.stations).contains(scheduleForm.getArrivalPlace())) {
            bindingResult.reject("noStation", null);
        }

        if(scheduleForm.getDeparturePlace().equals(scheduleForm.getArrivalPlace())) {
            bindingResult.reject("noSamePlace", null);
        }

        if(bindingResult.hasErrors()) {
            if(member == null) {
                model.addAttribute("notLogin", true);
                scheduleForm.setRound(false);
                return "index";
            }
            model.addAttribute("login", true);
            scheduleForm.setRound(false);
            return "index";
        }

        deployForm.setDeparturePlace(scheduleForm.getDeparturePlace());
        deployForm.setDepartureTime(before);
        deployForm.setArrivalPlace(scheduleForm.getArrivalPlace());
        deployForm.setArrivalTime(null);
        return "schedule";
    }


    @GetMapping("/reservation")
    @ResponseBody
    public String doReservation() {
        return "200";
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        return LocalDateTime.parse(dateTime, formatter);
    }
}
