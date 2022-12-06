package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Train;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.service.DeployService;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {

    private final DeployService deployService;

    @PostMapping("/schedule")
    public String getSchedule(@Valid @ModelAttribute ScheduleForm scheduleForm,
                              BindingResult bindingResult,
                              @ModelAttribute PassengerDto passengerDto,
                              @ModelAttribute DeployForm deployForm,
                              Model model,
                              @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        LocalDateTime after = null;
        LocalDateTime before = null;
        Long total = Long.valueOf(scheduleForm.getTotal());

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
        model.addAttribute("before", before); //출발하는 날
        model.addAttribute("after", after); //오는 날

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

        List<Deploy> deploysWhenGoing = deployService.searchDeployWithTrain(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);

        if(deploysWhenGoing.isEmpty() == true) {
            model.addAttribute("emptyWhenGoing", true);
            return "schedule";
        }

        model.addAttribute("deploysWhenGoing", deploysWhenGoing);

        List<List<Boolean>> fullCheck = new ArrayList<>();

        for (Deploy deploy : deploysWhenGoing) {
            Ktx train = (Ktx)deploy.getTrain();
            List<KtxRoom> ktxRooms = train.getKtxRooms();

            Long normalRemain = Long.valueOf(0);
            Long vipRemain = Long.valueOf(0);

            for (KtxRoom ktxRoom : ktxRooms) {
                if (ktxRoom.getGrade() == Grade.NORMAL) {
                    normalRemain += ktxRoom.howManyRemain();
                }
                else {
                    vipRemain += ktxRoom.howManyRemain();
                }
            }

            log.info("시발 = {}", normalRemain);
            log.info("시발 = {}", vipRemain);

            List<Boolean> check = new ArrayList<>();
            if (normalRemain >= total && vipRemain >= total) {
                check.add(true);
                check.add(true);
            } else if (normalRemain >= total) {
                check.add(true);
                check.add(false);
            } else if (vipRemain >= total) {
                check.add(false);
                check.add(true);
            } else {
                check.add(false);
                check.add(false);
            }
            fullCheck.add(check);
        }
        model.addAttribute("fullCheck", fullCheck);
        log.info("시발 ={}", fullCheck);

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
