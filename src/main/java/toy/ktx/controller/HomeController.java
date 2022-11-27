package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.service.ReservationService;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ReservationService reservationService;

    @GetMapping("/")
    public String getHome(Model model,
                          @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                          @ModelAttribute ScheduleForm scheduleForm){

        scheduleForm.setDateOfGoing(LocalDate.now().toString());

        model.addAttribute("minDateTime", LocalDateTime.now());
        model.addAttribute("maxDateTime", LocalDateTime.now().plusDays(30));

        if(member == null) {
            model.addAttribute("notLogin", true);
            return "index";
        }

        model.addAttribute("login", true);
        return "index";
    }

    @GetMapping("my-page")
    public String getMyPage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            Model model) {

        List<Reservation> reservations = reservationService.findByMember(member);
        List<Deploy> deploys = new ArrayList<>();

        for (Reservation reservation : reservations) {
            Deploy deploy = reservation.getDeploy();
            //프록시가 튀어 나옴
            log.info("시발 ={}", deploy.getClass());
            deploys.add(deploy);
        }

        List<String> durations = getDuration(deploys);

        if (reservations.isEmpty() != true) {
            model.addAttribute("reservations", reservations);
            model.addAttribute("durations", durations);
            log.info("시발 ={}", durations);
            log.info("시발 ={}", reservations);
        }

        if(member.getAuthorizations() == Authorizations.ADMIN) {
            model.addAttribute("member", member);
            return "mypage/adminMYPage";
        }

        model.addAttribute("member", member);
        return "mypage/userMyPage";
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


