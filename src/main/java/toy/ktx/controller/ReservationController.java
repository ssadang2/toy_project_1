package toy.ktx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Passenger;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.dto.DeployForm;
import toy.ktx.domain.dto.PassengerDto;
import toy.ktx.domain.dto.RoomDto;
import toy.ktx.domain.dto.projections.NormalSeatDto;
import toy.ktx.domain.dto.projections.VipSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.Ktx;
import toy.ktx.domain.ktx.KtxRoom;
import toy.ktx.domain.ktx.KtxSeat;
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
    private final KtxService ktxService;
    private final DeployService deployService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final PassengerService passengerService;

    //공유변수 주의하자
    private String targetRoom = null;
    private ObjectMapper objectMapper = new ObjectMapper();
    private NormalSeatDto beforeNormalSeatDto = null;
    private VipSeatDto beforeVipSeatDto = null;
    private String beforeRoomName = null;

    @PostMapping("/reservation/normal")
    //postmapping에 Transactional 거는 게 에바라는 의견이 좀 있고 본인도 그렇게 생각, service쪽으로 빼야 될 것 같음(이래도 동작은 됨)
    @Transactional
    public String reserveNormal(@ModelAttribute NormalSeatDto normalSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
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
                model.addAttribute("roomName", targetRoom);

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

                    normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

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
                model.addAttribute("roomName", targetRoom);

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

                    normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

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

                    KtxSeat foundSeat = ktxSeatService.findByKtxRoom(foundRoom.get()).get();
                    reservation.setSeats(beforeNormalSeatDto.returnSeats());
                    foundSeat.normalDtoToEntity(beforeNormalSeatDto);
                }
                else{
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    KtxSeat foundSeat = ktxSeatService.findByKtxRoom(foundRoom.get()).get();
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
                KtxSeat foundSeat2 = ktxSeatService.findByKtxRoom(foundRoom2.get()).get();
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

                reservation.savePassenger(passenger);
                reservation.setFee(passengerDto.getFee(reservation.getGrade()));

                reservation2.savePassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getGrade()));

                //reservation을 db에 저장
                reservationService.saveReservation(reservation);
                reservationService.saveReservation(reservation2);
                //이 값을 초기화해줘야지 일반 => 특실 전환 등의 작업이 가능함
                beforeNormalSeatDto = null;

                return "redirect:/my-page";
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

            model.addAttribute("going", true);

            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoom);

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

                normalSeatDto = ktxSeatService.findNormalDtoByKtxRoom(foundRoom.get());

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

                return "chooseNormalSeat";
            }

            //success logic
            Reservation reservation = new Reservation();

            Long deployId = deployForm.getDeployIdOfGoing();
            Optional<Deploy> deploy = deployService.findDeploy(deployId);
            Long trainId = deploy.get().getTrain().getId();

            Ktx ktx = ktxService.findKtx(trainId).get();
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
            reservation.setRoomName(foundRoom.get().getRoomName());
            reservation.setGrade(foundRoom.get().getGrade());

            //자리차지
            KtxSeat foundSeat = ktxSeatService.findByKtxRoom(foundRoom.get()).get();
            reservation.setSeats(normalSeatDto.returnSeats());
            foundSeat.normalDtoToEntity(normalSeatDto);

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
            reservation.savePassenger(passenger);
            reservation.setFee(passengerDto.getFee(reservation.getGrade()));

            //reservation을 db에 저장
            reservationService.saveReservation(reservation);

            return "redirect:/my-page";
        }
    }

    @PostMapping("/reservation/vip")
    //postmapping에 Transactional 거는 게 에바라는 의견이 좀 있고 본인도 그렇게 생각, service쪽으로 빼야 될 것 같음(이래도 동작은 됨)
    @Transactional
    public String reserveVip(@ModelAttribute VipSeatDto vipSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
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
                model.addAttribute("roomName", targetRoom);

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

                    vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

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
                model.addAttribute("roomName", targetRoom);

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

                    vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

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
                Ktx ktx = ktxService.findKtx(trainId).get();

                //자리차지
                if (beforeVipSeatDto != null) {
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    KtxSeat foundSeat = ktxSeatService.findByKtxRoom(foundRoom.get()).get();
                    reservation.setSeats(beforeVipSeatDto.returnSeats());
                    foundSeat.vipDtoToEntity(beforeVipSeatDto);
                }
                else {
                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(foundRoom.get().getRoomName());
                    reservation.setGrade(foundRoom.get().getGrade());

                    KtxSeat foundSeat = ktxSeatService.findByKtxRoom(foundRoom.get()).get();
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
                KtxSeat foundSeat2 = ktxSeatService.findByKtxRoom(foundRoom2.get()).get();
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

                reservation.savePassenger(passenger);
                reservation.setFee(passengerDto.getFee(reservation.getGrade()));

                reservation2.savePassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getGrade()));

                //reservation을 db에 저장
                reservationService.saveReservation(reservation);
                reservationService.saveReservation(reservation2);
                //이 값을 초기화해줘야지 일반 => 특실 전환 등의 작업이 가능함
                beforeVipSeatDto = null;

                return "redirect:/my-page";
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

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoom);

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

                vipSeatDto = ktxSeatService.findVipDtoByKtxRoom(foundRoom.get());

                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

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
            KtxSeat foundSeat = ktxSeatService.findByKtxRoom(foundRoom.get()).get();
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
            reservation.savePassenger(passenger);
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
