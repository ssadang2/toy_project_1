package toy.ktx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Train;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.service.DeployService;
import toy.ktx.service.KtxRoomService;
import toy.ktx.service.KtxSeatService;
import toy.ktx.service.KtxService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@Slf4j
@RequiredArgsConstructor
public class SeatController {

    private final KtxRoomService ktxRoomService;
    private final DeployService deployService;

    private final String[] alpha = {"A", "B", "C", "D"};
    //never used
//    Map seat = new HashMap();

    @PostMapping("/seat")
    public String chooseSeat(@ModelAttribute DeployForm deployForm,
                             BindingResult bindingResult,
                             @RequestParam(required = false) String prevGoing,
                             @RequestParam(required = false) String nextGoing,
                             @RequestParam(required = false) String prevComing,
                             @RequestParam(required = false) String nextComing,
                             @RequestParam(required = false) String dateTimeOfGoing,
                             @RequestParam(required = false) String dateTimeOfLeaving,
                             @RequestParam(required = false) String departurePlace,
                             @RequestParam(required = false) String arrivalPlace,
                             @RequestParam(required = false) Boolean round,
                             @ModelAttribute PassengerDto passengerDto,
                             Model model) {

        model.addAttribute("departurePlace", departurePlace);
        model.addAttribute("arrivalPlace", arrivalPlace);
        model.addAttribute("round", round);
        model.addAttribute("passengers", passengerDto.howManyOccupied());

        if (round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);
//            updated point
//            List<Deploy> deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, beforeDateTime);
//            //오는 날에는 가는 날의 출발지가 도착지고 도착지가 출발지임 따라서 getArrivalPlace가 departurePlace(출발지)에 위치해야 됨
//            List<Deploy> deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, afterDateTime);

//            List<Deploy> deploysWhenGoing = null;
//            List<Deploy> deploysWhenComing = null;

            LocalDateTime dateTime = null;
            Boolean noBefore = false;
            Boolean noAfter = false;

            if (prevGoing != null) {
                LocalDateTime newTime = beforeDateTime.minusDays(1);
                //updated point
                List<Deploy> deploysWhenGoing = null;
                List<Deploy> deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, afterDateTime);

                if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
                    deploysWhenGoing = deployService.searchDeploy(arrivalPlace, departurePlace, beforeDateTime);

                    bindingResult.reject("noBefore", null);
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", afterDateTime);

                    dateTime = beforeDateTime;
                    noBefore = true;
                }

                if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                    model.addAttribute("before", newTime);
                    model.addAttribute("after", afterDateTime);

                    dateTime = LocalDateTime.now();

                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
                }

                if(!newTime.isBefore(LocalDateTime.now())) {
                    model.addAttribute("before", newTime);
                    model.addAttribute("after", afterDateTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
                }

                //fullCheck list 넘겨줘야 됨
                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);
                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys2);

                doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);
                doCheck(deploysWhenComing, ktxRooms2, passengerDto, fullCheck2);

                model.addAttribute("fullCheck", fullCheck);
                model.addAttribute("fullCheck2", fullCheck2);

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                    return "schedule";
                }

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                    return "schedule";
                }

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                    return "schedule";
                }

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

            if (nextGoing != null) {
                LocalDateTime newTime = beforeDateTime.plusDays(1);

                List<Deploy> deploysWhenGoing = null;
                List<Deploy> deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, afterDateTime);

                if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() != LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, beforeDateTime);

                    bindingResult.reject("noAfter", null);
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", afterDateTime);

                    dateTime = beforeDateTime;
                    noAfter = true;
                }

                if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() == LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                    model.addAttribute("before", newTime);
                    model.addAttribute("after", afterDateTime);

                    int year = LocalDateTime.now().plusDays(30).getYear();
                    int monthValue = LocalDateTime.now().plusDays(30).getMonthValue();
                    int dayOfMonth = LocalDateTime.now().plusDays(30).getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    //이새끼 뭐야
                    //deploysWhenGoing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
                }

                if(!newTime.isAfter(LocalDateTime.now().plusDays(30))) {
                    model.addAttribute("before", newTime);
                    model.addAttribute("after", afterDateTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
                }

                //fullCheck list 넘겨줘야 됨
                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);
                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys2);

                doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);
                doCheck(deploysWhenComing, ktxRooms2, passengerDto, fullCheck2);

                model.addAttribute("fullCheck", fullCheck);
                model.addAttribute("fullCheck2", fullCheck2);

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                    return "schedule";
                }

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                    return "schedule";
                }

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfLeaving", dateTimeOfLeaving);
                    return "schedule";
                }

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

            if (prevComing != null) {
                LocalDateTime newTime = afterDateTime.minusDays(1);

                List<Deploy> deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, beforeDateTime);
                List<Deploy> deploysWhenComing = null;

                if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, afterDateTime);

                    bindingResult.reject("noBefore", null);
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", afterDateTime);

                    dateTime = afterDateTime;
                    noBefore = true;
                }

                if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", newTime);

                    dateTime = LocalDateTime.now();

                    //이 새끼 뭐임?
//                    deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);
                }

                if(!newTime.isBefore(LocalDateTime.now())) {
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);
                }

                //fullCheck list 넘겨줘야 됨
                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);
                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys2);

                doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);
                doCheck(deploysWhenComing, ktxRooms2, passengerDto, fullCheck2);

                model.addAttribute("fullCheck", fullCheck);
                model.addAttribute("fullCheck2", fullCheck2);

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("emptyWhenGoing", true);

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                    return "schedule";
                }

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                    return "schedule";
                }

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                    return "schedule";
                }

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

            if (nextComing != null) {
                LocalDateTime newTime = afterDateTime.plusDays(1);

                List<Deploy> deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, beforeDateTime);
                List<Deploy> deploysWhenComing = null;

                if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() != LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, afterDateTime);

                    bindingResult.reject("noAfter", null);
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", afterDateTime);

                    dateTime = afterDateTime;
                    noAfter = true;
                }

                if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() == LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", newTime);

                    int year = LocalDateTime.now().plusDays(30).getYear();
                    int monthValue = LocalDateTime.now().plusDays(30).getMonthValue();
                    int dayOfMonth = LocalDateTime.now().plusDays(30).getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);
                }

                if(!newTime.isAfter(LocalDateTime.now().plusDays(30))) {
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);
                }

                //fullCheck list 넘겨줘야 됨
                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);
                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys2);

                doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);
                doCheck(deploysWhenComing, ktxRooms2, passengerDto, fullCheck2);

                model.addAttribute("fullCheck", fullCheck);
                model.addAttribute("fullCheck2", fullCheck2);

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("emptyWhenGoing", true);

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                    return "schedule";
                }

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);

                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    deployForm.setDeployIdOfGoing(deploysWhenGoing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                    return "schedule";
                }

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);

                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    deployForm.setDeployIdOfComing(deploysWhenComing.get(0).getId());

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfLeaving", dateTime.toString());
                    return "schedule";
                }

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

            //success logic
            //예상 select query 2개? => 2개 맞음
            //List<KtxSeat> ktxSeats = ktxSeatService.findKtxSeatWithKtxRoomWithTrainWithDeploy(deployForm.getDeployIdOfGoing());
            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
            Ktx train = (Ktx) deploy.getTrain();
            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomWithSeatFetch(train.getId());

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

            log.info("시발 ={}", normalReserveOkList);
            log.info("시발 ={}", vipReserveOkList);

            if(normalReserveOkList.isEmpty()) {
                model.addAttribute("normalDisabled", true);
                model.addAttribute("normalReserveOkList", normalReserveOkList);
            }

            if(vipReserveOkList.isEmpty()) {
                model.addAttribute("vipDisabled", true);
                model.addAttribute("vipReserveOkList", vipReserveOkList);
            }

            model.addAttribute("round", true);
            model.addAttribute("going", true);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);

            return "normalVip";
        }
// round vs one-way --------------------------------------------------------------------------------------------------------------------------
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
        LocalDateTime dateTime = null;

        Boolean noBefore = false;
        Boolean noAfter = false;

        if (prevGoing != null) {
            LocalDateTime newTime = beforeDateTime.minusDays(1);
            List<Deploy> deploysWhenGoing = null;

            if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
                deploysWhenGoing = deployService.searchDeployWithTrain(departurePlace, arrivalPlace, beforeDateTime);

                bindingResult.reject("noBefore", null);
                model.addAttribute("before", beforeDateTime);

                dateTime = beforeDateTime;
                noBefore = true;
            }

            if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                model.addAttribute("before", newTime);

                dateTime = LocalDateTime.now();

                deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
            }

            if(!newTime.isBefore(LocalDateTime.now())) {
                model.addAttribute("before", newTime);

                int year = newTime.getYear();
                int monthValue = newTime.getMonthValue();
                int dayOfMonth = newTime.getDayOfMonth();

                dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
            }

            //fullCheck list 넘겨줘야 됨
            List<List<Boolean>> fullCheck = new ArrayList<>();

            List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);

            doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);

            model.addAttribute("fullCheck", fullCheck);

            if (deploysWhenGoing.isEmpty() == true) {
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

        if (nextGoing != null) {
            LocalDateTime newTime = beforeDateTime.plusDays(1);
            List<Deploy> deploysWhenGoing = null;

            if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() != LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                deploysWhenGoing = deployService.searchDeployWithTrain(departurePlace, arrivalPlace, beforeDateTime);

                bindingResult.reject("noAfter", null);
                model.addAttribute("before", beforeDateTime);

                dateTime = beforeDateTime;
                noAfter = true;
            }

            if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() == LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                model.addAttribute("before", newTime);

                int year = LocalDateTime.now().plusDays(30).getYear();
                int monthValue = LocalDateTime.now().plusDays(30).getMonthValue();
                int dayOfMonth = LocalDateTime.now().plusDays(30).getDayOfMonth();

                dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                //이 ㅅㄲ 뭐임?
//                deploysWhenGoing = deployService.searchDeploy(arrivalPlace, departurePlace, dateTime);
                deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
            }

            if(!newTime.isAfter(LocalDateTime.now().plusDays(30))) {
                model.addAttribute("before", newTime);

                int year = newTime.getYear();
                int monthValue = newTime.getMonthValue();
                int dayOfMonth = newTime.getDayOfMonth();

                dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                deploysWhenGoing = deployService.searchDeploy(departurePlace, arrivalPlace, dateTime);
            }

            //fullCheck list 넘겨줘야 됨
            List<List<Boolean>> fullCheck = new ArrayList<>();

            List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsWithSeatWithInFetch(deploys);

            doCheck(deploysWhenGoing, ktxRooms, passengerDto, fullCheck);

            model.addAttribute("fullCheck", fullCheck);

            if (deploysWhenGoing.isEmpty() == true) {
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
        //success Logic
        //예상 select query 2개? => 2개 맞음
        //List<KtxSeat> ktxSeats = ktxSeatService.findKtxSeatWithKtxRoomWithTrainWithDeploy(deployForm.getDeployIdOfGoing());
        Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
        Ktx train = (Ktx) deploy.getTrain();
        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomWithSeatFetch(train.getId());

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

        log.info("시발 ={}", normalReserveOkList);
        log.info("시발 ={}", vipReserveOkList);

        if(normalReserveOkList.isEmpty()) {
            model.addAttribute("normalDisabled", true);
        }

        if(vipReserveOkList.isEmpty()) {
            model.addAttribute("vipDisabled", true);
        }

        model.addAttribute("going", true);
        model.addAttribute("dateTimeOfGoing", beforeDateTime);

        return "normalVip";
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
}
