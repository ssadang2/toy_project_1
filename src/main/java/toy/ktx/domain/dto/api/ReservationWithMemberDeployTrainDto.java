package toy.ktx.domain.dto.api;

import lombok.Data;
import toy.ktx.domain.Reservation;

@Data
public class ReservationWithMemberDeployTrainDto {
    private Long reservationId;

    private Long fee;

    private String name;

    private Long age;

    private Long deployId;

    private String trainName;

    public ReservationWithMemberDeployTrainDto(Reservation reservation) {
        this.reservationId = reservation.getId();
        this.fee = reservation.getFee();
        this.name = reservation.getMember().getName();
        this.age = reservation.getMember().getAge();
        this.deployId = reservation.getDeploy().getId();
        this.trainName = reservation.getDeploy().getTrain().getTrainName();
    }

    public ReservationWithMemberDeployTrainDto(Long reservationId, Long fee, String name, Long age, Long deployId, String trainName) {
        this.reservationId = reservationId;
        this.fee = fee;
        this.name = name;
        this.age = age;
        this.deployId = deployId;
        this.trainName = trainName;
    }
}
