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
import toy.ktx.domain.dto.RoomDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.service.DeployService;
import toy.ktx.service.KtxRoomService;
import toy.ktx.service.KtxSeatService;
import toy.ktx.service.KtxService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final KtxRoomService ktxRoomService;
    private final KtxSeatService ktxSeatService;
    private final KtxService ktxService;
    private final DeployService deployService;
    private Integer roomNo = 0;
    //여기 수정되어야 함
    private String targetRoom = "room1";
    private ObjectMapper objectMapper = new ObjectMapper();

    @PostMapping("/reservation/normal")
    public String reserveNormal(@ModelAttribute NormalSeatDto normalSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @RequestParam Integer beforeOccupied,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
                          @RequestParam(required = false) String departurePlace,
                          @RequestParam(required = false) String arrivalPlace,
                          @RequestParam(required = false) String dateTimeOfGoing,
                          @RequestParam(required = false) String dateTimeOfLeaving,
                          Model model) {

        model.addAttribute("passengers", passengerDto.howManyOccupied());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Map map = objectMapper.convertValue(roomDto, Map.class);
            Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

            if(roomChange.isPresent()){
                for (Object key : map.keySet()) {
                    if (map.get(key) != null) {
                        targetRoom = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

//                model.addAttribute("seatDto", normalSeatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = normalSeatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);

                return "chooseNormalSeat";
            }

            else {
                if(normalSeatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfGoing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                    normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

//                    model.addAttribute("seatDto", normalSeatDto);
                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);
                    model.addAttribute("beforeOccupied", beforeOccupied);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);

                    return "chooseNormalSeat";
                }

                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

//                normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());
//                beforeOccupied = normalSeatDto.howManyOccupied();

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

//                ObjectMapper objectMapper = new ObjectMapper();
//                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
//                model.addAttribute("map", seatMap);
//                model.addAttribute("ktxRooms", ktxRooms);

                return "normalVip";
            }
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Map map = objectMapper.convertValue(roomDto, Map.class);
            Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

            if(roomChange.isPresent()){
                for (Object key : map.keySet()) {
                    if (map.get(key) != null) {
                        targetRoom = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

//                model.addAttribute("seatDto", normalSeatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = normalSeatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);

                return "chooseNormalSeat";
            }

            else {
                if(normalSeatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfComing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                    normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

//                    model.addAttribute("seatDto", normalSeatDto);
                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);
                    model.addAttribute("beforeOccupied", beforeOccupied);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);

                    return "chooseNormalSeat";
                }

                //success logic
                return "temp";
            }
        }
//--------------------------------------------------------------------------------------------------------------------------------------
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

        System.out.println("roomDto = " + roomDto);
        Map map = objectMapper.convertValue(roomDto, Map.class);
        Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

        if(roomChange.isPresent()){
            for (Object key : map.keySet()) {
                if (map.get(key) != null) {
                    targetRoom = (String) key;
                }
            }

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

            normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

//            model.addAttribute("seatDto", normalSeatDto);
            model.addAttribute("going", true);

            beforeOccupied = normalSeatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);

            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);

            return "chooseNormalSeat";
        }

        else {
            if(normalSeatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

//                model.addAttribute("seatDto", normalSeatDto);
                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);
                model.addAttribute("beforeOccupied", beforeOccupied);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);

                return "chooseNormalSeat";
            }

            //success logic

            return "temp";
        }
    }

    @PostMapping("/reservation/vip")
    public String reserveVip(@ModelAttribute VipSeatDto vipSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @RequestParam Integer beforeOccupied,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
                          @RequestParam(required = false) String departurePlace,
                          @RequestParam(required = false) String arrivalPlace,
                          @RequestParam(required = false) String dateTimeOfGoing,
                          @RequestParam(required = false) String dateTimeOfLeaving,
                          Model model) {

        model.addAttribute("passengers", passengerDto.howManyOccupied());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Map map = objectMapper.convertValue(roomDto, Map.class);
            Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

            if(roomChange.isPresent()){
                for (Object key : map.keySet()) {
                    if (map.get(key) != null) {
                        targetRoom = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

//                model.addAttribute("seatDto", vipSeatDto);
                model.addAttribute("round", true);
                model.addAttribute("going", true);

                beforeOccupied = vipSeatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);

                return "chooseVipSeat";
            }

            else {
                if(vipSeatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfGoing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                    vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

//                    model.addAttribute("seatDto", vipSeatDto);
                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);
                    model.addAttribute("beforeOccupied", beforeOccupied);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);

                    return "chooseVipSeat";
                }

                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

//                vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());
//                beforeOccupied = vipSeatDto.howManyOccupied();

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

//                ObjectMapper objectMapper = new ObjectMapper();
//                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
//                model.addAttribute("map", seatMap);
//                model.addAttribute("ktxRooms", ktxRooms);

                return "normalVip";
            }
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfLeaving);

            Map map = objectMapper.convertValue(roomDto, Map.class);
            Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

            if(roomChange.isPresent()){
                for (Object key : map.keySet()) {
                    if (map.get(key) != null) {
                        targetRoom = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

//                model.addAttribute("seatDto", vipSeatDto);
                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                beforeOccupied = vipSeatDto.howManyOccupied();
                model.addAttribute("beforeOccupied", beforeOccupied);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);

                return "chooseVipSeat";
            }

            else {
                if(vipSeatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfComing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                    vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

//                    model.addAttribute("seatDto", vipSeatDto);
                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);
                    model.addAttribute("beforeOccupied", beforeOccupied);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);

                    return "chooseVipSeat";
                }

                //success logic
                return "temp";
            }
        }
//--------------------------------------------------------------------------------------------------------------------------------------
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

        System.out.println("roomDto = " + roomDto);
        Map map = objectMapper.convertValue(roomDto, Map.class);
        Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

        if(roomChange.isPresent()){
            for (Object key : map.keySet()) {
                if (map.get(key) != null) {
                    targetRoom = (String) key;
                }
            }

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

            vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

//            model.addAttribute("seatDto", vipSeatDto);
            model.addAttribute("going", true);

            beforeOccupied = vipSeatDto.howManyOccupied();
            model.addAttribute("beforeOccupied", beforeOccupied);

            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);

            return "chooseVipSeat";
        }

        else {
            if(vipSeatDto.howManyOccupied() - beforeOccupied != passengerDto.howManyOccupied()) {

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoom)).findAny();

                vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

//                model.addAttribute("seatDto", vipSeatDto);
                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);
                model.addAttribute("beforeOccupied", beforeOccupied);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);

                return "chooseVipSeat";
            }

            //success logic

            return "temp";
        }
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }
}
