package toy.ktx.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.dto.api.ReservationDto;
import toy.ktx.domain.dto.api.ReservationWithMemberDeployTrainDto;
import toy.ktx.service.ReservationService;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReservationApiController {

    private final ReservationService reservationService;

    //entity 조회 후 dto로 발라내기 v3
    @GetMapping("/api/reservations")
    public Page<ReservationDto> findAllReservation(Pageable pageable) {
        Page<Reservation> reservations = reservationService.findAll(pageable);
        return reservations.map(r -> new ReservationDto(r));
    }

    //dto로 바로 조회(new 문법 사용해서) v4
    @GetMapping("/api/reservations-dto")
    public Page<ReservationDto> findAllReservationDtoBy(Pageable pageable) {
        Page<ReservationDto> reservations = reservationService.findAllReservationDtoBy(pageable);
        return reservations;
    }

    //entity 조회 후 dto로 발라내기 + fetch join v3
    @GetMapping("/api/reservations/member-deploy-train")
    public Page<ReservationWithMemberDeployTrainDto> findAllReservationWithMemberDeployTrainFetch(Pageable pageable) {
        Page<Reservation> reservations = reservationService.findAllReservationWithMemberDeployTrainFetch(pageable);
        return reservations.map(r -> new ReservationWithMemberDeployTrainDto(r));
    }

    //dto로 바로 조회(new 문법 사용해서) + join v4
    @GetMapping("/api/reservations-dto/member-deploy-train")
    public Page<ReservationWithMemberDeployTrainDto> findAllReservationDtoWithMemberDeployTrain(Pageable pageable) {
        return reservationService.findAllReservationDtoWithMemberDeployTrain(pageable);
    }
}
