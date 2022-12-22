package toy.ktx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final DeployService deployService;
    private final KtxRoomService ktxRoomService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;

    //동시성 제어 => 원래 쓰레드 로컬은 로직이 끝나면 Remove하는 게 맞으나 postMapping 호출하자마자 new ArrayList를 set하므로 remove 굳이 필요없을 듯
    private ThreadLocal<List<String>> okList = new ThreadLocal<>();

    @PostMapping("/grade")
    public String choiceGrade(@ModelAttribute DeployForm deployForm,
                              @ModelAttribute PassengerDto passengerDto,
                              @RequestParam(required = false) Boolean round,
                              @RequestParam(required = false) Boolean going,
                              @RequestParam(required = false) Boolean coming,
                              @RequestParam(required = false) String departurePlace,
                              @RequestParam(required = false) String arrivalPlace,
                              @RequestParam(required = false) String dateTimeOfGoing,
                              @RequestParam(required = false) String dateTimeOfLeaving,
                              @RequestParam(required = false) String normal,
                              @RequestParam(required = false) String vip,
                              @RequestParam(required = false) Boolean beforeNormal,
                              @RequestParam(required = false) Boolean beforeVip,
                              @RequestParam(required = false) String beforeChosenSeats,
                              @RequestParam(required = false) String beforeRoomName,
                              Model model) {

        model.addAttribute("departurePlace", departurePlace);
        model.addAttribute("arrivalPlace", arrivalPlace);
        model.addAttribute("passengers", passengerDto.howManyOccupied());

        log.info("fuck123 = {}", beforeNormal);
        log.info("fuck123 = {}", beforeVip);
        log.info("fuck123 = {}", beforeChosenSeats);

        okList.set(new ArrayList<>());

        if (normal != null && round == true && coming == Boolean.TRUE) {
            model.addAttribute("beforeNormal", beforeNormal);
            model.addAttribute("beforeVip", beforeVip);
            model.addAttribute("beforeChosenSeats", beforeChosenSeats);
            model.addAttribute("beforeRoomName", beforeRoomName);

            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "chooseNormalSeat";
        }

        else if (normal != null && round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "chooseNormalSeat";
        }

        if (normal != null) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.NORMAL);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatNormal ktxSeatNormal = (KtxSeatNormal) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck1234 = {}", okList.get());

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "chooseNormalSeat";
        }

// normal vs vip -------------------------------------------------------------------------------------------------------------------------------------
        if (vip != null && round == true && coming == Boolean.TRUE) {
            model.addAttribute("beforeNormal", beforeNormal);
            model.addAttribute("beforeVip", beforeVip);
            model.addAttribute("beforeChosenSeats", beforeChosenSeats);
            model.addAttribute("beforeRoomName", beforeRoomName);

            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatVip ktxSeatNormal = (KtxSeatVip) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "chooseVipSeat";
        }

        else if (vip != null && round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatVip ktxSeatNormal = (KtxSeatVip) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "chooseVipSeat";
        }

        else {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
            KtxRoom targetRoom = null;

            for (KtxRoom ktxRoom : ktxRooms) {
                KtxSeatVip ktxSeatNormal = (KtxSeatVip) ktxRoom.getKtxSeat();
                if (ktxSeatNormal.remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                    okList.get().add(ktxRoom.getRoomName());
                }
            }

            for (String roomName : okList.get()) {
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                if (optionalKtxRoom.isPresent()) {
                    targetRoom = optionalKtxRoom.get();
                    break;
                }
            }
            log.info("fuck = {}", okList.get());

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(targetRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("roomName", targetRoom.getRoomName());
            model.addAttribute("okList", okList.get());

            return "chooseVipSeat";
        }
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }
}
