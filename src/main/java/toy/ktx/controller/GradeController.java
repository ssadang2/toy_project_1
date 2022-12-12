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
import toy.ktx.domain.Train;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
import toy.ktx.service.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class GradeController {

    private final DeployService deployService;
    private final KtxService ktxService;
    private final KtxRoomService ktxRoomService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;

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
                              Model model) {

        model.addAttribute("departurePlace", departurePlace);
        model.addAttribute("arrivalPlace", arrivalPlace);
        model.addAttribute("passengers", passengerDto.howManyOccupied());

        if (normal != null && round == true && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfComing());
            Ktx ktx = (Ktx) deploy.getTrain();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
            KtxRoom ktxRoom = ktxRooms.get(0);

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(ktxRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", ktxRoom.getRoomName());

            return "chooseNormalSeat";
        }

        else if (normal != null && round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
            KtxRoom ktxRoom = ktxRooms.get(0);

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(ktxRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", ktxRoom.getRoomName());

            return "chooseNormalSeat";
        }

        if (normal != null) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);;
            KtxRoom ktxRoom = ktxRooms.get(0);

            NormalSeatDto normalSeatDto = ktxSeatNormalService.findNormalDtoById(ktxRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("roomName", ktxRoom.getRoomName());

            return "chooseNormalSeat";
        }

// normal vs vip -------------------------------------------------------------------------------------------------------------------------------------
        if (vip != null && round == true && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Long deployId = deployForm.getDeployIdOfComing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            KtxRoom ktxRoom = ktxRooms.get(0);

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(ktxRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("coming", true);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", ktxRoom.getRoomName());
            log.info("시발 ={}", ktxRoom.getRoomName());

            return "chooseVipSeat";
        }

        else if (vip != null && round == true) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            KtxRoom ktxRoom = ktxRooms.get(0);

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(ktxRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("round", true);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("dateTimeOfLeaving", afterDateTime);
            model.addAttribute("roomName", ktxRoom.getRoomName());
            log.info("시발 ={}", ktxRoom.getRoomName());

            return "chooseVipSeat";
        }

        else {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            KtxRoom ktxRoom = ktxRooms.get(0);

            VipSeatDto vipSeatDto = ktxSeatVipService.findVipDtoById(ktxRoom.getKtxSeat().getId());

            ObjectMapper objectMapper = new ObjectMapper();
            Map map = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", map);

            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("going", going);

            model.addAttribute("dateTimeOfGoing", beforeDateTime);
            model.addAttribute("roomName", ktxRoom.getRoomName());
            log.info("시발 ={}", ktxRoom.getRoomName());

            return "chooseVipSeat";
        }
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }
}
