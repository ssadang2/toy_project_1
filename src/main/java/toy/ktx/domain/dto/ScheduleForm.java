package toy.ktx.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
public class ScheduleForm {

    @NotBlank
    private String departurePlace;

    @NotBlank
    private String arrivalPlace;

    private Boolean round;

    @NotBlank
    private String departureDate;

    private String departureTime;

    private String arrivalDate;

    private String arrivalTime;

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;
}
