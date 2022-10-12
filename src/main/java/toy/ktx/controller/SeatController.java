package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.java.Log;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.service.DeployService;
import toy.ktx.service.KtxRoomService;
import toy.ktx.service.KtxSeatService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SeatController {

    private final KtxRoomService ktxRoomService;
    private final KtxSeatService ktxSeatService;
    private final DeployService deployService;

    @PostMapping("/seat")
    public String chooseSeat(@ModelAttribute DeployForm deployForm,
                             @RequestParam(required = false) String prevGoing,
                             @RequestParam(required = false) String nextGoing,
                             @RequestParam(required = false) String prevComing,
                             @RequestParam(required = false) String nextComing,
                             @RequestParam(required = false) String dateTimeOfGoing,
                             @RequestParam(required = false) String dateTimeOfLeaving,
                             @RequestParam(required = false) String departurePlace,
                             @RequestParam(required = false) String arrivalPlace,
                             @RequestParam(required = false) Boolean round,
                             Model model) {

        log.info("시발={}", prevGoing);
        log.info("시발={}", nextGoing);
        log.info("시발={}", prevComing);
        log.info("시발={}", nextComing);
        log.info("시발={}", dateTimeOfGoing);
        log.info("시발={}", dateTimeOfLeaving);
        log.info("시발={}", departurePlace);
        log.info("시발={}", arrivalPlace);
        log.info("시발={}", round);

        model.addAttribute("departurePlace", departurePlace);
        model.addAttribute("arrivalPlace", arrivalPlace);
        model.addAttribute("round", round);

        boolean seat;

        if(prevGoing == null && nextGoing == null && prevComing == null && nextComing == null){
            seat = true;
        }

        else {
            seat = false;
        }

        if(round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            List<Deploy> deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, beforeDateTime);
            //오는 날에는 가는 날의 출발지가 도착지고 도착지가 출발지임 따라서 getArrivalPlace가 departurePlace(출발지)에 위치해야 됨
            List<Deploy> deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, afterDateTime);

            if(deploysWhenGoing.isEmpty() == true || deploysWhenComing.isEmpty() == true) {
                //TODO 여기를 잘못 짬 이미 deploys 목록 갱신했는데 그걸 가지고 다시 분기하고 있음
                if(prevGoing != null) {
                    LocalDateTime newTime = beforeDateTime.minusDays(1);
                    model.addAttribute("before", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                }

                if(nextGoing != null) {
                    LocalDateTime newTime = beforeDateTime.plusDays(1);
                    model.addAttribute("before", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                }

                if(prevComing != null) {
                    LocalDateTime newTime = afterDateTime.minusDays(1);
                    model.addAttribute("after", newTime);


                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                }

                if(nextComing != null) {
                    LocalDateTime newTime = afterDateTime.plusDays(1);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                }

                if(deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true && seat == true) {
                    return "chooseSeat";
                }

                if(deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true && seat == false) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);
                    return "schedule";
                }

                if(deploysWhenGoing.isEmpty() == true && seat == true) {
                    return "chooseSeat";
                }

                if(deploysWhenGoing.isEmpty() == true && seat == false) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                    return "schedule";
                }

                if(deploysWhenComing.isEmpty() == true && seat == true) {
                    return "chooseSeat";
                }

                if(deploysWhenComing.isEmpty() == true && seat == false) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));

                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                    return "schedule";
                }
            }

            if(deploysWhenGoing.isEmpty() == false && deploysWhenComing.isEmpty() == false) {
                if(prevGoing != null) {
                    LocalDateTime newTime = beforeDateTime.minusDays(1);
                    model.addAttribute("before", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);

                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);

                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                    return "schedule";
                }

                if(nextGoing != null) {
                    LocalDateTime newTime = beforeDateTime.plusDays(1);
                    model.addAttribute("before", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);

                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);

                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                    return "schedule";
                }

                if(prevComing != null) {
                    LocalDateTime newTime = afterDateTime.minusDays(1);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);

                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());

                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                    return "schedule";
                }

                if(nextComing != null) {
                    LocalDateTime newTime = afterDateTime.plusDays(1);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);

                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());

                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                    return "schedule";
                }
                return "chooseSeat";
            }
        }
// --------------------------------------------------------------------------------------------------------------------------
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

        if(prevGoing != null) {
            LocalDateTime newTime = beforeDateTime.minusDays(1);
            model.addAttribute("before", newTime);

            int year = newTime.getYear();
            int monthValue = newTime.getMonthValue();
            int dayOfMonth = newTime.getDayOfMonth();

            LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
            List<Deploy> deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);

            if(deploysWhenGoing.isEmpty() == true) {
                model.addAttribute("emptyWhenGoing", true);

                model.addAttribute("dateTimeOfGoing", dateTime.toString());
                model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                return "schedule";
            }

            model.addAttribute("deploysWhenGoing", deploysWhenGoing);
            model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));

            model.addAttribute("dateTimeOfGoing", dateTime.toString());
            model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);

            deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
            return "schedule";
        }

        if(nextGoing != null) {
            LocalDateTime newTime = beforeDateTime.plusDays(1);
            model.addAttribute("before", newTime);

            int year = newTime.getYear();
            int monthValue = newTime.getMonthValue();
            int dayOfMonth = newTime.getDayOfMonth();

            LocalDateTime dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
            List<Deploy> deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);

            if(deploysWhenGoing.isEmpty() == true) {
                model.addAttribute("emptyWhenGoing", true);

                model.addAttribute("dateTimeOfGoing", dateTime.toString());
                model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                return "schedule";
            }

            model.addAttribute("deploysWhenGoing", deploysWhenGoing);
            model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));

            model.addAttribute("dateTimeOfGoing", dateTime.toString());
            model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);

            deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
            return "schedule";
        }
        return "chooseSeat";
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

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }
}
