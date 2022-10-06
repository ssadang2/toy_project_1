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

    private String dateOfGoing;

    private String timeOfGoing;

    private String dateOfLeaving;

    private String timeOfLeaving;

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;
}
