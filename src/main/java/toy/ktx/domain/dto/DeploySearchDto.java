package toy.ktx.domain.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class DeploySearchDto {

    private String dateOfGoing;

    private String timeOfGoing;

    private String dateOfComing;

    private String timeOfComing;
}
