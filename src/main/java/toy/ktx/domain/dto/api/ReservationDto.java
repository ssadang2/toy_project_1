package toy.ktx.domain.dto.api;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import lombok.Setter;
import toy.ktx.domain.Reservation;
import toy.ktx.domain.enums.Grade;

@Getter
@Setter
public class ReservationDto {
    private Long reservationId;

    private Long fee;

    private String seats;

    private String roomName;

    private Grade grade;

    public ReservationDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.fee = reservation.getFee();
        this.seats = reservation.getSeats();
        this.roomName = reservation.getRoomName();
        this.grade = reservation.getGrade();
    }

    public ReservationDto(Long reservationId, Long fee, String seats, String roomName, Grade grade) {
        this.reservationId = reservationId;
        this.fee = fee;
        this.seats = seats;
        this.roomName = roomName;
        this.grade = grade;
    }

}
