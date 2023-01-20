package toy.ktx.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class CreateDeployForm {
    @NotBlank
    private String departurePlace;

    @NotBlank
    private String arrivalPlace;

    //dateTime 타입은 어떤 이유에서 validation 불가한 듯
    @NotBlank
    private String dateOfGoing;

    @NotBlank
    private String timeOfGoing;

    @NotBlank
    private String dateOfComing;

    @NotBlank
    private String timeOfComing;

    @NotBlank
    private String trainName;
}
