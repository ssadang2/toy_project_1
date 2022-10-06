package toy.ktx.domain.dto;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DeployForm {

    private LocalDateTime departureTime;

    private LocalDateTime arrivalTime;

    private String departurePlace;

    private String arrivalPlace;
}
