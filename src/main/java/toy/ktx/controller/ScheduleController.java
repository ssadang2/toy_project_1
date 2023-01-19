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
import toy.ktx.domain.Train;
import toy.ktx.domain.comparator.DeployComparator;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
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
    private final KtxService ktxService;
    private final MugunghwaService mugunghwaService;
    private final SaemaulService saemaulService;

    //시간표 정보를 받고 처리 및 validation 하는 컨트롤러
    @PostMapping("/schedule")
    public String getSchedule(@Valid @ModelAttribute ScheduleForm scheduleForm,
                              BindingResult bindingResult,
                              @ModelAttribute DeployForm deployForm,
                              @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                              Model model) {

        LocalDateTime after = null;
        LocalDateTime before = null;

        //passengerDto를 modelAttribute로 받으면 중복으로 검증 logic이 발생하는데 scheduleForm에만 bindingResult가 있기 때문에 에러가 발생
        //해결방법은 model attribute를 쓰지 않고 똑같은 기능을 구현하면 됨
        PassengerDto passengerDto = scheduleForm.getDto();

        if(scheduleForm.getDateOfGoing() != "") {
            String dateTimeOfGoing = scheduleForm.getDateOfGoing() + "T" + scheduleForm.getTimeOfGoing();
            model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
            before = getLocalDateTime(dateTimeOfGoing);

            if(before.isBefore(LocalDateTime.now()) == true && before.getHour() != LocalDateTime.now().getHour()) {
                bindingResult.reject("late", null);
            }
        }

        if(scheduleForm.getRound() == true && scheduleForm.getDateOfComing() != "") {
            String dateTimeOfComing = scheduleForm.getDateOfComing() + "T" + scheduleForm.getTimeOfComing();
            model.addAttribute("dateTimeOfComing", dateTimeOfComing);
            after = getLocalDateTime(dateTimeOfComing);

            if(before.isAfter(after)) {
                bindingResult.reject("leavingIsBeforeGoing", null);
            }
        }

        if(passengerDto.howManyOccupied() > Long.valueOf(9)) {
            bindingResult.reject("tooManyPassengers", null);
        }

        if(!StringUtils.hasText(scheduleForm.getDateOfGoing())) {
            bindingResult.reject("noDepartureDate", null);
        }

        if(scheduleForm.getRound() == true && !StringUtils.hasText(scheduleForm.getDateOfComing())) {
            bindingResult.reject("noArrivalDate", null);
        }

        if(scheduleForm.getToddler() == null && scheduleForm.getKids() == null && scheduleForm.getAdult() == null && scheduleForm.getSenior() == null) {
            bindingResult.reject("passenger", null);
        }

        if(!Arrays.asList(StationsConst.stations).contains(scheduleForm.getDeparturePlace())
                || !Arrays.asList(StationsConst.stations).contains(scheduleForm.getArrivalPlace())) {
            bindingResult.reject("noStation", null);
        }

        if(!scheduleForm.getDeparturePlace().isBlank() && scheduleForm.getDeparturePlace().equals(scheduleForm.getArrivalPlace())) {
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

        model.addAttribute("passengerDto", passengerDto);
        model.addAttribute("departurePlace", scheduleForm.getDeparturePlace());
        model.addAttribute("arrivalPlace", scheduleForm.getArrivalPlace());
        model.addAttribute("round", scheduleForm.getRound());
        model.addAttribute("before", before); //출발하는 날
        model.addAttribute("after", after); //오는 날

        //query 8개 나감 O(8)
        //시간표에서 왕복을 선택했을 때
        if(scheduleForm.getRound() == true) {
            //fetch
            List<Deploy> deploysWhenGoing = deployService.searchDeployToTrain(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);
            //오는 날에는 가는 날의 출발지가 도착지고 도착지가 출발지임 따라서 getArrivalPlace가 departurePlace(출발지)에 위치해야 됨
            List<Deploy> deploysWhenComing = deployService.searchDeployToTrain(scheduleForm.getArrivalPlace(), scheduleForm.getDeparturePlace(), after);

            Collections.sort(deploysWhenGoing, new DeployComparator());
            Collections.sort(deploysWhenComing, new DeployComparator());

            if(deploysWhenGoing.isEmpty() == true || deploysWhenComing.isEmpty() == true) {
                //가는 날, 오는 날 모두 시간표가 없을 때
                if(deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);
                    return "schedule";
                }

                //가는 날만 시간표가 없을 때
                if(deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                    ktxService.getKtxToSeatWithFetchAndIn(deploys);
                    mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                    saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

                    doCheck(deploys, passengerDto, fullCheck);

                    int cntComing = 0;

                    for (List<Boolean> booleans : fullCheck) {
                        if (booleans.contains(Boolean.TRUE)) {
                            deployForm.setDeployIdOfComing(deploysWhenComing.get(cntComing).getId());
                            model.addAttribute("fullCheck2", fullCheck);
                            return "schedule";
                        }
                        cntComing += 1;
                    }

                    model.addAttribute("fullCheck2", fullCheck);
                    model.addAttribute("disableSeatButton", true);
                    return "schedule";
                }

                //오는 날만 시간표가 없을 때
                if(deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                    //미리 당기기
                    ktxService.getKtxToSeatWithFetchAndIn(deploys);
                    mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                    saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

                    doCheck(deploys, passengerDto, fullCheck);

                    int cntGoing = 0;

                    for (List<Boolean> booleans : fullCheck) {
                        if (booleans.contains(Boolean.TRUE)) {
                            deployForm.setDeployIdOfGoing(deploysWhenGoing.get(cntGoing).getId());
                            model.addAttribute("fullCheck", fullCheck);
                            return "schedule";
                        }
                        cntGoing += 1;
                    }
                    model.addAttribute("fullCheck", fullCheck);
                    model.addAttribute("disableSeatButton", true);
                    return "schedule";
                }
            }
            //가는 날, 오는 날 모두 시간표가 있을 때
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

                //미리 당기기
                //미리 당기지 않으면 데이터 개수만큼 쿼리가 나감
                ktxService.getKtxToSeatWithFetchAndIn(deploys);
                mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

                //미리 당기기
                ktxService.getKtxToSeatWithFetchAndIn(deploys2);
                mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys2);
                saemaulService.getSaemaulToSeatWithFetchAndIn(deploys2);

                //going
                doCheck(deploys, passengerDto, fullCheck);

                //coming
                doCheck(deploys2, passengerDto, fullCheck2);

                Boolean noSeatGoing = Boolean.TRUE;
                Boolean noSeatComing = Boolean.TRUE;
                int cntGoing = 0;
                int cntComing = 0;

                for (List<Boolean> booleans : fullCheck) {
                    if (booleans.contains(Boolean.TRUE)) {
                        noSeatGoing = Boolean.FALSE;
                        break;
                    }
                    cntGoing += 1;
                }

                for (List<Boolean> booleans : fullCheck2) {
                    if (booleans.contains(Boolean.TRUE)) {
                        noSeatComing = Boolean.FALSE;
                        break;
                    }
                    cntComing += 1;
                }

                if (noSeatGoing == Boolean.TRUE && noSeatComing == Boolean.TRUE) {
                    model.addAttribute("fullCheck", fullCheck);
                    model.addAttribute("fullCheck2", fullCheck2);
                    model.addAttribute("disableSeatButton", true);

                    return "schedule";
                }

                if (noSeatGoing == Boolean.TRUE && noSeatComing == Boolean.FALSE) {
                    model.addAttribute("fullCheck", fullCheck);
                    model.addAttribute("fullCheck2", fullCheck2);

                    deployForm.setDeployIdOfComing(deploysWhenComing.get(cntComing).getId());

                    model.addAttribute("disableSeatButton", true);

                    return "schedule";
                }

                if (noSeatGoing == Boolean.FALSE && noSeatComing == Boolean.TRUE) {
                    model.addAttribute("fullCheck", fullCheck);
                    model.addAttribute("fullCheck2", fullCheck2);

                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(cntGoing).getId());

                    model.addAttribute("disableSeatButton", true);

                    return "schedule";
                }

                model.addAttribute("fullCheck", fullCheck);
                model.addAttribute("fullCheck2", fullCheck2);

                deployForm.setDeployIdOfGoing(deploysWhenGoing.get(cntGoing).getId());
                deployForm.setDeployIdOfComing(deploysWhenComing.get(cntComing).getId());

                return "schedule";
            }
        }
// Round vs one-way--------------------------------------------------------------------------------------------------------------
        //select query 데이터 개수 상관없이 4개 나감 O(4)
        //왜 deploys를 긁어 올 때 seat까지 같이 긁지 않는 것인가? => 연관관계를 가지는 train이 부모 클래스라서 자식 클래스인 ktx로 객체 탐색이 불가함
        //oneToOne query 개수만큼 나가는 것 => in 절로 해결하자 => 해결

        //fetch
        List<Deploy> deploysWhenGoing = deployService.searchDeployToTrain(scheduleForm.getDeparturePlace(), scheduleForm.getArrivalPlace(), before);
        Collections.sort(deploysWhenGoing, new DeployComparator());

        if(deploysWhenGoing.isEmpty() == true) {
            model.addAttribute("emptyWhenGoing", true);
            return "schedule";
        }

        model.addAttribute("deploysWhenGoing", deploysWhenGoing);
        model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));

        List<List<Boolean>> fullCheck = new ArrayList<>();
        List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

        //select query를 1/10로 줄임 (oneToOne 때문에 일어난 문제인 듯)
        //oneToOne query 많이 나가는 거 막으려고 미리 당기는 작업
        //미리 당기기 => 미리 안 당기면 Deploys 개수만큼 쿼리가 나가야 됨
        ktxService.getKtxToSeatWithFetchAndIn(deploys);
        mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
        saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

        doCheck(deploys, passengerDto, fullCheck);

        int cntGoing = 0;

        for (List<Boolean> booleans : fullCheck) {
            if (booleans.contains(Boolean.TRUE)) {
                deployForm.setDeployIdOfGoing(deploysWhenGoing.get(cntGoing).getId());
                model.addAttribute("fullCheck", fullCheck);
                return "schedule";
            }
            cntGoing += 1;
        }

        model.addAttribute("fullCheck", fullCheck);
        model.addAttribute("disableSeatButton", true);
        return "schedule";
    }

    //이 기차가 선택된 인원수로 예약이 가능한지 아닌지 알려주는 check List를 생성 후 넘기는 메소드
    private void doCheck(List<Long> deploys, PassengerDto passengerDto, List<List<Boolean>> fullCheck) {
        for (Long deployId : deploys) {
            Deploy deploy = deployService.findDeploy(deployId).get();
            Train train = deploy.getTrain();
            if (train.getTrainName().contains("KTX")) {
                List<KtxRoom> ktxRooms = ((Ktx) train).getKtxRooms();

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

            else if (train.getTrainName().contains("MUGUNGHWA")) {
                List<MugunghwaRoom> mugunghwaRooms = ((Mugunghwa) train).getMugunghwaRooms();

                List<String> reserveOkList = new ArrayList<>();

                for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                    if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                        reserveOkList.add(mugunghwaRoom.getRoomName());
                    }
                }

                List<Boolean> check = new ArrayList<>();

                if (reserveOkList.isEmpty()) {
                    check.add(false);
                } else {
                    check.add(true);
                }
                fullCheck.add(check);
            }

            else {
                List<SaemaulRoom> saemaulRooms = ((Saemaul) train).getSaemaulRooms();

                List<String> reserveOkList = new ArrayList<>();

                for (SaemaulRoom saemaulRoom : saemaulRooms) {
                    if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                        reserveOkList.add(saemaulRoom.getRoomName());
                    }
                }

                List<Boolean> check = new ArrayList<>();

                if (reserveOkList.isEmpty()) {
                    check.add(false);
                } else {
                    check.add(true);
                }
                fullCheck.add(check);
            }
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


