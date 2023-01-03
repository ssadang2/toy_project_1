package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import toy.ktx.domain.Deploy;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.constant.SessionConst;
import toy.ktx.domain.constant.StationsConst;
import toy.ktx.domain.constant.TrainNameConst;
import toy.ktx.domain.dto.CreateDeployForm;
import toy.ktx.domain.dto.ScheduleForm;
import toy.ktx.domain.enums.Authorizations;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.repository.MugunghwaRepository;
import toy.ktx.service.*;

import javax.validation.Valid;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@Slf4j
@RequiredArgsConstructor
public class HomeController {

    private final ReservationService reservationService;
    private final KtxRoomService ktxRoomService;
    private final MugunghwaRoomService mugunghwaRoomService;
    private final SaemaulRoomService saemaulRoomService;
    private final KtxSeatService ktxSeatService;
    private final MugunghwaSeatService mugunghwaSeatService;
    private final SaemaulSeatService saemaulSeatService;
    private final KtxService ktxService;
    private final MugunghwaService mugunghwaService;
    private final SaemaulService saemaulService;
    private final KtxSeatNormalService ktxSeatNormalService;
    private final KtxSeatVipService ktxSeatVipService;
    private final DeployService deployService;

    @GetMapping("/")
    public String getHome(Model model,
                          @SessionAttribute(name = SessionConst.LOGIN_MEMBER, required = false) Member member,
                          @ModelAttribute ScheduleForm scheduleForm){

        scheduleForm.setDateOfGoing(LocalDate.now().toString());

        model.addAttribute("minDateTime", LocalDateTime.now());
        model.addAttribute("maxDateTime", LocalDateTime.now().plusDays(30));

        if(member == null) {
            model.addAttribute("notLogin", true);
            return "index";
        }

        model.addAttribute("login", true);
        return "index";
    }

    @GetMapping("/my-page")
    public String getMyPage(@SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member,
                            Model model) {

        //userPage 진입
        if (member.getAuthorizations().equals(Authorizations.USER)) {
            List<Reservation> reservations = reservationService.findByMember(member);
            List<Deploy> deploys = new ArrayList<>();

            for (Reservation reservation : reservations) {
                Deploy deploy = reservation.getDeploy();
                deploys.add(deploy);
            }

            List<String> durations = getDuration(deploys);

            if (reservations.isEmpty() != true) {
                model.addAttribute("reservations", reservations);
                model.addAttribute("durations", durations);
            }

            model.addAttribute("localDateTime", LocalDateTime.now());
            model.addAttribute("member", member);
            return "mypage/userMyPage";
        }

        //adminPage 진입
        model.addAttribute("member", member);
        model.addAttribute("createDeployForm", new CreateDeployForm());

        List<Deploy> deployList = deployService.findAll();
        List<String> durations = getDuration(deployList);
        model.addAttribute("deployList", deployList);
        model.addAttribute("durations", durations);
        return "mypage/adminMyPage";

    }

    //컨트롤 api
    @PostMapping("/my-page/delete-reservation")
    public String cancelReservation(@RequestParam(required = false) Long reservationId) {

        //예약 삭제 로직
        //예상 select 쿼리 2개 -> 실제 3개 select passenger 나가는 이유 => 프록시 초기화해야 pk 값을 가져올 수 있기 때문에
        if (reservationId != null) {
            Optional<Reservation> foundReservation = reservationService.getReservationToTrainByIdWithFetch(reservationId);
            if (foundReservation.isPresent()) {
                Reservation reservation = foundReservation.get();
                if (reservation.getDeploy().getTrain().getTrainName().contains("KTX")) {
                    Ktx train = (Ktx) reservation.getDeploy().getTrain();
                    List<KtxRoom> ktxRooms = ktxRoomService.getKtxRoomsToSeatByIdWithFetch(train.getId());
                    Optional<KtxRoom> roomOptional = ktxRooms.stream().filter(r -> r.getRoomName().equals(reservation.getRoomName())).findFirst();
                    KtxRoom ktxRoom = roomOptional.get();
                    KtxSeat ktxSeat = ktxRoom.getKtxSeat();

                    //reservation 등의 entity 뿐만 아니라 seat entity 안의 자리까지 체크 해제해줘야 됨
                    if (ktxRoom.getGrade().equals(Grade.NORMAL)) {
                        ktxSeat = (KtxSeatNormal) ktxSeat;
                        System.out.println("ktxSeat = " + ktxSeat.getClass());
                    } else {
                        ktxSeat = (KtxSeatVip) ktxSeat;
                    }

                    ktxSeatService.updateSeatsWithReflection(ktxSeat, reservation.getSeats());
                } else if (reservation.getDeploy().getTrain().getTrainName().contains("MUGUNGHWA")) {
                    Mugunghwa train = (Mugunghwa) reservation.getDeploy().getTrain();
                    List<MugunghwaRoom> mugunghwaRooms = mugunghwaRoomService.getMugunghwaRoomsToSeatByIdWithFetch(train.getId());
                    Optional<MugunghwaRoom> roomOptional = mugunghwaRooms.stream().filter(r -> r.getRoomName().equals(reservation.getRoomName())).findFirst();
                    MugunghwaRoom mugunghwaRoom = roomOptional.get();
                    MugunghwaSeat mugunghwaSeat = mugunghwaRoom.getMugunghwaSeat();

                    mugunghwaSeatService.updateSeatsWithReflection(mugunghwaSeat, reservation.getSeats());
                } else {
                    Saemaul train = (Saemaul) reservation.getDeploy().getTrain();
                    List<SaemaulRoom> saemaulRooms = saemaulRoomService.getSaemaulRoomsToSeatByIdWithFetch(train.getId());
                    Optional<SaemaulRoom> roomOptional = saemaulRooms.stream().filter(r -> r.getRoomName().equals(reservation.getRoomName())).findFirst();
                    SaemaulRoom saemaulRoom = roomOptional.get();
                    SaemaulSeat saemaulSeat = saemaulRoom.getSaemaulSeat();

                    saemaulSeatService.updateSeatsWithReflection(saemaulSeat, reservation.getSeats());
                }
            }

            //cascade option을 켰기 때문에 passenger를 굳이 수동으로 안 지워줘도 됨
            reservationService.deleteById(reservationId);
        }
        //prg
        return "redirect:/my-page";
    }

    @PostMapping("/my-page/save-deploy")
    public String saveDeploy(@Valid @ModelAttribute CreateDeployForm createDeployForm, BindingResult bindingResult,
                             @SessionAttribute(name = SessionConst.LOGIN_MEMBER) Member member, Model model) {

        if(createDeployForm.getTimeOfGoing().length() != 5) {
            bindingResult.reject("noCorrectTimeFormatGoing", null);
        }

        if(createDeployForm.getTimeOfComing().length() != 5) {
            bindingResult.reject("noCorrectTimeFormatComing", null);
        }

        if(createDeployForm.getTimeOfGoing().length() == 5 && !createDeployForm.getTimeOfGoing().substring(2,3).equals(":")) {
            bindingResult.reject("noColonGoing", null);
        }

        if(createDeployForm.getTimeOfComing().length() == 5 && !createDeployForm.getTimeOfComing().substring(2,3).equals(":")) {
            bindingResult.reject("noColonComing", null);
        }

        try {
            if(createDeployForm.getTimeOfGoing().length() == 5 &&
                    ((Integer.parseInt(createDeployForm.getTimeOfGoing().substring(0 ,2)) > 24 ||
                            Integer.parseInt(createDeployForm.getTimeOfGoing().substring(0 ,2)) < 0) ||
                            (Integer.parseInt(createDeployForm.getTimeOfGoing().substring(3)) >60 ||
                                    Integer.parseInt(createDeployForm.getTimeOfGoing().substring(3)) < 0))) {
                bindingResult.reject("noCorrectTimeFormatGoing", null);
            }
        } catch (Exception e) {
            log.info("fuck = {}", e);
            bindingResult.reject("noCorrectTimeFormatGoing", null);
        }

        try {
            if(createDeployForm.getTimeOfComing().length() == 5 &&
                    ((Integer.parseInt(createDeployForm.getTimeOfComing().substring(0 ,2)) > 24 ||
                            Integer.parseInt(createDeployForm.getTimeOfComing().substring(0 ,2)) < 0) ||
                            (Integer.parseInt(createDeployForm.getTimeOfComing().substring(3)) >60 ||
                                    Integer.parseInt(createDeployForm.getTimeOfComing().substring(3)) < 0))) {
                bindingResult.reject("noCorrectTimeFormatComing", null);
            }
        } catch (Exception e) {
            log.info("fuck = {}", e);
            bindingResult.reject("noCorrectTimeFormatComing", null);
        }

        if(!Arrays.asList(StationsConst.stations).contains(createDeployForm.getDeparturePlace())
                || !Arrays.asList(StationsConst.stations).contains(createDeployForm.getArrivalPlace())) {
            bindingResult.reject("noStation", null);
        }

        if(!Arrays.asList(TrainNameConst.trains).contains(createDeployForm.getTrainName())) {
            bindingResult.reject("noTrain", null);
        }

        if(bindingResult.hasErrors()) {
            //logic상 member가 존재하지 않을 수 없다고 판단하여 따로 null check logic 넣지 않았음
            model.addAttribute("member", member);
            model.addAttribute("createDeployForm", new CreateDeployForm());

            List<Deploy> deployList = deployService.findAll();
            List<String> durations = getDuration(deployList);
            model.addAttribute("deployList", deployList);
            model.addAttribute("durations", durations);
            return "mypage/adminMYPage";
        }
        //실제 db에 입력된 정보에 기반해 deploy를 입력하는 logic(success logic)
        Deploy deploy = new Deploy();

        String dateTimeOfGoingString = createDeployForm.getDateOfGoing() + "T" + createDeployForm.getTimeOfGoing();
        LocalDateTime dateTimeOfGoing = getLocalDateTime(dateTimeOfGoingString);

        String dateTimeOfComingString = createDeployForm.getDateOfComing() + "T" + createDeployForm.getTimeOfComing();
        LocalDateTime dateTimeOfComing = getLocalDateTime(dateTimeOfComingString);

        deploy.setDeparturePlace(createDeployForm.getDeparturePlace());
        deploy.setArrivalPlace(createDeployForm.getArrivalPlace());
        deploy.setDepartureTime(dateTimeOfGoing);
        deploy.setArrivalTime(dateTimeOfComing);

        if (createDeployForm.getTrainName().contains("KTX")) {
            Ktx ktx = new Ktx();
            ktx.setTrainName(createDeployForm.getTrainName());
            ktxService.saveKtx(ktx);

            KtxSeatNormal ktxSeat1 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat2 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatVip ktxSeat3 = new KtxSeatVip(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false);

            KtxSeatVip ktxSeat4 = new KtxSeatVip(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false);

            KtxSeatVip ktxSeat5 = new KtxSeatVip( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false);

            KtxSeatNormal ktxSeat6 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat7 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat8 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat9 = new KtxSeatNormal(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            KtxSeatNormal ktxSeat10 = new KtxSeatNormal( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            ktxSeatNormalService.save(ktxSeat1);
            ktxSeatNormalService.save(ktxSeat2);
            ktxSeatVipService.save(ktxSeat3);
            ktxSeatVipService.save(ktxSeat4);
            ktxSeatVipService.save(ktxSeat5);
            ktxSeatNormalService.save(ktxSeat6);
            ktxSeatNormalService.save(ktxSeat7);
            ktxSeatNormalService.save(ktxSeat8);
            ktxSeatNormalService.save(ktxSeat9);
            ktxSeatNormalService.save(ktxSeat10);

            KtxRoom room1 = new KtxRoom("room1", ktx, Grade.NORMAL, ktxSeat1);
            KtxRoom room2 = new KtxRoom("room2", ktx, Grade.NORMAL, ktxSeat2);
            KtxRoom room3 = new KtxRoom("room3", ktx, Grade.VIP, ktxSeat3);
            KtxRoom room4 = new KtxRoom("room4", ktx, Grade.VIP, ktxSeat4);
            KtxRoom room5 = new KtxRoom("room5", ktx, Grade.VIP, ktxSeat5);
            KtxRoom room6 = new KtxRoom("room6", ktx, Grade.NORMAL, ktxSeat6);
            KtxRoom room7 = new KtxRoom("room7", ktx, Grade.NORMAL, ktxSeat7);
            KtxRoom room8 = new KtxRoom("room8", ktx, Grade.NORMAL, ktxSeat8);
            KtxRoom room9 = new KtxRoom("room9", ktx, Grade.NORMAL, ktxSeat9);
            KtxRoom room10 = new KtxRoom("room10", ktx, Grade.NORMAL,ktxSeat10);

            ktxRoomService.saveKtxRoom(room1);
            ktxRoomService.saveKtxRoom(room2);
            ktxRoomService.saveKtxRoom(room3);
            ktxRoomService.saveKtxRoom(room4);
            ktxRoomService.saveKtxRoom(room5);
            ktxRoomService.saveKtxRoom(room6);
            ktxRoomService.saveKtxRoom(room7);
            ktxRoomService.saveKtxRoom(room8);
            ktxRoomService.saveKtxRoom(room9);
            ktxRoomService.saveKtxRoom(room10);

            deploy.setTrain(ktx);
            deployService.saveDeploy(deploy);
            return "redirect:/my-page";

        } else if (createDeployForm.getTrainName().contains("MUGUNGHWA")) {
            Mugunghwa mugunghwa = new Mugunghwa();
            mugunghwa.setTrainName(createDeployForm.getTrainName());
            mugunghwaService.save(mugunghwa);

            MugunghwaSeat seat1 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat2 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat3 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat4 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            MugunghwaSeat seat5 = new MugunghwaSeat(false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            mugunghwaSeatService.save(seat1);
            mugunghwaSeatService.save(seat2);
            mugunghwaSeatService.save(seat3);
            mugunghwaSeatService.save(seat4);
            mugunghwaSeatService.save(seat5);

            MugunghwaRoom room1 = new MugunghwaRoom("room1", mugunghwa, seat1);
            MugunghwaRoom room2 = new MugunghwaRoom("room2", mugunghwa, seat2);
            MugunghwaRoom room3 = new MugunghwaRoom("room3", mugunghwa, seat3);
            MugunghwaRoom room4 = new MugunghwaRoom("room4", mugunghwa, seat4);
            MugunghwaRoom room5 = new MugunghwaRoom("room5", mugunghwa, seat5);

            mugunghwaRoomService.save(room1);
            mugunghwaRoomService.save(room2);
            mugunghwaRoomService.save(room3);
            mugunghwaRoomService.save(room4);
            mugunghwaRoomService.save(room5);

            deploy.setTrain(mugunghwa);
            deployService.saveDeploy(deploy);
            return "redirect:/my-page";
        } else {
            Saemaul saemaul = new Saemaul();
            saemaul.setTrainName(createDeployForm.getTrainName());
            saemaulService.save(saemaul);

            SaemaulSeat seat1 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat2 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat3 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat4 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            SaemaulSeat seat5 = new SaemaulSeat( false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false,
                    false, false, false, false, false, false, false, false);

            saemaulSeatService.save(seat1);
            saemaulSeatService.save(seat2);
            saemaulSeatService.save(seat3);
            saemaulSeatService.save(seat4);
            saemaulSeatService.save(seat5);

            SaemaulRoom room1 = new SaemaulRoom("room1", saemaul, seat1);
            SaemaulRoom room2 = new SaemaulRoom("room2", saemaul, seat2);
            SaemaulRoom room3 = new SaemaulRoom("room3", saemaul, seat3);
            SaemaulRoom room4 = new SaemaulRoom("room4", saemaul, seat4);
            SaemaulRoom room5 = new SaemaulRoom("room5", saemaul, seat5);

            saemaulRoomService.save(room1);
            saemaulRoomService.save(room2);
            saemaulRoomService.save(room3);
            saemaulRoomService.save(room4);
            saemaulRoomService.save(room5);

            deploy.setTrain(saemaul);
            deployService.saveDeploy(deploy);
            return "redirect:/my-page";
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


