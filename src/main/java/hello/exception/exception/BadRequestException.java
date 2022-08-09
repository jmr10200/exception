package hello.exception.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;


//@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "잘못된 요청 오류")
// 메시지 기능
@ResponseStatus(code = HttpStatus.BAD_REQUEST, reason = "bad request error!!")
public class BadRequestException extends RuntimeException {
    // @ResponseStatus 어노테이션으로 HTTP 상태 코드를 변경해준다.
    // -> BadRequestException 가 컨트롤러 밖으로 넘어가면 ResponseStatusExceptionResolver 가 해당 어노테이션을 확인해서
    //    에러코드를 HttpStatus.BAD_REQUEST(400) 으로 변경하고 메시지도 담는다.
    // -> ResponseStatusExceptionResolver 를 확인해보면 결국 response.sendError(statusCode, resolvedReason) 를 호출 한다.
    //    sendError(400) 를 호출했기 때문에 WAS 에서 다시 에러페이지 /error 를 내부 요청한다.
}

// ResponseStatusExceptionResolver
// 예외에 따라서 HTTP 상태 코드를 지정해주는 역할을 한다.
// 다음 두 가지 경우를 처리함
// (1) @ResponseStatus 가 달려 있는 예외
// (2) ResponseStatusException 예외

