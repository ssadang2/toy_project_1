package toy.ktx.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import toy.ktx.domain.Member;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.dto.SignUpForm;
import toy.ktx.domain.enums.Grade;
import toy.ktx.domain.ktx.*;
import toy.ktx.domain.mugunhwa.Mugunghwa;
import toy.ktx.domain.mugunhwa.MugunghwaRoom;
import toy.ktx.domain.mugunhwa.MugunghwaSeat;
import toy.ktx.domain.saemaul.Saemaul;
import toy.ktx.domain.saemaul.SaemaulRoom;
import toy.ktx.domain.saemaul.SaemaulSeat;
import toy.ktx.service.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@Controller
@RequiredArgsConstructor
@Slf4j
public class MemberController {

    private final MemberService memberService;
    private final ReservationService reservationService;
    private final KtxRoomService ktxRoomService;
    private final MugunghwaRoomService mugunghwaRoomService;
    private final SaemaulRoomService saemaulRoomService;
    private final KtxSeatService ktxSeatService;
    private final MugunghwaSeatService mugunghwaSeatService;
    private final SaemaulSeatService saemaulSeatService;

    //회원 가입 페이지로 이동하게 하는 컨트롤러
    @GetMapping("/sign-up")
    public String getSignUpPage(@ModelAttribute SignUpForm SignUpForm) {
        return "signUpPage";
    }

    //회원가입 완료 및 validation을 처리하는 컨트롤러
    @PostMapping("/sign-up")
    public String completeSignUp(@Valid @ModelAttribute SignUpForm signUpForm, BindingResult bindingResult) {
        if(bindingResult.hasErrors()) {
            log.info("errors={}", bindingResult);
            return "signUpPage";
        }

        Member member = memberService.findByLoginId(signUpForm.getLoginId()).orElse(null);
        if(member != null) {
            bindingResult.rejectValue("loginId", "dup", "중복된 로그인 아이디입니다.");
            return "signUpPage";
        }
        memberService.dtoToSaveMember(signUpForm);

        return "redirect:/";
    }

    //컨트롤 URI
    //사용자의 예약 삭제를 처리하는 컨트롤러
    @PostMapping("/my-page/delete-reservation")
    public String cancelReservation(@RequestParam(required = false) Long reservationId) {

        //예약 삭제 로직
        //예상 select 쿼리 2개 -> 실제 3개 select passenger 나가는 이유 -> 프록시 초기화해야 pk 값을 가져올 수 있기 때문에
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
}
