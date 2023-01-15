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
import toy.ktx.domain.comparator.DeployComparator;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.projections.MugunghwaSeatDto;
import toy.ktx.domain.dto.projections.SaemaulSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.service.*;

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
    private final MugunghwaRoomService mugunghwaRoomService;
    private final SaemaulRoomService saemaulRoomService;
    private final DeployService deployService;
    private final KtxService ktxService;
    private final MugunghwaService mugunghwaService;
    private final SaemaulService saemaulService;
    private final MugunghwaSeatService mugunghwaSeatService;
    private final SaemaulSeatService saemaulSeatService;

    //final으로 쓰기가 불가함으로 threadLocal에 넣을 필요없을 듯
    private final String[] alpha = {"A", "B", "C", "D"};

    private ThreadLocal<List<String>> okList = new ThreadLocal<>();

    @PostMapping("/seat")
    public String chooseSeat(@ModelAttribute DeployForm deployForm,
                             BindingResult bindingResult,
                             @ModelAttribute PassengerDto passengerDto,
                             @RequestParam(required = false) String prevGoing,
                             @RequestParam(required = false) String nextGoing,
                             @RequestParam(required = false) String prevComing,
                             @RequestParam(required = false) String nextComing,
                             @RequestParam(required = false) String dateTimeOfGoing,
                             @RequestParam(required = false) String dateTimeOfComing,
                             @RequestParam(required = false) String departurePlace,
                             @RequestParam(required = false) String arrivalPlace,
                             @RequestParam(required = false) Boolean round,
                             Model model) {

        model.addAttribute("departurePlace", departurePlace);
        model.addAttribute("arrivalPlace", arrivalPlace);
        model.addAttribute("round", round);
        model.addAttribute("passengers", passengerDto.howManyOccupied());

        okList.set(new ArrayList<>());

        //이동시 going, coming 둘 다 deploys가 존재하면 8개 한 개만 존재하면 4개 아무것도 없으면 2개만 나가는 구조
        if (round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

            LocalDateTime dateTime = null;
            Boolean noBefore = false;
            Boolean noAfter = false;

            if (prevGoing != null) {
                LocalDateTime newTime = beforeDateTime.minusDays(1);
                //updated point
                List<Deploy> deploysWhenGoing = null;
                List<Deploy> deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, afterDateTime);

                if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
                    deploysWhenGoing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, beforeDateTime);

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

                    deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
                }

                if(!newTime.isBefore(LocalDateTime.now())) {
                    model.addAttribute("before", newTime);
                    model.addAttribute("after", afterDateTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
                }

                //updated
                Collections.sort(deploysWhenGoing, new DeployComparator());
                Collections.sort(deploysWhenComing, new DeployComparator());

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                    return "schedule";
                }

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                    //updated point!!!!!!
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

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                    //updated
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

                model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                model.addAttribute("deploysWhenComing", deploysWhenComing);
                model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                model.addAttribute("dateTimeOfGoing", dateTime.toString());
                model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                //going
                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                //coming
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                //미리 당기기
                ktxService.getKtxToSeatWithFetchAndIn(deploys);
                mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

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

            if (nextGoing != null) {
                LocalDateTime newTime = beforeDateTime.plusDays(1);

                List<Deploy> deploysWhenGoing = null;
                List<Deploy> deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, afterDateTime);

                if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() != LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                    deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, beforeDateTime);

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
                    deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
                }

                if(!newTime.isAfter(LocalDateTime.now().plusDays(30))) {
                    model.addAttribute("before", newTime);
                    model.addAttribute("after", afterDateTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
                }

                Collections.sort(deploysWhenGoing, new DeployComparator());
                Collections.sort(deploysWhenComing, new DeployComparator());

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("emptyWhenComing", true);

                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfComing", dateTimeOfComing);
                    return "schedule";
                }

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());
                    log.info("fuck 555 = {}", deploys);

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

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("dateTimeOfGoing", dateTime.toString());
                    model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

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

                model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                model.addAttribute("deploysWhenComing", deploysWhenComing);
                model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                model.addAttribute("dateTimeOfGoing", dateTime.toString());
                model.addAttribute("dateTimeOfComing", dateTimeOfComing);

                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                //going
                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                //coming
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                ktxService.getKtxToSeatWithFetchAndIn(deploys);
                mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

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

            if (prevComing != null) {
                LocalDateTime newTime = afterDateTime.minusDays(1);

                List<Deploy> deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, beforeDateTime);
                List<Deploy> deploysWhenComing = null;

                if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
                    deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, afterDateTime);

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

                    deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, dateTime);
                }

                if(!newTime.isBefore(LocalDateTime.now())) {
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, dateTime);
                }

                Collections.sort(deploysWhenGoing, new DeployComparator());
                Collections.sort(deploysWhenComing, new DeployComparator());

                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("emptyWhenGoing", true);

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfComing", dateTime.toString());
                    return "schedule";
                }

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfComing", dateTime.toString());

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                    //updated
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

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfComing", dateTime.toString());

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

                model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                model.addAttribute("deploysWhenComing", deploysWhenComing);
                model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                model.addAttribute("dateTimeOfComing", dateTime.toString());

                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                //going
                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                //coming
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                ktxService.getKtxToSeatWithFetchAndIn(deploys);
                mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

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

            if (nextComing != null) {
                LocalDateTime newTime = afterDateTime.plusDays(1);

                List<Deploy> deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, beforeDateTime);
                List<Deploy> deploysWhenComing = null;

                if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() != LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                    deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, afterDateTime);

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
                    deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, dateTime);
                }

                if(!newTime.isAfter(LocalDateTime.now().plusDays(30))) {
                    model.addAttribute("before", beforeDateTime);
                    model.addAttribute("after", newTime);

                    int year = newTime.getYear();
                    int monthValue = newTime.getMonthValue();
                    int dayOfMonth = newTime.getDayOfMonth();

                    dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                    deploysWhenComing = deployService.searchDeployToTrain(arrivalPlace, departurePlace, dateTime);
                }

                Collections.sort(deploysWhenGoing, new DeployComparator());
                Collections.sort(deploysWhenComing, new DeployComparator());


                if (deploysWhenGoing.isEmpty() == true && deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("emptyWhenGoing", true);

                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfComing", dateTime.toString());
                    return "schedule";
                }

                if (deploysWhenComing.isEmpty() == true) {
                    model.addAttribute("emptyWhenComing", true);
                    model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                    model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfComing", dateTime.toString());

                    List<List<Boolean>> fullCheck = new ArrayList<>();
                    List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

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

                if (deploysWhenGoing.isEmpty() == true) {
                    model.addAttribute("emptyWhenGoing", true);
                    model.addAttribute("deploysWhenComing", deploysWhenComing);
                    model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                    model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                    model.addAttribute("dateTimeOfComing", dateTime.toString());

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

                model.addAttribute("deploysWhenGoing", deploysWhenGoing);
                model.addAttribute("deploysWhenComing", deploysWhenComing);
                model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
                model.addAttribute("durationsWhenComing", getDuration(deploysWhenComing));
                model.addAttribute("dateTimeOfGoing", dateTimeOfGoing);
                model.addAttribute("dateTimeOfComing", dateTime.toString());

                List<List<Boolean>> fullCheck = new ArrayList<>();
                List<List<Boolean>> fullCheck2 = new ArrayList<>();

                //going
                List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());
                //coming
                List<Long> deploys2 = deploysWhenComing.stream().map(d -> d.getId()).collect(Collectors.toList());

                ktxService.getKtxToSeatWithFetchAndIn(deploys);
                mugunghwaService.getMugunghwaToSeatWithFetchAndIn(deploys);
                saemaulService.getSaemaulToSeatWithFetchAndIn(deploys);

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
            //success logic
            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            if (deploy.getTrain().getTrainName().contains("KTX")) {
                Ktx train = (Ktx) deploy.getTrain();
                List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByIdWithFetch(train.getId());

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

                if(normalReserveOkList.isEmpty()) {
                    model.addAttribute("normalDisabled", true);
                }

                if(vipReserveOkList.isEmpty()) {
                    model.addAttribute("vipDisabled", true);
                }

                model.addAttribute("going", true);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                return "normalVip";
            }
            else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                Mugunghwa train = (Mugunghwa) deploy.getTrain();
                List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(train.getId());
                MugunghwaRoom targetRoom = null;

                for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                    if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                        okList.get().add(mugunghwaRoom.getRoomName());
                    }
                }

                for (String roomName : okList.get()) {
                    Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                    if (optionalMugunghwaRoom.isPresent()) {
                        targetRoom = optionalMugunghwaRoom.get();
                        break;
                    }
                }
                MugunghwaSeatDto mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(targetRoom.getMugunghwaSeat().getId());

                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
                model.addAttribute("map", map);

                model.addAttribute("going", true);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                model.addAttribute("roomName", targetRoom.getRoomName());
                model.addAttribute("okList", okList.get());

                return "trainseat/chooseMugunghwaSeat";
            }
            else {
                Saemaul train = (Saemaul) deploy.getTrain();
                List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(train.getId());
                SaemaulRoom targetRoom = null;

                for (SaemaulRoom saemaulRoom : saemaulRooms) {
                    if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                        okList.get().add(saemaulRoom.getRoomName());
                    }
                }

                for (String roomName : okList.get()) {
                    Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                    if (optionalSaemaulRoom.isPresent()) {
                        targetRoom = optionalSaemaulRoom.get();
                        break;
                    }
                }
                SaemaulSeatDto saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(targetRoom.getSaemaulSeat().getId());

                ObjectMapper objectMapper = new ObjectMapper();
                Map map = objectMapper.convertValue(saemaulSeatDto, Map.class);
                model.addAttribute("map", map);

                model.addAttribute("going", true);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                model.addAttribute("saemaulRooms", saemaulRooms);
                model.addAttribute("roomName", targetRoom.getRoomName());
                model.addAttribute("okList", okList.get());

                return "trainseat/chooseSaemaulSeat";
            }
        }
// round vs one-way --------------------------------------------------------------------------------------------------------------------------
        //이동시 going이 존재하면 4개 존재하지 않으면 1개만 나감
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
        LocalDateTime dateTime = null;

        Boolean noBefore = false;
        Boolean noAfter = false;

        if (prevGoing != null) {
            LocalDateTime newTime = beforeDateTime.minusDays(1);
            List<Deploy> deploysWhenGoing = null;

            if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() != LocalDateTime.now().getDayOfMonth()) {
                deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, beforeDateTime);

                bindingResult.reject("noBefore", null);
                model.addAttribute("before", beforeDateTime);

                dateTime = beforeDateTime;
                noBefore = true;
            }

            if (newTime.isBefore(LocalDateTime.now()) && newTime.getDayOfMonth() == LocalDateTime.now().getDayOfMonth()) {
                model.addAttribute("before", newTime);

                dateTime = LocalDateTime.now();

                deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
            }

            if(!newTime.isBefore(LocalDateTime.now())) {
                model.addAttribute("before", newTime);

                int year = newTime.getYear();
                int monthValue = newTime.getMonthValue();
                int dayOfMonth = newTime.getDayOfMonth();

                dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
            }

            Collections.sort(deploysWhenGoing, new DeployComparator());

            if (deploysWhenGoing.isEmpty() == true) {
                model.addAttribute("emptyWhenGoing", true);
                model.addAttribute("dateTimeOfGoing", dateTime.toString());
                model.addAttribute("dateTimeOfComing", dateTimeOfComing);
                return "schedule";
            }

            model.addAttribute("deploysWhenGoing", deploysWhenGoing);
            model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
            model.addAttribute("dateTimeOfGoing", dateTime.toString());
            model.addAttribute("dateTimeOfComing", dateTimeOfComing);

            List<List<Boolean>> fullCheck = new ArrayList<>();
            List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

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

        if (nextGoing != null) {
            LocalDateTime newTime = beforeDateTime.plusDays(1);
            List<Deploy> deploysWhenGoing = null;

            if (newTime.isAfter(LocalDateTime.now().plusDays(30)) && newTime.getDayOfMonth() != LocalDateTime.now().plusDays(30).getDayOfMonth()) {
                deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, beforeDateTime);

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
                deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
            }

            if(!newTime.isAfter(LocalDateTime.now().plusDays(30))) {
                model.addAttribute("before", newTime);

                int year = newTime.getYear();
                int monthValue = newTime.getMonthValue();
                int dayOfMonth = newTime.getDayOfMonth();

                dateTime = LocalDateTime.of(year, monthValue, dayOfMonth, 0, 0);
                deploysWhenGoing = deployService.searchDeployToTrain(departurePlace, arrivalPlace, dateTime);
            }

            Collections.sort(deploysWhenGoing, new DeployComparator());

            if (deploysWhenGoing.isEmpty() == true) {
                model.addAttribute("emptyWhenGoing", true);
                model.addAttribute("dateTimeOfGoing", dateTime.toString());
                model.addAttribute("dateTimeOfComing", dateTimeOfComing);
                return "schedule";
            }

            model.addAttribute("deploysWhenGoing", deploysWhenGoing);
            model.addAttribute("durationsWhenGoing", getDuration(deploysWhenGoing));
            model.addAttribute("dateTimeOfGoing", dateTime.toString());
            model.addAttribute("dateTimeOfComing", dateTimeOfComing);

            List<List<Boolean>> fullCheck = new ArrayList<>();
            List<Long> deploys = deploysWhenGoing.stream().map(d -> d.getId()).collect(Collectors.toList());

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

        //success Logic
        Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
        if (deploy.getTrain().getTrainName().contains("KTX")) {
            Ktx train = (Ktx) deploy.getTrain();
            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByIdWithFetch(train.getId());

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
        else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
            Mugunghwa train = (Mugunghwa) deploy.getTrain();
            List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(train.getId());
            MugunghwaRoom targetRoom = null;

            for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(mugunghwaRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalMugunghwaRoom.isPresent()) {
                    targetRoom = optionalMugunghwaRoom.get();
                    break;
                }
            }

            MugunghwaSeatDto mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(targetRoom.getMugunghwaSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("going", true);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            model.addAttribute("mugunghwaRooms", mugunghwaRooms);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "trainseat/chooseMugunghwaSeat";
        }
        else {
            Saemaul train = (Saemaul) deploy.getTrain();
            List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(train.getId());
            SaemaulRoom targetRoom = null;

            for (SaemaulRoom saemaulRoom : saemaulRooms) {
                if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(saemaulRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalSaemaulRoom.isPresent()) {
                    targetRoom = optionalSaemaulRoom.get();
                    break;
                }
            }

            SaemaulSeatDto saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(targetRoom.getSaemaulSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(saemaulSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("going", true);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            model.addAttribute("saemaulRooms", saemaulRooms);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "trainseat/chooseSaemaulSeat";
        }
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
}
