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
import toy.ktx.domain.Reservation;
import toy.ktx.domain.comparator.DeployComparator;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.constant.TrainNameConst;
import toy.ktx.domain.dto.CreateDeployForm;
import toy.ktx.domain.dto.DeploySearchDto;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.repository.KtxRepository;
import toy.ktx.service.*;

import javax.persistence.EntityManager;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final KtxRepository ktxRepository;
    private final EntityManager em;

    private final ReservationService reservationService;
    private final KtxRoomService ktxRoomService;
    private final MugunghwaRoomService mugunghwaRoomService;
    private final SaemaulRoomService saemaulRoomService;
    private final KtxSeatService ktxSeatService;
    private final MugunghwaSeatService mugunghwaSeatService;
    private final SaemaulSeatService saemaulSeatService;
    private final KtxService ktxService;
    private final MugunghwaService mugunghwaService;
    private final SaemaulService saemaulService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;
    private final DeployService deployService;

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

    @GetMapping("/my-page")
    public String getMyPage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            Model model) {

        //userPage 진입
        if (member.getAuthorizations().equals(Authorizations.USER)) {
            List<Reservation> reservations = reservationService.findByMember(member);
            List<Deploy> deploys = new ArrayList<>();

            for (Reservation reservation : reservations) {
                Deploy deploy = reservation.getDeploy();
                deploys.add(deploy);
            }
            List<String> durations = getDuration(deploys);

            if (reservations.isEmpty() != true) {
                model.addAttribute("reservations", reservations);
                model.addAttribute("durations", durations);
            }

            model.addAttribute("localDateTime", LocalDateTime.now());
            model.addAttribute("member", member);
            return "mypage/userMyPage";
        }

        //adminPage 진입
        model.addAttribute("member", member);
        model.addAttribute("createDeployForm", new CreateDeployForm());
        model.addAttribute("deploySearchDto", new DeploySearchDto());

        List<Deploy> deployList = deployService.getDeploysToTrain();
        Collections.sort(deployList, new DeployComparator());
        List<String> durations = getDuration(deployList);

        model.addAttribute("deployList", deployList);
        model.addAttribute("durations", durations);
        return "mypage/adminMyPage";
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


