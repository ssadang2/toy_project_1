package toy.ktx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.Hibernate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.*;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.dto.CheckRoomDto;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.RoomDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.service.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;
    private final KtxService ktxService;
    private final DeployService deployService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final PassengerService passengerService;

    //공유변수 주의하자(동시성 문제)
    private ObjectMapper objectMapper = new ObjectMapper();
    private String targetRoomName = null;

    private NormalSeatDto beforeNormalSeatDto = null;
    private VipSeatDto beforeVipSeatDto = null;
    private String beforeRoomName = null;


    @PostMapping("/reservation/normal")
    //postmapping에 Transactional 거는 게 에바라는 의견이 좀 있고 본인도 그렇게 생각, service쪽으로 빼야 될 것 같음(이래도 동작은 됨)
    //transactional 지워도 잘 됨 오해가 있었던 듯
    //@Transactional
    public String reserveNormal(@ModelAttribute NormalSeatDto normalSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
                          @ModelAttribute CheckRoomDto checkRoomDto,
                          @RequestParam String roomName,
                          @RequestParam(required = false) String departurePlace,
                          @RequestParam(required = false) String arrivalPlace,
                          @RequestParam(required = false) String dateTimeOfGoing,
                          @RequestParam(required = false) String dateTimeOfLeaving,
                          HttpServletRequest request,
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
                        targetRoomName = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName)).findAny();

                normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseNormalSeat";
            }

            else {
                if(normalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfGoing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "chooseNormalSeat";
                }

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                beforeRoomName = roomName;
                beforeNormalSeatDto = normalSeatDto;
                beforeVipSeatDto = null;

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
                        targetRoomName = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName)).findAny();

                normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseNormalSeat";
            }

            else {
                if(normalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfComing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "chooseNormalSeat";
                }

                //success logic
                Reservation reservation = new Reservation();
                Reservation reservation2 = new Reservation();
                //갈 때
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();
                Ktx ktx = ktxService.findKtx(trainId).get();

                //자리차지
                if (beforeNormalSeatDto != null) {
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    KtxSeatNormal foundSeat = (KtxSeatNormal) ktxSeatService.findKtxSeat(foundRoom.get().getKtxSeat().getId()).get();
                    reservation.setSeats(beforeNormalSeatDto.returnSeats());
                    foundSeat.normalDtoToEntity(beforeNormalSeatDto);
                }
                else{
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    KtxSeatNormal foundSeat = (KtxSeatNormal) ktxSeatService.findKtxSeat(foundRoom.get().getKtxSeat().getId()).get();
                    reservation.setSeats(beforeVipSeatDto.returnSeats());
                    foundSeat.vipDtoToEntity(beforeVipSeatDto);
                }

                //올 떄
                Long deployId2 = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy2 = deployService.findDeploy(deployId2);
                Long trainId2 = deploy2.get().getTrain().getId();

                Ktx ktx2 = ktxService.findKtx(trainId2).get();
                List<KtxRoom> ktxRooms2 = ktxRoomService.findByKtxAndGrade(ktx2, Grade.NORMAL);
                Optional<KtxRoom> foundRoom2 = ktxRooms2.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                reservation2.setRoomName(foundRoom2.get().getRoomName());
                reservation2.setGrade(foundRoom2.get().getGrade());

                //자리차지
                KtxSeatNormal foundSeat2 = (KtxSeatNormal) ktxSeatService.findKtxSeat(foundRoom2.get().getKtxSeat().getId()).get();
                reservation2.setSeats(normalSeatDto.returnSeats());
                foundSeat2.normalDtoToEntity(normalSeatDto);

                //deploy
                reservation.setDeploy(deploy.get());
                reservation2.setDeploy(deploy2.get());

                //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
                HttpSession session = request.getSession();
                String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
                Member foundMember = memberService.findByLoginId(loginId).orElse(null);

                if (foundMember != null) {
                    reservation.setMember(foundMember);
                    reservation2.setMember(foundMember);
                }

                //passenger가 있어야 되나?? => 어떤 고객층이 많이 이용하는지에 대한 통계성 쿼리 낼 때 사용하면 될 듯
                Passenger passenger = passengerDto.dtotoPassenger();
                Passenger passenger2 = passengerDto.dtotoPassenger();

                passengerService.save(passenger);
                passengerService.save(passenger2);

                reservation.setPassenger(passenger);
                reservation.setFee(passengerDto.getFee(reservation.getGrade()));

                reservation2.setPassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getGrade()));

                //reservation을 db에 저장
                reservationService.saveReservation(reservation);
                reservationService.saveReservation(reservation2);
                //이 값을 초기화해줘야지 일반 => 특실 전환 등의 작업이 가능함
                beforeNormalSeatDto = null;

                return "redirect:/my-page";
            }
        }
//round vs one-way--------------------------------------------------------------------------------------------------------------------------------------
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

        Map map = objectMapper.convertValue(roomDto, Map.class);
        Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

        if(roomChange.isPresent()){
            for (Object key : map.keySet()) {
                if (map.get(key) != null) {
                    targetRoomName = (String) key;
                    //이게 맞을 듯
                    break;
                }
            }

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName)).findAny();

            normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

            model.addAttribute("going", true);

            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoomName);

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "chooseNormalSeat";
        }

        else {
            if(normalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", roomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseNormalSeat";
            }

            //success logic
            Reservation reservation = new Reservation();

//            Long deployId = deployForm.getDeployIdOfGoing();
//            Optional<Deploy> deploy = deployService.findDeploy(deployId);
//            Long trainId = deploy.get().getTrain().getId();
//            Ktx ktx = ktxService.findKtx(trainId).get();
//            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);

            Deploy deploy = deployService.getDeployWithTrain(deployForm.getDeployIdOfGoing());
            Ktx train = (Ktx) deploy.getTrain();
            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomWithSeatFetch(((Ktx) deploy.getTrain()).getId());

            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
            reservation.setRoomName(foundRoom.get().getRoomName());
            reservation.setGrade(foundRoom.get().getGrade());

            //자리차지
            KtxSeatNormal foundSeat = (KtxSeatNormal) ktxSeatService.findKtxSeat(foundRoom.get().getKtxSeat().getId()).get();
            reservation.setSeats(normalSeatDto.returnSeats());
            foundSeat.normalDtoToEntity(normalSeatDto);

            //deploy
            reservation.setDeploy(deploy);

            //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
            HttpSession session = request.getSession();
            String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
            Member foundMember = memberService.findByLoginId(loginId).orElse(null);
            log.info("시발 = {}",foundMember);

            if (foundMember != null) {
                reservation.setMember(foundMember);
            }

            //passenger가 있어야 되나?? => 어떤 고객층이 많이 이용하는지에 대한 통계성 쿼리 낼 때 사용하면 될 듯
            Passenger passenger = passengerDto.dtotoPassenger();
            passengerService.save(passenger);
            reservation.setPassenger(passenger);
            reservation.setFee(passengerDto.getFee(reservation.getGrade()));

            //reservation을 db에 저장
            reservationService.saveReservation(reservation);

            return "redirect:/my-page";
        }
    }

    @PostMapping("/reservation/vip")
    //postmapping에 Transactional 거는 게 에바라는 의견이 좀 있고 본인도 그렇게 생각, service쪽으로 빼야 될 것 같음(이래도 동작은 됨)
//    @Transactional
    public String reserveVip(@ModelAttribute VipSeatDto vipSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
                          @ModelAttribute CheckRoomDto checkRoomDto,
                          @RequestParam String roomName,
                          @RequestParam(required = false) String departurePlace,
                          @RequestParam(required = false) String arrivalPlace,
                          @RequestParam(required = false) String dateTimeOfGoing,
                          @RequestParam(required = false) String dateTimeOfLeaving,
                          HttpServletRequest request,
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
                        targetRoomName = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName)).findAny();

                vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseVipSeat";
            }

            else {
                if(vipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfGoing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "chooseVipSeat";
                }

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                beforeRoomName = roomName;
                beforeVipSeatDto = vipSeatDto;
                beforeNormalSeatDto = null;

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
                        targetRoomName = (String) key;
                    }
                }

                Long deployId = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName)).findAny();

                vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseVipSeat";
            }

            else {
                if(vipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

                    Long deployId = deployForm.getDeployIdOfComing();
                    Optional<Deploy> deploy = deployService.findDeploy(deployId);
                    Long trainId = deploy.get().getTrain().getId();

                    Ktx ktx = ktxService.findKtx(trainId).get();
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    return "chooseVipSeat";
                }

                //success logic
                Reservation reservation = new Reservation();
                Reservation reservation2 = new Reservation();

                //갈 때
                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();
                //어떻게 프록시가 아니라 진짜 엔티티(ktxRoom)를 들고 있지? 어디서 이미 1차 캐시에 들어갔나?
                //query 최적화하면 됨
                Ktx ktx = ktxService.findKtx(trainId).get();
                log.info("fuck555 = {}",ktx.getKtxRooms().get(0).getClass());
                log.info("fuck555 = {}",ktx.getKtxRooms().get(1).getClass());
                log.info("fuck555 = {}",ktx.getKtxRooms());

                //자리차지
                if (beforeVipSeatDto != null) {
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    log.info("fuck555 = {}",ktxRooms.getClass());
                    log.info("fuck555 = {}",ktxRooms);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    KtxSeatVip foundSeat = (KtxSeatVip) ktxSeatService.findKtxSeat(foundRoom.get().getKtxSeat().getId()).get();
                    reservation.setSeats(beforeVipSeatDto.returnSeats());
                    foundSeat.vipDtoToEntity(beforeVipSeatDto);
                }
                else {
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    log.info("fuck555 = {}",ktxRooms.get(0).getClass());
                    log.info("fuck555 = {}",ktxRooms);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    //얘 갑자기 in query 왜 날림??
                    KtxSeatVip foundSeat = (KtxSeatVip) ktxSeatService.findKtxSeat(foundRoom.get().getKtxSeat().getId()).get();
                    reservation.setSeats(beforeNormalSeatDto.returnSeats());
                    foundSeat.normalDtoToEntity(beforeNormalSeatDto);
                }

                //올 떄
                Long deployId2 = deployForm.getDeployIdOfComing();
                Optional<Deploy> deploy2 = deployService.findDeploy(deployId2);
                Long trainId2 = deploy2.get().getTrain().getId();

                Ktx ktx2 = ktxService.findKtx(trainId2).get();
                List<KtxRoom> ktxRooms2 = ktxRoomService.findByKtxAndGrade(ktx2, Grade.VIP);
                Optional<KtxRoom> foundRoom2 = ktxRooms2.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                reservation2.setRoomName(foundRoom2.get().getRoomName());
                reservation2.setGrade(foundRoom2.get().getGrade());

                //자리차지
                KtxSeatVip foundSeat2 = (KtxSeatVip) ktxSeatService.findKtxSeat(foundRoom2.get().getKtxSeat().getId()).get();
                reservation2.setSeats(vipSeatDto.returnSeats());
                foundSeat2.vipDtoToEntity(vipSeatDto);

                //deploy
                reservation.setDeploy(deploy.get());
                reservation2.setDeploy(deploy2.get());

                //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
                HttpSession session = request.getSession();
                String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
                Member foundMember = memberService.findByLoginId(loginId).orElse(null);
                log.info("시발 = {}",foundMember);

                if (foundMember != null) {
                    reservation.setMember(foundMember);
                    reservation2.setMember(foundMember);
                }

                //passenger가 있어야 되나?? => 어떤 고객층이 많이 이용하는지에 대한 통계성 쿼리 낼 때 사용하면 될 듯
                Passenger passenger = passengerDto.dtotoPassenger();
                Passenger passenger2 = passengerDto.dtotoPassenger();

                passengerService.save(passenger);
                passengerService.save(passenger2);

                reservation.setPassenger(passenger);
                reservation.setFee(passengerDto.getFee(reservation.getGrade()));

                reservation2.setPassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getGrade()));

                //reservation을 db에 저장
                reservationService.saveReservation(reservation);
                reservationService.saveReservation(reservation2);
                //이 값을 초기화해줘야지 일반 => 특실 전환 등의 작업이 가능함
                beforeVipSeatDto = null;

                return "redirect:/my-page";
            }
        }
//round vs one-way--------------------------------------------------------------------------------------------------------------------------------------
        LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);

        System.out.println("roomDto = " + roomDto);
        Map map = objectMapper.convertValue(roomDto, Map.class);
        Optional roomChange = map.values().stream().filter(r -> r != null).findFirst();

        if(roomChange.isPresent()){
            for (Object key : map.keySet()) {
                if (map.get(key) != null) {
                    targetRoomName = (String) key;
                }
            }

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName)).findAny();

            vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoomName);

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "chooseVipSeat";
        }

        else {
            if(vipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

                Long deployId = deployForm.getDeployIdOfGoing();
                Optional<Deploy> deploy = deployService.findDeploy(deployId);
                Long trainId = deploy.get().getTrain().getId();

                Ktx ktx = ktxService.findKtx(trainId).get();
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                //이거 빠져 있었음
                model.addAttribute("roomName", roomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseVipSeat";
            }

            //success logic
            Reservation reservation = new Reservation();

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
            reservation.setRoomName(foundRoom.get().getRoomName());
            reservation.setGrade(foundRoom.get().getGrade());

            //자리차지
            KtxSeatVip foundSeat = (KtxSeatVip) ktxSeatService.findKtxSeat(foundRoom.get().getKtxSeat().getId()).get();
            reservation.setSeats(vipSeatDto.returnSeats());
            foundSeat.vipDtoToEntity(vipSeatDto);

            //deploy
            reservation.setDeploy(deploy.get());

            //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
            HttpSession session = request.getSession();
            String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
            Member foundMember = memberService.findByLoginId(loginId).orElse(null);
            log.info("시발 = {}",foundMember);

            if (foundMember != null) {
                reservation.setMember(foundMember);
            }

            //passenger가 있어야 되나?? => 어떤 고객층이 많이 이용하는지에 대한 통계성 쿼리 낼 때 사용하면 될 듯
            Passenger passenger = passengerDto.dtotoPassenger();
            passengerService.save(passenger);
            reservation.setPassenger(passenger);
            reservation.setFee(passengerDto.getFee(reservation.getGrade()));

            //reservation을 db에 저장
            reservationService.saveReservation(reservation);

            return "redirect:/my-page";
        }
    }

    private LocalDateTime getLocalDateTime(String dateTime) {
        DateTimeFormatter formatter = DateTimeFormatter.ISO_DATE_TIME;
        return LocalDateTime.parse(dateTime, formatter);
    }
}
