package toy.ktx.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class ReservationController {

    private final KtxRoomService ktxRoomService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;
    private final DeployService deployService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final PassengerService passengerService;

    //공유변수 주의하자(동시성 문제)
    private final ObjectMapper objectMapper = new ObjectMapper();
    // logic 상 굳이 Remove할 필요없을 듯
    private ThreadLocal<String> targetRoomName = new ThreadLocal<>();

    @PostMapping("/reservation/normal")
    public String reserveNormal(@ModelAttribute NormalSeatDto normalSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
                          @ModelAttribute CheckRoomDto checkRoomDto,
                          @RequestParam String roomName,
                          @RequestParam(required = false) String beforeRoomName,
                          @RequestParam(required = false) String departurePlace,
                          @RequestParam(required = false) String arrivalPlace,
                          @RequestParam(required = false) String dateTimeOfGoing,
                          @RequestParam(required = false) String dateTimeOfLeaving,
                          @RequestParam(required = false) Boolean beforeNormal,
                          @RequestParam(required = false) String beforeChosenSeats,
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
                        targetRoomName.set((String) key);
                        break;
                    }
                }

                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                //seat까지 fetch 안 하는 게 맞을까?
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

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
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseNormalSeat";
            }

            else {
                if(normalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

//                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
//                    Ktx ktx = (Ktx) deploy.getTrain();
//
//                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
//                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
//
//                    normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);

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
                //올 때 고를 때 일반실/특실 좌석 체크 Logic
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
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

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                model.addAttribute("beforeRoomName", roomName);
                model.addAttribute("beforeNormal", true);
                model.addAttribute("beforeVip", false);
                model.addAttribute("beforeChosenSeats", normalSeatDto.returnSeats());

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
                        targetRoomName.set((String) key);
                        break;
                    }
                }
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                model.addAttribute("beforeRoomName", beforeRoomName);
                model.addAttribute("beforeNormal", beforeNormal);
                model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseNormalSeat";
            }

            else {
                if(normalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

//                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
//                    Ktx ktx = (Ktx) deploy.getTrain();
//
//                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
//                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
//
//                    normalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    //updated
                    model.addAttribute("beforeRoomName", beforeRoomName);
                    model.addAttribute("beforeNormal", beforeNormal);
                    model.addAttribute("beforeChosenSeats", beforeChosenSeats);

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
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                //자리차지
                if (beforeNormal) {
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.NORMAL);
                    Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                    reservation.setGrade(optionalKtxRoom.get().getGrade());

                    KtxSeatNormal foundSeat = (KtxSeatNormal) optionalKtxRoom.get().getKtxSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                }
                else{
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
                    Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                    reservation.setGrade(optionalKtxRoom.get().getGrade());

                    //updated point
                    KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                }

                //올 떄
                Deploy deploy2 = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                Ktx ktx2 = (Ktx) deploy2.getTrain();

                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx2, Grade.NORMAL);
                Optional<KtxRoom> optionalKtxRoom2 = ktxRooms2.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                reservation2.setRoomName(optionalKtxRoom2.get().getRoomName());
                reservation2.setGrade(optionalKtxRoom2.get().getGrade());

                //자리차지
                KtxSeatNormal foundSeat2 = (KtxSeatNormal) optionalKtxRoom2.get().getKtxSeat();
                reservation2.setSeats(normalSeatDto.returnSeats());
                foundSeat2.normalDtoToEntity(normalSeatDto);

                //deploy
                reservation.setDeploy(deploy);
                reservation2.setDeploy(deploy2);

                //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
                HttpSession session = request.getSession();
                String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
                Member foundMember = memberService.findByLoginId(loginId).orElse(null);

                if (foundMember != null) {
                    reservation.setMember(foundMember);
                    reservation2.setMember(foundMember);
                }

                //passenger entity가 꼭 있어야 되나?? => 어떤 고객층이 많이 이용하는지에 대한 통계성 쿼리 낼 때 사용하면 될 듯
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
                    targetRoomName.set((String) key);
                    break;
                }
            }

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();
            //굳이 seat까지 Fetch 안 해도 됨
            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
            Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

            //1차 캐시에서 찾아오겠지?? => dto projection이라 1차 캐시에서 못 긁어 오는 듯
            normalSeatDto = ktxSeatNormalService.findNormalDtoById(optionalKtxRoom.get().getKtxSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(normalSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoomName.get());

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "chooseNormalSeat";
        }

        else {
            if(normalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

//                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
//                Ktx ktx = (Ktx) deploy.getTrain();
//
//                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
//                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
//
//                normalSeatDto = ktxSeatNormalService.findNormalDtoById(optionalKtxRoom.get().getKtxSeat().getId());

                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);

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
            //select query 3개
            Reservation reservation = new Reservation();

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            //seat까지 fetch 안 하면 KtxSeatNormal 다운 캐스팅 부분에서 에러터짐 => 프록시이기 때문에
            //join 때문에 db에 부하가 가더라도 이 방법이 맞을 듯
            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.NORMAL);
            Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

            reservation.setRoomName(optionalKtxRoom.get().getRoomName());
            reservation.setGrade(optionalKtxRoom.get().getGrade());

            //자리차지
            KtxSeatNormal foundSeat = (KtxSeatNormal) optionalKtxRoom.get().getKtxSeat();
            reservation.setSeats(normalSeatDto.returnSeats());
            foundSeat.normalDtoToEntity(normalSeatDto);

            //deploy
            reservation.setDeploy(deploy);

            //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
            HttpSession session = request.getSession();
            String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
            Member foundMember = memberService.findByLoginId(loginId).orElse(null);

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
    public String reserveVip(@ModelAttribute VipSeatDto vipSeatDto,
                          @ModelAttribute DeployForm deployForm,
                          @ModelAttribute PassengerDto passengerDto,
                          @RequestParam(required = false) Boolean round,
                          @RequestParam(required = false) Boolean going,
                          @RequestParam(required = false) Boolean coming,
                          @ModelAttribute RoomDto roomDto,
                          @ModelAttribute CheckRoomDto checkRoomDto,
                          @RequestParam String roomName,
                          @RequestParam(required = false) String beforeRoomName,
                          @RequestParam(required = false) String departurePlace,
                          @RequestParam(required = false) String arrivalPlace,
                          @RequestParam(required = false) String dateTimeOfGoing,
                          @RequestParam(required = false) String dateTimeOfLeaving,
                          @RequestParam(required = false) Boolean beforeVip,
                          @RequestParam(required = false) String beforeChosenSeats,
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
                        targetRoomName.set((String) key);
                    }
                }

                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

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
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseVipSeat";
            }

            else {
                if(vipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

//                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
//                    Ktx ktx = (Ktx) deploy.getTrain();
//
//                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
//                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
//
//                    vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);

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
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
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

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                model.addAttribute("beforeRoomName", roomName);
                model.addAttribute("beforeVip", true);
                model.addAttribute("beforeNormal", false);
                model.addAttribute("beforeChosenSeats", vipSeatDto.returnSeats());

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
                        targetRoomName.set((String) key);
                        break;
                    }
                }

                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfLeaving", afterDateTime);

                //updated
                model.addAttribute("beforeRoomName", beforeRoomName);
                model.addAttribute("beforeVip", beforeVip);
                model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "chooseVipSeat";
            }

            else {
                if(vipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

//                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
//                    Ktx ktx = (Ktx) deploy.getTrain();
//
//                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
//                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
//                    vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfLeaving", afterDateTime);

                    //updated
                    model.addAttribute("beforeRoomName", beforeRoomName);
                    model.addAttribute("beforeVip", beforeVip);
                    model.addAttribute("beforeChosenSeats", beforeChosenSeats);

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
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                //자리차지
                if (beforeVip == true) {
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
                    Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                    reservation.setGrade(optionalKtxRoom.get().getGrade());

                    KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                }
                else {
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.NORMAL);
                    Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                    reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                    reservation.setGrade(optionalKtxRoom.get().getGrade());

                    KtxSeatNormal foundSeat = (KtxSeatNormal) optionalKtxRoom.get().getKtxSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                }

                //올 떄
                Deploy deploy2 = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                Ktx ktx2 = (Ktx) deploy2.getTrain();

                List<KtxRoom> ktxRooms2 = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx2, Grade.VIP);
                Optional<KtxRoom> optionalKtxRoom2 = ktxRooms2.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
                reservation2.setRoomName(optionalKtxRoom2.get().getRoomName());
                reservation2.setGrade(optionalKtxRoom2.get().getGrade());

                //자리차지
                KtxSeatVip foundSeat2 = (KtxSeatVip) optionalKtxRoom2.get().getKtxSeat();
                reservation2.setSeats(vipSeatDto.returnSeats());
                foundSeat2.vipDtoToEntity(vipSeatDto);

                //deploy
                reservation.setDeploy(deploy);
                reservation2.setDeploy(deploy2);

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
                    targetRoomName.set((String) key);
                    break;
                }
            }

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
            Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();
            vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(vipSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoomName.get());

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "chooseVipSeat";
        }

        else {
            if(vipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {

//                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
//                Ktx ktx = (Ktx) deploy.getTrain();
//
//                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
//                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
//                vipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);

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

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Ktx ktx = (Ktx) deploy.getTrain();

            List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
            Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();
            reservation.setRoomName(optionalKtxRoom.get().getRoomName());
            reservation.setGrade(optionalKtxRoom.get().getGrade());

            //자리차지
            KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
            reservation.setSeats(vipSeatDto.returnSeats());
            foundSeat.vipDtoToEntity(vipSeatDto);

            //deploy
            reservation.setDeploy(deploy);

            //여기까지 멤버를 넘겨야 될 듯 => 세션에 로그인아이디를 담는 방법으로 해결
            HttpSession session = request.getSession();
            String loginId = (String) session.getAttribute(SessionConst.LOGIN_MEMBER_ID);
            Member foundMember = memberService.findByLoginId(loginId).orElse(null);

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
