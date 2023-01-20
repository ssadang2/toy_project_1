package toy.ktx.domain.dto.api;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class DeployWithTrainDto {

    private Long deployId;

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private String departurePlace;

    private String arrivalPlace;

    private Long trainId;

    private String trainName;

    public DeployWithTrainDto(Long deployId, LocalDateTime departureTime, LocalDateTime arrivalTime, String departurePlace, String arrivalPlace, Long trainId, String trainName) {
        this.deployId = deployId;
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departurePlace = departurePlace;
        this.arrivalPlace = arrivalPlace;
        this.trainId = trainId;
        this.trainName = trainName;
    }
}
