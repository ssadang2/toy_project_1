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
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ScheduleController {

    private final DeployService deployService;
    private final KtxRoomService ktxRoomService;

    @PostMapping("/schedule")
    public String getSchedule(@Valid @ModelAttribute ScheduleForm scheduleForm,
                              BindingResult bindingResult,
                              @ModelAttribute PassengerDto passengerDto,
                              @ModelAttribute DeployForm deployForm,
                              Model model,
                              @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member) {

        LocalDateTime after = null;
        LocalDateTime before = null;
        //never used
        //Long total = Long.valueOf(scheduleForm.getTotal());

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
            //fetch
            List<Deploy> deploysWhenGoing = deployService.searchDeployWithTrain(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);
            //오는 날에는 가는 날의 출발지가 도착지고 도착지가 출발지임 따라서 getArrivalPlace가 departurePlace(출발지)에 위치해야 됨
            List<Deploy> deploysWhenComing = deployService.searchDeployWithTrain(scheduleForm.getArrivalPlace(), scheduleForm.getDeparturePlace(), after);

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

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);

                    doCheck(deploysWhenComing, ktxRooms, passengerDto, fullCheck);
                    model.addAttribute("fullCheck2", fullCheck);
                    return "schedule";
                }

                if(deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);

                    doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);
                    model.addAttribute("fullCheck", fullCheck);
                    return "schedule";
                }
            }
            //success logic
            if(deploysWhenGoing.isEmpty() == false && deploysWhenComing.isEmpty() == false) {
                model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                model.addAttribute("deploysWhenComing", deploysWhenComing);

                model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                //going
                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                //coming
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);
                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys2);

                //going
                doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);

                //coming
                doCheck(deploysWhenComing, ktxRooms2, passengerDto, fullCheck2);

                model.addAttribute("fullCheck", fullCheck);
                model.addAttribute("fullCheck2", fullCheck2);

                deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
                deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());
                return "schedule";
            }
        }
// Round vs one-way--------------------------------------------------------------------------------------------------------------
//success logic
        //seat을 기준으로 객체 탐색을 하는 건 deploy, reservation 등의 주 테이블에서 탐색해야 된다는 원칙에서 위배됨 -> 자연스럽지 못한 듯
        //왜 deploys를 긁어 올 때 seat까지 같이 긁지 않는 것인가? => 연관관계를 가지는 train이 부모 클래스라서 자식 클래스인 ktx로 객체 탐색이 불가함(내가 하는 방법을 모르는 걸 수도)
        //예상 1 + N = > 1 + N(라고 보기는 애매함 1:1 관계에서의 문제라서) => in 절로 해결하자 => 해결
        //fetch
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
//        }

        List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
        //얘는 반복되는 대로 1차 캐시에서 안 찾고 쿼리를 날림 => Pk 조회가 아니어서 그런 듯
        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);

        doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);
        model.addAttribute("fullCheck", fullCheck);
        model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
        deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());
        return "schedule";
    }

    private void doCheck(List<Deploy> deploysWhen, List<KtxRoom> ktxRooms, PassengerDto passengerDto, List<List<Boolean>> fullCheck) {
        for (Deploy deploy : deploysWhen) {
            //실험중 select 3개에서 2개로 줄임(using in clause)
            List<String> normalReserveOkList = new ArrayList<>();
            List<String> vipReserveOkList = new ArrayList<>();

            for (KtxRoom ktxRoom : ktxRooms) {
                if (ktxRoom.getGrade() == Grade.NORMAL) {
                    KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                    if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                        normalReserveOkList.add(ktxRoom.getRoomName());
                    }
                }
                else {
                    KtxSeatVip ktxSeatVip = (KtxSeatVip) ktxRoom.getKtxSeat();
                    if (ktxSeatVip.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                        vipReserveOkList.add(ktxRoom.getRoomName());
                    }
                }
            }

            log.info("fuck = {}",normalReserveOkList);
            log.info("fuck = {}",vipReserveOkList);

            List<Boolean> check = new ArrayList<>();

            if(!normalReserveOkList.isEmpty() && !vipReserveOkList.isEmpty()) {
                check.add(true);
                check.add(true);
            }

            if(!normalReserveOkList.isEmpty() && vipReserveOkList.isEmpty()) {
                check.add(true);
                check.add(false);
            }

            if(normalReserveOkList.isEmpty() && !vipReserveOkList.isEmpty()) {
                check.add(false);
                check.add(true);
            }

            if (normalReserveOkList.isEmpty() && vipReserveOkList.isEmpty()) {
                check.add(false);
                check.add(false);
            }
            fullCheck.add(check);
        }
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
