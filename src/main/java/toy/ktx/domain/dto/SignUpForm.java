package toy.ktx.domain.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SignUpForm {

    @NotBlank
    private String loginId;

    @NotBlank
    private String password;

    @NotBlank
    private String name;

    @NotNull
    private Long age;
}
