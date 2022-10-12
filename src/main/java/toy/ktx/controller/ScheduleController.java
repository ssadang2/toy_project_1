package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.service.DeployService;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {

    private final DeployService deployService;

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
            String dateTimeOfGoing = scheduleForm.getDateOfGoing() + "T" + scheduleForm.getTimeOfGoing();
            model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
            before = getLocalDateTime(dateTimeOfGoing);
        }

        if(before.isBefore(LocalDateTime.now()) == true && before.getHour() != LocalDateTime.now().getHour()) {
            bindingResult.reject("late", null);
        }

        if(scheduleForm.getRound() == true && scheduleForm.getDateOfLeaving() != "") {
            String dateTimeOfLeaving = scheduleForm.getDateOfLeaving() + "T" + scheduleForm.getTimeOfLeaving();
            model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
            after = getLocalDateTime(dateTimeOfLeaving);
        }

        if(scheduleForm.getRound() == true && after != null && before.isAfter(after)) {
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

        model.addAttribute("departurePlace", scheduleForm.getDeparturePlace());
        model.addAttribute("arrivalPlace", scheduleForm.getArrivalPlace());
        model.addAttribute("round", scheduleForm.getRound());
        model.addAttribute("before", before);
        model.addAttribute("after", after);


        if(scheduleForm.getRound() == true) {
            List<Deploy> deploysWhenGoing = deployService.searchDeploy(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);
            //오는 날에는 가는 날의 출발지가 도착지고 도착지가 출발지임 따라서 getArrivalPlace가 departurePlace(출발지)에 위치해야 됨
            List<Deploy> deploysWhenComing = deployService.searchDeploy(scheduleForm.getArrivalPlace(), scheduleForm.getDeparturePlace(), after);

            if(deploysWhenGoing.isEmpty() == true || deploysWhenComing.isEmpty() == true) {
                if(deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);
                    return "schedule";
                }

                if(deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                    return "schedule";
                }

                if(deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                    return "schedule";
                }
            }

            if(deploysWhenGoing.isEmpty() == false && deploysWhenComing.isEmpty() == false) {
                model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                model.addAttribute("deploysWhenComing", deploysWhenComing);

                model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());

                return "schedule";
            }
        }

        List<Deploy> deploysWhenGoing = deployService.searchDeploy(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);

        if(deploysWhenGoing.isEmpty() == true) {
            model.addAttribute("emptyWhenGoing", true);
            return "schedule";
        }

        model.addAttribute("deploysWhenGoing", deploysWhenGoing);
        model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
        deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
        return "schedule";
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }

    private List<String> getDuration(List<Deploy> deploys) {
        List<String> durations = new ArrayList<>();
        for (Deploy deploy : deploys) {
            LocalDateTime departureTime = deploy.getDepartureTime();
            LocalDateTime arrivalTime = deploy.getArrivalTime();

            Duration duration = Duration.between(departureTime, arrivalTime);

            long toMinute = duration.getSeconds() / Long.valueOf(60);
            long hour = toMinute / Long.valueOf(60);
            long minute = toMinute % Long.valueOf(60);

            durations.add(hour + "시간 " + minute + "분");
        }
        return durations;
    }
}
