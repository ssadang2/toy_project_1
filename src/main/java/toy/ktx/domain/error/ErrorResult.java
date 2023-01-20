package toy.ktx.domain.error;

import lombok.Data;

@Data
//api에서 스펙이 틀렸을 때 나가는 에러 객체
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
