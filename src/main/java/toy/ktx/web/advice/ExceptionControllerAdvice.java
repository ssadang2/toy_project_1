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
public class ExceptionControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ErrorResult exHandler(MethodArgumentTypeMismatchException e) {
        log.error("fuck = {}", e.getMessage());
        log.error("fuck = {}", e.getClass());
        log.error("fuck = {}", e.getName());

        return new ErrorResult(e.getClass(), "api 스펙이 틀렸습니다. 날짜를 제대로 입력해주세요", e.getName());
    }
}
