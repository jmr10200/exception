package hello.exception.exhandler;

import hello.exception.UserException;
import hello.exception.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionV2Controller {

    // @ExceptionHandler 예외 처리 방법
    // 어노테이션을 선언하고, 해당 컨트롤러에서 처리하고 싶은 예외를 지정한다.
    // 해당 컨트롤러에서 예외가 발생하면 이 메소드가 호출된다.
    // 참고로 지정한 예외 또는 그 예외의 자식 클래스는 모두 잡을 수 있다.

    // 다음과 같이 복수 처리도 가능하다.
    // @ExceptionHandler({AException.class, BException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(IllegalArgumentException.class)
    public ErrorResult illegalExHandler(IllegalArgumentException e) {
        // IllegalArgumentException 또는 그 자식클래스 모두 처리한다.
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("BAD", e.getMessage());
        // 실행
        // 컨트롤러를 호출하면 IllegalArgumentException 예외가 컨트롤러 밖으로 던져진다.
        // 예외가 발생해서 ExceptionResolver 가 움직인다. 이때 가장 우선순위 높은 ExceptionHandlerExceptionResolver 가 실행된다.
        // ExceptionHandlerExceptionResolver 는 해당 컨트롤러에 IllegalArgumentException 을 처리할수 있는 @ExceptionHandler 를 확인한다.
        // illegalExHandler() 를 실행한다.
        // @RestController 이므로 illegalExHandler() 도 @ResponseBody() 가 적용된다.
        // 따라서 HTTP 컨버터가 사용되고, 응답이 JSON 으로 반환된다.
        // @ResponseStatus(HttpStatus.BAD_REQUEST) 이므로 400 으로 응답한다.
    }

    // 다음과 같이 예외를 생략할 수 있다. 생략하면 메소드 파라미터의 예외가 지정된다.
    @ExceptionHandler
    public ResponseEntity<ErrorResult> userExHandle(UserException e) {
        log.error("[exceptionHandle] ex", e);
        ErrorResult errorResult = new ErrorResult("USER-EX", e.getMessage());
        return new ResponseEntity<>(errorResult, HttpStatus.BAD_REQUEST);
        // UserException 처리 실행
        // @ExceptionHandler 에 예외를 지정하지 않아 파라미터의 UserException 예외 사용
        // ResponseEntity 를 사용해서 HTTP 메시지 바디에 직접 응답. HTTP 컨버터가 사용된다.
        // ResponseEntity 를 사용하면 HTTP 응답 코드를 프로그래밍해서 동적으로 변경 할 수 있다.
        // 즉, @ResponseStatus 는 어노테이션이므로 HTTP 응답 코드를 동적으로 변경할 수 없다.
    }

    // 스프링 컨트롤러 파라미터 응답처럼 다양한 파라미터와 응답을 지정할 수 있다.
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult exHandle(Exception e) {
        log.error("[exceptionHandle] ex", e);
        return new ErrorResult("EX", "내부 오류");
        // 실행
        // throw new RuntimeException("잘못된 사용자") 코드가 실행되면서 컨트롤러 밖으로 RuntimeException 이 던져진다.
        // RuntimeException 은 Exception 의 자식 클래스이다. 따라서 exHandle() 이 호출 된다.
        // @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR) 로 HTTP 상태코드를 500 으로 응답한다.
    }

    // 참고 : 다음과 같이 ModelAndView 를 사용해서 HTML 을 응답하는데 사용할 수도 있다.
    // @ExceptionHandler(ViewException.class)
    // public ModelAndView ex(ViewException e) {...}

    @GetMapping("/api2/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못 입력 값");
        }

        if (id.equals("user-ex")) {
            throw new UserException("사용자 에러");
        }

        return new MemberDto(id, "hello " + id);
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

}
/** 스프링 API 예외처리 ExceptionHandlerExceptionResolver : @ExceptionHandler */
/* HTML 화면 에러 vs API 에러 */
// 웹 브라우저에 HTML 화면을 제공할 때는 에러가 발생하면 BasicErrorController 사용하는게 편하다.
// 단순히 5xx,4xx 등 화면을 보여주면 된다. BasicErrorController 가 모두 구현해두었다.
// API 의 경우, 각 시스템마다 응답 스펙이 다르다. 단순히 화면을 보여주면 되는 것이 아니라,
// 예외에 따라 필요한 데이터를 출력해줘야 한다.
// 결국 BasicErrorController 를 사용하거나 HandlerExceptionResolver 직접 구현으로는 쉽지 않다.

// API 예외처리의 어려운점
// ・HandlerExceptionResolver 를 떠올려 보면 ModelAndView 를 반환해야 하는데, API 는 필요하지 않다.
// ・API 응답을 위해서 HttpServletResponse 에 직접 응답 데이터를 넣어주었따. 이는 번거롭다.
// 　스프링 컨트롤러에 비유하면 과거 서블릿을 사용하던 시절과 같다.
// ・특정 컨트롤러에서만 발생하는 예외를 별도 처리하기 어렵다. 예를 들어, 회원 관련 컨트롤러와 상품관리 컨트롤러에서
// 　발생하는 RuntimeException 을 각각 다른 방식으로 처리하고 싶을때 어렵다.

/** @ExceptionHandler */
// 스프링이 제공하는 API 예외 처리 핸들러. 이것이 ExceptionHandlerExceptionResolver 이다.
// 스프링은 ExceptionHandlerExceptionResolver 를 기본으로 제공하고, 우선순위 가장 높다.
// 실무에서 API 예외처리는 대부분 이 기능을 사용한다.

/* 스프링 우선순위 */
// 항상 자세한 것이 우선권을 가진다.
//   @ExceptionHandler(부모예외.class)
//   public String 부모예외처리()(부모예외 e) {}
//   @ExceptionHandler(자식예외.class)
//   public String 자식예외처리()(자식예외 e) {}
// @ExceptionHandler 에 지정한 부모 클래스는 자식 클래스까지 처리할 수 있다.
// 따라서 자식예외가 발생하면 부모예외처리(), 자식예외처리() 둘다 호출 대상이 된다.
// 그런데, 둘 중 더 자세한 것이 우선권을 가지므로 자식예외처리() 가 호출된다.
// 물론 부모예외 가 호출되면 부모예외처리() 만 호출 대상이 되므로 부모예외처리() 가 호출된다.
