package toy.ktx.domain.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.enums.Grade;

@Data
public class ReservationDto2 {
    private Long reservationId;

    @JsonIgnore
    private Long memberId;

    private Long fee;

    private String seats;

    public ReservationDto2(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.memberId = reservation.getMember().getId();
        this.fee = reservation.getFee();
        this.seats = reservation.getSeats();
    }

    public ReservationDto2(Long reservationId, Long memberId, Long fee, String seats) {
        this.reservationId = reservationId;
        this.memberId = memberId;
        this.fee = fee;
        this.seats = seats;
    }
}
