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
import toy.ktx.domain.ktx.*;
import toy.ktx.service.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {

    private final DeployService deployService;
    private final KtxRoomService ktxRoomService;
    private final KtxSeatService ktxSeatService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;

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
        // success logic--------------------------------------------------------------------------------------------------------------
        //열차까지 fetch join
        List<Deploy> deploysWhenGoing = deployService.searchDeployWithTrain(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);

        if(deploysWhenGoing.isEmpty() == true) {
            model.addAttribute("emptyWhenGoing", true);
            return "schedule";
        }

        model.addAttribute("deploysWhenGoing", deploysWhenGoing);

        List<List<Boolean>> fullCheck = new ArrayList<>();

//        for (Deploy deploy : deploysWhenGoing) {
            // oneToOne 때문에 발생하는 문제 해결하기 위해 먼저 초기화 N + 1 문제라고 보기는 애매한 듯?
            // select query가 50번 나가던 걸 5번으로 줄임 => 1/10
            // 필기에는 트랜잭션 하나에 영속성 컨텍스트 하나가 대응되는 그렇다면 multiple services가 같은 Tx를 쓰는 건가?
            // oneToOne query 많이 나가는 거 막으려고 미리 당기는 작업
//            ktxSeatService.findKtxSeatWithKtxRoomWithTrainWithDeploy(deploy.getId());

//            List<KtxSeatNormal> a = ktxSeatNormalService.findKtxSeatNormalWithDeployIdFetch(deploy.getId());
//            List<KtxSeatVip> b = ktxSeatVipService.findKtxSeatVipWithDeployIdFetch(deploy.getId());

//        }


        //예상 쿼리 2개?
        for (Deploy deploy : deploysWhenGoing) {
            Ktx train = (Ktx) deploy.getTrain();
            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomWithSeatFetch(train.getId());
            log.info("fuck??? = {}", ktxRooms);
            log.info("fuck??? = {}", deploy);

            List<String> normalReserveOkList = new ArrayList<>();
            List<String> vipReserveOkList = new ArrayList<>();

            for (KtxRoom ktxRoom : ktxRooms) {
                if (ktxRoom.getGrade() == Grade.NORMAL) {
                    KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                    log.info("fuck = {}", ktxSeatNormal);
                    if (ktxSeatNormal.howManyRemain(passengerDto.howManyOccupied()) != null) {
                        normalReserveOkList.add(ktxRoom.getRoomName());
                    }
                }
                else {
                    KtxSeatVip ktxSeatVip = (KtxSeatVip) ktxRoom.getKtxSeat();
                    if (ktxSeatVip.howManyRemain(passengerDto.howManyOccupied()) != null) {
                        vipReserveOkList.add(ktxRoom.getRoomName());
                    }
                }
            }
            log.info("fuck ={}",normalReserveOkList);
            log.info("fuck ={}",vipReserveOkList);

//            for (KtxSeat ktxSeat : ktxSeats) {
//                if (ktxSeat.getKtxRoom().getGrade() == Grade.NORMAL) {
//                    KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxSeat;
//                    if (ktxSeatNormal.howManyRemain(passengerDto.howManyOccupied()) != null) {
//                        normalReserveOkList.add(ktxSeat.getKtxRoom().getRoomName());
//                    }
//                }
//                else {
//                    KtxSeatVip ktxSeatVip = (KtxSeatVip) ktxSeat;
//                    if (ktxSeatVip.howManyRemain(passengerDto.howManyOccupied()) != null) {
//                        vipReserveOkList.add(ktxSeat.getKtxRoom().getRoomName());
//                    }
//                }
//            }

            log.info("fuck = {}",normalReserveOkList);
            log.info("fuck = {}",vipReserveOkList);

            List<Boolean> check = new ArrayList<>();

            if(!normalReserveOkList.isEmpty() && !vipReserveOkList.isEmpty()) {
                log.info("여기1?");
                check.add(true);
                check.add(true);
            }

            if(!normalReserveOkList.isEmpty() && vipReserveOkList.isEmpty()) {
                log.info("여기2?");
                check.add(true);
                check.add(false);
            }

            if(normalReserveOkList.isEmpty() && !vipReserveOkList.isEmpty()) {
                log.info("여기3?");
                check.add(false);
                check.add(true);
            }

            if (normalReserveOkList.isEmpty() && vipReserveOkList.isEmpty()) {
                log.info("여기4?");
                check.add(false);
                check.add(false);
            }
            fullCheck.add(check);
        }
        log.info("fuck ={}",fullCheck);
        model.addAttribute("fullCheck", fullCheck);
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
