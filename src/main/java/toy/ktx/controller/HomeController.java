package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.Train;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.service.KtxSeatService;
import toy.ktx.service.ReservationService;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ReservationService reservationService;
    private final KtxSeatService ktxSeatService;

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

    @PostMapping("my-page")
    public String cancelReservation(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            @RequestParam(required = false) Long reservationId,
                            Model model) {

        // 예약 삭제 로직
        if (reservationId != null) {
            Optional<Reservation> foundReservation = reservationService.getReservationWithFetch(reservationId);
            if (foundReservation.isPresent()) {
                Reservation reservation = foundReservation.get();
                log.info("시발 ={}", reservation);
                Ktx train = (Ktx) reservation.getDeploy().getTrain();
                List<KtxRoom> ktxRooms = train.getKtxRooms();

                String roomName = reservation.getRoomName();
                Optional<KtxRoom> ktxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                KtxSeat ktxSeat = ktxRoom.get().getKtxSeat();
                //reservation 등의 entity 뿐만 아니라 seat entity 안의 자리까지 체크 해제해줘야 됨
                ktxSeatService.updateSeatsWithReflection(ktxSeat, reservation.getSeats());
            }

            //cascade option을 켰기 때문에 passenger를 굳이 손으로 안 지워줘도 됨
            reservationService.deleteById(reservationId);
        }

//        List<Reservation> reservations = reservationService.findByMember(member);
//        List<Deploy> deploys = new ArrayList<>();
//
//        for (Reservation reservation : reservations) {
//            Deploy deploy = reservation.getDeploy();
//            deploys.add(deploy);
//        }
//
//        List<String> durations = getDuration(deploys);
//
//        if (reservations.isEmpty() != true) {
//            model.addAttribute("reservations", reservations);
//            model.addAttribute("durations", durations);
//            log.info("시발 ={}", durations);
//            log.info("시발 ={}", reservations);
//        }

        //prg
        return "redirect:/my-page";
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


