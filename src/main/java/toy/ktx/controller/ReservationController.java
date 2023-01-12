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
import toy.ktx.domain.dto.projections.KtxNormalSeatDto;
import toy.ktx.domain.dto.projections.KtxVipSeatDto;
import toy.ktx.domain.dto.projections.MugunghwaSeatDto;
import toy.ktx.domain.dto.projections.SaemaulSeatDto;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.domain.saemaul.SaemaulSeat;
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
    private final MugunghwaRoomService mugunghwaRoomService;
    private final SaemaulRoomService saemaulRoomService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;
    private final MugunghwaSeatService mugunghwaSeatService;
    private final SaemaulSeatService saemaulSeatService;
    private final DeployService deployService;
    private final MemberService memberService;
    private final ReservationService reservationService;
    private final PassengerService passengerService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    //공유변수 주의하자(동시성 문제)
    // logic 상 굳이 remove할 필요없을 듯
    private ThreadLocal<String> targetRoomName = new ThreadLocal<>();
    // logic 상 굳이 remove할 필요없을 듯
    private ThreadLocal<List<String>> okList = new ThreadLocal<>();

    @PostMapping("/reservation/ktx/normal")
    public String reserveKtxNormal(@ModelAttribute KtxNormalSeatDto ktxNormalSeatDto,
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
                          @RequestParam(required = false) String dateTimeOfComing,
                          @RequestParam(required = false) String beforeRoomName,
                          @RequestParam(required = false) Boolean beforeNormal,
                          @RequestParam(required = false) Boolean beforeVip,
                          @RequestParam(required = false) String beforeChosenSeats,
                          HttpServletRequest request,
                          Model model) {

        model.addAttribute("passengers", passengerDto.howManyOccupied());
        okList.set(new ArrayList<>());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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

                //seat까지 fetch 안 하는 게 맞을까? => 맞음 섣부른 Fetch join임
                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                ktxNormalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(ktxNormalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseKtxNormalSeat";
            }

            else {
                if(ktxNormalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    ktxNormalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(ktxNormalSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseKtxNormalSeat";
                }
                //success logic
                //올 때 좌석 유무 체크 및 해당 페이지 이동 Logic
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());

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

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", true);
                    model.addAttribute("beforeChosenSeats", ktxNormalSeatDto.returnSeats());

                    return "normalVip";
                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    MugunghwaRoom targetRoom = null;

                    for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                        if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(mugunghwaRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalMugunghwaRoom.isPresent()) {
                            targetRoom = optionalMugunghwaRoom.get();
                            break;
                        }
                    }
                    MugunghwaSeatDto targetMugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(targetRoom.getMugunghwaSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetMugunghwaSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);
                    model.addAttribute("okList", okList.get());

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", true);
                    model.addAttribute("beforeChosenSeats", ktxNormalSeatDto.returnSeats());

                    return "trainseat/chooseMugunghwaSeat";
                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //query 몇 개 나가는지 보기
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    SaemaulRoom targetRoom = null;

                    for (SaemaulRoom saemaulRoom : saemaulRooms) {
                        if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(saemaulRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalSaemaulRoom.isPresent()) {
                            targetRoom = optionalSaemaulRoom.get();
                            break;
                        }
                    }
                    SaemaulSeatDto targetSaemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(targetRoom.getSaemaulSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetSaemaulSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("saemaulRooms", saemaulRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", true);
                    model.addAttribute("beforeChosenSeats", ktxNormalSeatDto.returnSeats());

                    return "trainseat/chooseSaemaulSeat";
                }
            }
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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

                ktxNormalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                model.addAttribute("beforeRoomName", beforeRoomName);
                model.addAttribute("beforeNormal", beforeNormal);
                model.addAttribute("beforeVip", beforeVip);
                model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(ktxNormalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseKtxNormalSeat";
            }

            else {
                if(ktxNormalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    ktxNormalSeatDto = ktxSeatNormalService.findNormalDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    //updated
                    model.addAttribute("beforeRoomName", beforeRoomName);
                    model.addAttribute("beforeNormal", beforeNormal);
                    model.addAttribute("beforeVip", beforeVip);
                    model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(ktxNormalSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseKtxNormalSeat";
                }
                //success logic
                Reservation reservation = new Reservation();
                Reservation reservation2 = new Reservation();

                //갈 때
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());

                if (deploy.getTrain().getTrainName().contains("KTX")) {
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
                    } else if(beforeVip) {
                        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
                        Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                        reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                        reservation.setGrade(optionalKtxRoom.get().getGrade());

                        //updated point
                        KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
                        reservation.setSeats(beforeChosenSeats);
                        foundSeat.checkSeats(beforeChosenSeats);
                    }

                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    //자리차지
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalMugunghwaRoom.get().getRoomName());
                    MugunghwaSeat foundSeat = optionalMugunghwaRoom.get().getMugunghwaSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);

                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //자리차지
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalSaemaulRoom.get().getRoomName());
                    SaemaulSeat foundSeat = optionalSaemaulRoom.get().getSaemaulSeat();
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
                reservation2.setSeats(ktxNormalSeatDto.returnSeats());
                foundSeat2.normalDtoToEntity(ktxNormalSeatDto);

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
                reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

                reservation2.setPassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getDeploy().getTrain(), reservation2.getGrade()));

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
            ktxNormalSeatDto = ktxSeatNormalService.findNormalDtoById(optionalKtxRoom.get().getKtxSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(ktxNormalSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoomName.get());

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "trainseat/chooseKtxNormalSeat";
        }

        else {
            if(ktxNormalSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.NORMAL);
                Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                //1차 캐시에서 찾아오겠지?? => dto projection이라 1차 캐시에서 못 긁어 오는 듯
                ktxNormalSeatDto = ktxSeatNormalService.findNormalDtoById(optionalKtxRoom.get().getKtxSeat().getId());


                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(ktxNormalSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", roomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseKtxNormalSeat";
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
            reservation.setSeats(ktxNormalSeatDto.returnSeats());
            foundSeat.normalDtoToEntity(ktxNormalSeatDto);

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
            reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

            //reservation을 db에 저장
            reservationService.saveReservation(reservation);

            return "redirect:/my-page";
        }
    }

    @PostMapping("/reservation/ktx/vip")
    public String reserveKtxVip(@ModelAttribute KtxVipSeatDto ktxVipSeatDto,
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
                          @RequestParam(required = false) String dateTimeOfComing,
                          @RequestParam(required = false) Boolean beforeNormal,
                          @RequestParam(required = false) Boolean beforeVip,
                          @RequestParam(required = false) String beforeChosenSeats,
                          HttpServletRequest request,
                          Model model) {

        model.addAttribute("passengers", passengerDto.howManyOccupied());
        okList.set(new ArrayList<>());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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

                ktxVipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseKtxVipSeat";
            }

            else {
                if(ktxVipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    ktxVipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseKtxVipSeat";
                }
                //success logic
                //올 때 좌석 유무 체크 및 해당 페이지 이동 Logic
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());

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

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", true);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", ktxVipSeatDto.returnSeats());

                    return "normalVip";
                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    MugunghwaRoom targetRoom = null;

                    for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                        if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(mugunghwaRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalMugunghwaRoom.isPresent()) {
                            targetRoom = optionalMugunghwaRoom.get();
                            break;
                        }
                    }
                    MugunghwaSeatDto targetMugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(targetRoom.getMugunghwaSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetMugunghwaSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", true);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", ktxVipSeatDto.returnSeats());

                    return "trainseat/chooseMugunghwaSeat";
                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    SaemaulRoom targetRoom = null;

                    for (SaemaulRoom saemaulRoom : saemaulRooms) {
                        if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(saemaulRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalSaemaulRoom.isPresent()) {
                            targetRoom = optionalSaemaulRoom.get();
                            break;
                        }
                    }
                    SaemaulSeatDto targetSaemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(targetRoom.getSaemaulSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetSaemaulSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("saemaulRooms", saemaulRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", true);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", ktxVipSeatDto.returnSeats());

                    return "trainseat/chooseSaemaulSeat";
                }
            }
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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

                ktxVipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                //updated
                model.addAttribute("beforeRoomName", beforeRoomName);
                model.addAttribute("beforeNormal", beforeNormal);
                model.addAttribute("beforeVip", beforeVip);
                model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseKtxVipSeat";
            }

            else {
                if(ktxVipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                    Ktx ktx = (Ktx) deploy.getTrain();

                    List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                    Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    ktxVipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    //updated
                    model.addAttribute("beforeRoomName", beforeRoomName);
                    model.addAttribute("beforeNormal", beforeNormal);
                    model.addAttribute("beforeVip", beforeVip);
                    model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("ktxRooms", ktxRooms);
                    model.addAttribute("roomName", roomName);

                    return "trainseat/chooseKtxVipSeat";
                }
                //success logic
                Reservation reservation = new Reservation();
                Reservation reservation2 = new Reservation();

                //갈 때
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());

                if (deploy.getTrain().getTrainName().contains("KTX")) {
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
                    } else if(beforeVip) {
                        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
                        Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                        reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                        reservation.setGrade(optionalKtxRoom.get().getGrade());

                        //updated point
                        KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
                        reservation.setSeats(beforeChosenSeats);
                        foundSeat.checkSeats(beforeChosenSeats);
                    }

                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    //자리차지
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalMugunghwaRoom.get().getRoomName());
                    MugunghwaSeat foundSeat = optionalMugunghwaRoom.get().getMugunghwaSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //자리차지
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalSaemaulRoom.get().getRoomName());
                    SaemaulSeat foundSeat = optionalSaemaulRoom.get().getSaemaulSeat();
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
                reservation2.setSeats(ktxVipSeatDto.returnSeats());
                foundSeat2.vipDtoToEntity(ktxVipSeatDto);

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
                reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

                reservation2.setPassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getDeploy().getTrain(), reservation2.getGrade()));

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
            ktxVipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("ktxRooms", ktxRooms);
            model.addAttribute("roomName", targetRoomName.get());

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "trainseat/chooseKtxVipSeat";
        }

        else {
            if(ktxVipSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Ktx ktx = (Ktx) deploy.getTrain();

                List<KtxRoom> ktxRooms = ktxRoomService.findByKtxAndGrade(ktx, Grade.VIP);
                Optional<KtxRoom> foundRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                ktxVipSeatDto = ktxSeatVipService.findVipDtoById(foundRoom.get().getKtxSeat().getId());

                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(ktxVipSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("ktxRooms", ktxRooms);
                //이거 빠져 있었음
                model.addAttribute("roomName", roomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseKtxVipSeat";
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
            reservation.setSeats(ktxVipSeatDto.returnSeats());
            foundSeat.vipDtoToEntity(ktxVipSeatDto);

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
            reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

            //reservation을 db에 저장
            reservationService.saveReservation(reservation);

            return "redirect:/my-page";
        }
    }

    @PostMapping("/reservation/mugunghwa")
    public String reserveMugunghwa(@ModelAttribute MugunghwaSeatDto mugunghwaSeatDto,
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
                                   @RequestParam(required = false) String dateTimeOfComing,
                                   @RequestParam(required = false) String beforeRoomName,
                                   @RequestParam(required = false) Boolean beforeNormal,
                                   @RequestParam(required = false) Boolean beforeVip,
                                   @RequestParam(required = false) String beforeChosenSeats,
                                   HttpServletRequest request,
                                   Model model) {

        model.addAttribute("passengers", passengerDto.howManyOccupied());
        okList.set(new ArrayList<>());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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
                Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                //query 몇 개 나가는지 보기 => 3개 나감 dto 조회는 1차 캐시에서 찾아오는 작업을 거치지 않는 듯 2개 x
                List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.findAllByMugunghwa(mugunghwa);
                Optional<MugunghwaRoom> foundRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(foundRoom.get().getMugunghwaSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseMugunghwaSeat";
            }

            else {
                if(mugunghwaSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.findAllByMugunghwa(mugunghwa);
                    Optional<MugunghwaRoom> foundRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(foundRoom.get().getMugunghwaSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseMugunghwaSeat";
                }
                //success logic
                //올 때 좌석 유무 체크 및 해당 페이지 이동 Logic
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());

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

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", mugunghwaSeatDto.returnSeats());

                    return "normalVip";
                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    MugunghwaRoom targetRoom = null;

                    for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                        if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(mugunghwaRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalMugunghwaRoom.isPresent()) {
                            targetRoom = optionalMugunghwaRoom.get();
                            break;
                        }
                    }
                    MugunghwaSeatDto targetMugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(targetRoom.getMugunghwaSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetMugunghwaSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", mugunghwaSeatDto.returnSeats());

                    return "trainseat/chooseMugunghwaSeat";
                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //query 몇 개 나가는지 보기
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    SaemaulRoom targetRoom = null;

                    for (SaemaulRoom saemaulRoom : saemaulRooms) {
                        if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(saemaulRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalSaemaulRoom.isPresent()) {
                            targetRoom = optionalSaemaulRoom.get();
                            break;
                        }
                    }
                    SaemaulSeatDto targetSaemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(targetRoom.getSaemaulSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetSaemaulSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("saemaulRooms", saemaulRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", mugunghwaSeatDto.returnSeats());

                    return "trainseat/chooseSaemaulSeat";
                }
            }
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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
                Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

//                List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.findAllByMugunghwa(mugunghwa);
                Optional<MugunghwaRoom> foundRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(foundRoom.get().getMugunghwaSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                model.addAttribute("beforeRoomName", beforeRoomName);
                model.addAttribute("beforeNormal", beforeNormal);
                model.addAttribute("beforeVip", beforeVip);
                model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseMugunghwaSeat";
            }

            else {
                if(mugunghwaSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

//                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.findAllByMugunghwa(mugunghwa);
                    Optional<MugunghwaRoom> foundRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(foundRoom.get().getMugunghwaSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    //updated
                    model.addAttribute("beforeRoomName", beforeRoomName);
                    model.addAttribute("beforeNormal", beforeNormal);
                    model.addAttribute("beforeVip", beforeVip);
                    model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseMugunghwaSeat";
                }
                //success logic
                Reservation reservation = new Reservation();
                Reservation reservation2 = new Reservation();

                //갈 때
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());

                if (deploy.getTrain().getTrainName().contains("KTX")) {
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
                    } else if(beforeVip){
                        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
                        Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                        reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                        reservation.setGrade(optionalKtxRoom.get().getGrade());

                        //updated point
                        KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
                        reservation.setSeats(beforeChosenSeats);
                        foundSeat.checkSeats(beforeChosenSeats);
                    }

                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    //자리차지
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalMugunghwaRoom.get().getRoomName());
                    MugunghwaSeat foundSeat = optionalMugunghwaRoom.get().getMugunghwaSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);

                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //자리차지
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalSaemaulRoom.get().getRoomName());
                    SaemaulSeat foundSeat = optionalSaemaulRoom.get().getSaemaulSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                }

                //올 떄
                Deploy deploy2 = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                Mugunghwa mugunghwa2 = (Mugunghwa) deploy2.getTrain();

                //자리차지
                List<MugunghwaRoom> mugunghwaRooms2 = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa2.getId());
                Optional<MugunghwaRoom> optionalMugunghwaRoom2 = mugunghwaRooms2.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                reservation2.setRoomName(optionalMugunghwaRoom2.get().getRoomName());
                MugunghwaSeat foundSeat2 = optionalMugunghwaRoom2.get().getMugunghwaSeat();
                reservation2.setSeats(mugunghwaSeatDto.returnSeats());
                foundSeat2.mugunghwaDtoToEntity(mugunghwaSeatDto);

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
                reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

                reservation2.setPassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getDeploy().getTrain(), reservation2.getGrade()));

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
            Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

//            List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
            List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.findAllByMugunghwa(mugunghwa);
            Optional<MugunghwaRoom> foundRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

            //1차 캐시에서 찾아오겠지?? => dto projection이라 1차 캐시에서 못 긁어 오는 듯
            mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(foundRoom.get().getMugunghwaSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("mugunghwaRooms", mugunghwaRooms);
            model.addAttribute("roomName", targetRoomName.get());

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "trainseat/chooseMugunghwaSeat";
        }

        else {
            if(mugunghwaSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

//                List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.findAllByMugunghwa(mugunghwa);
                Optional<MugunghwaRoom> foundRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                mugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(foundRoom.get().getMugunghwaSeat().getId());

                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(mugunghwaSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                model.addAttribute("roomName", roomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseMugunghwaSeat";
            }
            //success logic
            Reservation reservation = new Reservation();

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

            //seat까지 fetch 안 하면 KtxSeatNormal 다운 캐스팅 부분에서 에러터짐 => 프록시이기 때문에
            //join 때문에 db에 부하가 가더라도 이 방법이 맞을 듯
            List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
            Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

            reservation.setRoomName(optionalMugunghwaRoom.get().getRoomName());

            //자리차지
            MugunghwaSeat foundSeat = optionalMugunghwaRoom.get().getMugunghwaSeat();
            reservation.setSeats(mugunghwaSeatDto.returnSeats());
            foundSeat.mugunghwaDtoToEntity(mugunghwaSeatDto);

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
            reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

            //reservation을 db에 저장
            reservationService.saveReservation(reservation);

            return "redirect:/my-page";
        }
    }

    @PostMapping("/reservation/saemaul")
    public String reserveSaemaul(@ModelAttribute SaemaulSeatDto saemaulSeatDto,
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
                                   @RequestParam(required = false) String dateTimeOfComing,
                                   @RequestParam(required = false) String beforeRoomName,
                                   @RequestParam(required = false) Boolean beforeNormal,
                                   @RequestParam(required = false) Boolean beforeVip,
                                   @RequestParam(required = false) String beforeChosenSeats,
                                   HttpServletRequest request,
                                   Model model) {

        model.addAttribute("passengers", passengerDto.howManyOccupied());
        okList.set(new ArrayList<>());

        if(round == Boolean.TRUE && going == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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
                Saemaul saemaul = (Saemaul) deploy.getTrain();

//                List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                List<SaemaulRoom> saemaulRooms = saemaulRoomService.findAllBySaemaul(saemaul);
                Optional<SaemaulRoom> foundRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(foundRoom.get().getSaemaulSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("saemaulRooms", saemaulRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseSaemaulSeat";
            }

            else {
                if(saemaulSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

//                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.findAllBySaemaul(saemaul);
                    Optional<SaemaulRoom> foundRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(foundRoom.get().getSaemaulSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("going", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("saemaulRooms", saemaulRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseSaemaulSeat";
                }
                //success logic
                //올 때 좌석 유무 체크 및 해당 페이지 이동 Logic
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());

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

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", saemaulSeatDto.returnSeats());

                    return "normalVip";
                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    MugunghwaRoom targetRoom = null;

                    for (MugunghwaRoom mugunghwaRoom : mugunghwaRooms) {
                        if (mugunghwaRoom.getMugunghwaSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(mugunghwaRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalMugunghwaRoom.isPresent()) {
                            targetRoom = optionalMugunghwaRoom.get();
                            break;
                        }
                    }
                    MugunghwaSeatDto targetMugunghwaSeatDto = mugunghwaSeatService.findMugunghwaSeatDtoById(targetRoom.getMugunghwaSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetMugunghwaSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("mugunghwaRooms", mugunghwaRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", saemaulSeatDto.returnSeats());

                    return "trainseat/chooseMugunghwaSeat";
                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //query 몇 개 나가는지 보기
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    SaemaulRoom targetRoom = null;

                    for (SaemaulRoom saemaulRoom : saemaulRooms) {
                        if (saemaulRoom.getSaemaulSeat().remain(passengerDto.howManyOccupied()) == Boolean.TRUE) {
                            okList.get().add(saemaulRoom.getRoomName());
                        }
                    }

                    for (String rName : okList.get()) {
                        Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(rName)).findAny();
                        if (optionalSaemaulRoom.isPresent()) {
                            targetRoom = optionalSaemaulRoom.get();
                            break;
                        }
                    }
                    SaemaulSeatDto targetSaemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(targetRoom.getSaemaulSeat().getId());

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(targetSaemaulSeatDto, Map.class);
                    model.addAttribute("map", seatMap);

                    model.addAttribute("saemaulRooms", saemaulRooms);
                    model.addAttribute("roomName", targetRoom.getRoomName());

                    model.addAttribute("okList", okList.get());

                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    model.addAttribute("beforeRoomName", roomName);
                    model.addAttribute("beforeVip", false);
                    model.addAttribute("beforeNormal", false);
                    model.addAttribute("beforeChosenSeats", saemaulSeatDto.returnSeats());

                    return "trainseat/chooseSaemaulSeat";
                }
            }
        }

        if(round == Boolean.TRUE && coming == Boolean.TRUE) {
            LocalDateTime beforeDateTime = getLocalDateTime(dateTimeOfGoing);
            LocalDateTime afterDateTime = getLocalDateTime(dateTimeOfComing);

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
                Saemaul saemaul = (Saemaul) deploy.getTrain();

                List<SaemaulRoom> saemaulRooms = saemaulRoomService.findAllBySaemaul(saemaul);
                Optional<SaemaulRoom> foundRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

                saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(foundRoom.get().getSaemaulSeat().getId());

                model.addAttribute("round", true);
                model.addAttribute("coming", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);
                model.addAttribute("dateTimeOfComing", afterDateTime);

                model.addAttribute("beforeRoomName", beforeRoomName);
                model.addAttribute("beforeNormal", beforeNormal);
                model.addAttribute("beforeVip", beforeVip);
                model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("saemaulRooms", saemaulRooms);
                model.addAttribute("roomName", targetRoomName.get());

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseSaemaulSeat";
            }

            else {
                if(saemaulSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                    Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

//                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.findAllBySaemaul(saemaul);
                    Optional<SaemaulRoom> foundRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                    saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(foundRoom.get().getSaemaulSeat().getId());

                    model.addAttribute("passengerNumberNotSame", true);
                    model.addAttribute("round", true);
                    model.addAttribute("coming", true);

                    model.addAttribute("departurePlace", departurePlace);
                    model.addAttribute("arrivalPlace", arrivalPlace);
                    model.addAttribute("dateTimeOfGoing", beforeDateTime);
                    model.addAttribute("dateTimeOfComing", afterDateTime);

                    //updated
                    model.addAttribute("beforeRoomName", beforeRoomName);
                    model.addAttribute("beforeNormal", beforeNormal);
                    model.addAttribute("beforeVip", beforeVip);
                    model.addAttribute("beforeChosenSeats", beforeChosenSeats);

                    ObjectMapper objectMapper = new ObjectMapper();
                    Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
                    model.addAttribute("map", seatMap);
                    model.addAttribute("saemaulRooms", saemaulRooms);
                    model.addAttribute("roomName", roomName);

                    Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                    model.addAttribute("okList", checkMap.values());

                    return "trainseat/chooseSaemaulSeat";
                }
                //success logic
                Reservation reservation = new Reservation();
                Reservation reservation2 = new Reservation();

                //갈 때
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());

                if (deploy.getTrain().getTrainName().contains("KTX")) {
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
                    } else if(beforeVip) {
                        List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByKtxAndGradeWithFetch(ktx, Grade.VIP);
                        Optional<KtxRoom> optionalKtxRoom = ktxRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();
                        reservation.setRoomName(optionalKtxRoom.get().getRoomName());
                        reservation.setGrade(optionalKtxRoom.get().getGrade());

                        //updated point
                        KtxSeatVip foundSeat = (KtxSeatVip) optionalKtxRoom.get().getKtxSeat();
                        reservation.setSeats(beforeChosenSeats);
                        foundSeat.checkSeats(beforeChosenSeats);
                    }

                } else if (deploy.getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa mugunghwa = (Mugunghwa) deploy.getTrain();

                    //자리차지
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(mugunghwa.getId());
                    Optional<MugunghwaRoom> optionalMugunghwaRoom = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalMugunghwaRoom.get().getRoomName());
                    MugunghwaSeat foundSeat = optionalMugunghwaRoom.get().getMugunghwaSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);

                } else {
                    Saemaul saemaul = (Saemaul) deploy.getTrain();

                    //자리차지
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                    Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(beforeRoomName)).findAny();

                    reservation.setRoomName(optionalSaemaulRoom.get().getRoomName());
                    SaemaulSeat foundSeat = optionalSaemaulRoom.get().getSaemaulSeat();
                    reservation.setSeats(beforeChosenSeats);
                    foundSeat.checkSeats(beforeChosenSeats);
                }

                //올 떄
                Deploy deploy2 = deployService.getDeployToTrainById(deployForm.getDeployIdOfComing());
                Saemaul saemaul2 = (Saemaul) deploy2.getTrain();

                //자리차지
                List<SaemaulRoom> saemaulRooms2 = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul2.getId());
                Optional<SaemaulRoom> optionalSaemaulRoom2 = saemaulRooms2.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                reservation2.setRoomName(optionalSaemaulRoom2.get().getRoomName());
                SaemaulSeat foundSeat2 = optionalSaemaulRoom2.get().getSaemaulSeat();
                reservation2.setSeats(saemaulSeatDto.returnSeats());
                foundSeat2.saemaulDtoToEntity(saemaulSeatDto);

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
                reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

                reservation2.setPassenger(passenger2);
                reservation2.setFee(passengerDto.getFee(reservation2.getDeploy().getTrain(), reservation2.getGrade()));

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
            Saemaul saemaul = (Saemaul) deploy.getTrain();

//            List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
            List<SaemaulRoom> saemaulRooms = saemaulRoomService.findAllBySaemaul(saemaul);
            Optional<SaemaulRoom> foundRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(targetRoomName.get())).findAny();

            //1차 캐시에서 찾아오겠지?? => dto projection이라 1차 캐시에서 못 긁어 오는 듯
            saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(foundRoom.get().getSaemaulSeat().getId());

            model.addAttribute("going", true);
            model.addAttribute("departurePlace", departurePlace);
            model.addAttribute("arrivalPlace", arrivalPlace);
            model.addAttribute("dateTimeOfGoing", beforeDateTime);

            ObjectMapper objectMapper = new ObjectMapper();
            Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
            model.addAttribute("map", seatMap);
            model.addAttribute("saemaulRooms", saemaulRooms);
            model.addAttribute("roomName", targetRoomName.get());

            Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
            model.addAttribute("okList", checkMap.values());

            return "trainseat/chooseSaemaulSeat";
        }

        else {
            if(saemaulSeatDto.howManyOccupied() != passengerDto.howManyOccupied()) {
                Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
                Saemaul saemaul = (Saemaul) deploy.getTrain();

//                List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
                List<SaemaulRoom> saemaulRooms = saemaulRoomService.findAllBySaemaul(saemaul);
                Optional<SaemaulRoom> foundRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

                saemaulSeatDto = saemaulSeatService.findSaemaulSeatDtoById(foundRoom.get().getSaemaulSeat().getId());

                model.addAttribute("passengerNumberNotSame", true);
                model.addAttribute("going", true);

                model.addAttribute("departurePlace", departurePlace);
                model.addAttribute("arrivalPlace", arrivalPlace);
                model.addAttribute("dateTimeOfGoing", beforeDateTime);

                ObjectMapper objectMapper = new ObjectMapper();
                Map seatMap = objectMapper.convertValue(saemaulSeatDto, Map.class);
                model.addAttribute("map", seatMap);
                model.addAttribute("saemaulRooms", saemaulRooms);
                model.addAttribute("roomName", roomName);

                Map checkMap = objectMapper.convertValue(checkRoomDto, Map.class);
                model.addAttribute("okList", checkMap.values());

                return "trainseat/chooseSaemaulSeat";
            }
            //success logic
            Reservation reservation = new Reservation();

            Deploy deploy = deployService.getDeployToTrainById(deployForm.getDeployIdOfGoing());
            Saemaul saemaul = (Saemaul) deploy.getTrain();

            //seat까지 fetch 안 하면 KtxSeatNormal 다운 캐스팅 부분에서 에러터짐 => 프록시이기 때문에
            //join 때문에 db에 부하가 가더라도 이 방법이 맞을 듯
            List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(saemaul.getId());
            Optional<SaemaulRoom> optionalSaemaulRoom = saemaulRooms.stream().filter(r -> r.getRoomName().equals(roomName)).findAny();

            reservation.setRoomName(optionalSaemaulRoom.get().getRoomName());

            //자리차지
            SaemaulSeat foundSeat = optionalSaemaulRoom.get().getSaemaulSeat();
            reservation.setSeats(saemaulSeatDto.returnSeats());
            foundSeat.saemaulDtoToEntity(saemaulSeatDto);

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
            reservation.setFee(passengerDto.getFee(reservation.getDeploy().getTrain(), reservation.getGrade()));

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
