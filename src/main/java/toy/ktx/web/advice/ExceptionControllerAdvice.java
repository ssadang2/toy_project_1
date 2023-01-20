package toy.ktx.web.advice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import toy.ktx.domain.error.ErrorResult;

@Slf4j
@RestControllerAdvice(annotations = {RestController.class})
//api error를 처리해주는 컨트롤러 어드바이스
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResult exHandler(MethodArgumentTypeMismatchException e) {
        return new ErrorResult(e.getClass(), "api 스펙이 틀렸습니다. 날짜를 제대로 입력해주세요", e.getName());
    }
}
