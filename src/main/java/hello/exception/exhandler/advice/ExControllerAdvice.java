package hello.exception.exhandler.advice;

import hello.exception.UserException;
import hello.exception.exhandler.ErrorResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ExControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExceptionHandle(IllegalArgumentException e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExceptionHandle(UserException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exceptionHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("EX", "내부 오류");
    }
    // ApiExceptionV2Controller 의 @ExceptionHandler 모두 제거
}
// @ControllerAdvice
// 대상으로 지정한 여러 컨트롤러에 @ExceptionHandler, @InitBinder 기능을 부여해주는 역할
// @ControllerAdvice 에 대상을 지정하지 않으면 모든 컨트롤러에 적용된다.
// @RestControllerAdvice 는 @ControllerAdvice 와 같고, @ResponseBody 가 추가되어 있다.
// (@Controller, @RestController 차이와 같다)

// 대상 컨트롤러 지정방법
// 특정 어노테이션이 있는 컨트롤러를 지정할 수 있다.
// 특정 패키지를 지정할 수 있다. 이 경우 해당 패키지와 하위에 있는 컨트롤러가 대상이다.
// 특정 클래스를 지정할 수도 있다.
// 대상 컨트롤러 지정을 생략하면 모든 컨트롤러에 적용된다.

// 정리하면, @ExceptionHandler 와 @ControllerAdvice 조합으로 예외처리가 깔끔해진다.