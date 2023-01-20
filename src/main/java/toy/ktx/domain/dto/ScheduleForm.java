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

    private String dateOfComing;

    private String timeOfComing;

    private Integer toddler;

    private Integer kids;

    private Integer adult;

    private Integer senior;

    public Long getTotal() {
        Long sum = Long.valueOf(0);

        if (toddler != null) {
            sum += toddler;
        }

        if (kids != null) {
            sum += kids;
        }

        if (adult != null) {
            sum += adult;
        }

        if (senior != null) {
            sum += senior;
        }
        return sum;
    }

    public PassengerDto getDto() {
        PassengerDto passengerDto = new PassengerDto();

        passengerDto.setToddler(toddler);
        passengerDto.setKids(kids);
        passengerDto.setAdult(adult);
        passengerDto.setSenior(senior);

        return passengerDto;
    }
}
