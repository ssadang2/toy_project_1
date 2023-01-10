package toy.ktx.domain.error;

import lombok.Data;

@Data
public class ErrorResult {

    private Class clazz;
    private String message;
    private String param;

    public ErrorResult(Class clazz, String message, String param) {
        this.clazz = clazz;
        this.message = message;
        this.param = param;
    }
}
