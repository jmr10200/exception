package hello.exception.api;

import hello.exception.UserException;
import hello.exception.exception.BadRequestException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Slf4j
@RestController
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
        }

        // localhost:8080/api/members/bad : IllegalArgumentException 호출
        // -> 실행 결과 : 상태코드 500
        if (id.equals("bad")) {
            throw new IllegalArgumentException("잘못 입력 값");
        }

        // 사용자 정의 예외
        // localhost:8080/api/members/user-ex : UserException 호출
        // -> 실행결과 : { "ex": "hello.exception.exception.UserException", "message": "사용자 오류" }
        if (id.equals("user-ex")) {
            throw new UserException("사용자 에러");
        }

        return new MemberDto(id, "hello " + id);
        // 테스트1 : localhost:8080/api/members/spring
        // 결과 : { "memberId" : "spring", "name" : "hello spring" }
        // -> api 를 요청했는데, 정상인경우 JSON 데이터가 반환된다.
        // 테스트2 : localhost:8080/api/members/ex
        // 결과 : 500 에러페이지 표시
        // -> 클라이언트는 정상 요청이든, 에러 요청이든 JSON 데이터가 반환되기를 기대한다.
        // 웹 브라우저가 아닌이상, HTML 을 직접 받아서 할 수 있는 것은 별로 없다.
    }

    @Data
    @AllArgsConstructor
    static class MemberDto {
        private String memberId;
        private String name;
    }

    @GetMapping("/api/response-status-ex1")
    public String responseStatusEx1() {
        throw new BadRequestException();
    }

    @GetMapping("/api/response-status-ex2")
    public String responseStatusEx2() {
        throw new ResponseStatusException(HttpStatus.NOT_FOUND, "error.bad", new IllegalArgumentException());
        // ResponseStatusException
        // @ResponseStatus 는 개발자가 직접 변경할 수 없는 예외에는 적용 불가.
        // (어노테이션을 직접 넣어야 하는데, 내가 코드를 수정할 수 없는 라이브러리의 예외 코드 같은 곳에는 적용 불가)
        // 추가로 어노테이션을 사용하기 때문에 조건에 따라 동적으로 변경하는 것도 어렵다.
        // 이때는, ResponseStatusException 예외를 사용하면 된다.
    }

    @GetMapping("/api/default-handler-ex")
    public String defaultException(@RequestParam Integer data) {
        // Integer data 에 문자를 입력하면 내부에서 TypeMismatchException 발생
        return "ok";
    }

}
/* API 예외처리 - 스프링 부트 기본 에러 처리 */
// api 예외 처리도 스프링 부트가 제공하는 기본 오류 방식을 사용할 수 있다.
// 스프링 부트가 제공하는 BasicErrorController 코드를 보면,
// @RequestMapping(produces = MediaType.TEXT_HTML_VALUE)
// public ModelAndView errorHtml(HttpServletRequest request, HttpServletResponse response) {}
// @RequestMapping
// public ResponseEntity<Map<String, Object>> error(HttpServletRequest request) {}

// /error 동일 한 경로를 처리하는 errorHtml(), error() 두 메소드를 확인할 수 있다.
// errorHtml() : produce = MediaType.TEXT_HTML_VALUE : 클라이언트 요청의 Accept 값이 text/html 이면 errorHtml() 호출해서 view 를 제공한다.
// error() : 그 외 호출되고 ResponseEntity 로 HTTP Body 에 JSON 데이터를 반환한다.

// 스프링 부트의 기본 설정은 에러 발생시 /error 를 에러 페이지로 요청한다.
// BasicErrorController 는 이 경로를 기본으로 받는다. (server.error.path 로 수정가능, 기본경로 error)

// 주의 : BasicErrorController 를 사용하려면 WebServerCustomize 의 @Component 를 주석처리 해야함!!
// 테스트 : GET localhost:8080/api/members/ex
// 결과
// {
//    "timestamp": "2022-08-08T10:57:10.240+00:00",
//    "status": 500,
//    "error": "Internal Server Error",
//    "exception": "java.lang.RuntimeException",
//    "path": "/api/members/ex"
// }
// application.properties 의 설정에 따라 출력정보를 수정할 수 있다. (로그로 출력하도록 하자)
// server.error.include-binding-errors=always
// server.error.include-exception=true
// server.error.include-message=always
// server.error.include-stacktrace=always

/* Html 페이지 vs API 에러 */
// BasicErrorController 를 확장하면 JSON 메시지도 변경할 수 있다. (이해를 위해 변경할 수 있다 정도만)
// API 에러의 경우, @ExceptionHandler 가 제공하는 기능을 사용하는 것이 더 나은 방법이다.

// 스프링 부트가 제공하는 BasicErrorController 는 HTML 페이지를 제공하는 경우에는 매우 편리하지만,
// api 에러의 경우, 각각의 컨트롤러나 예외 마다 응답 결과를 다르게 출력해야 할 수도 있다.
// 따라서 이 방법은 HTML 화면을 처리할 때 사용하고, API 에러 처리는 @ExceptionHandler 를 사용하자.

/* api 예외처리 - HandlerExceptionResolver */
// 목표 : 예외가 발생해서 서블릿을 넘어 WAS 까지 에러가 전달되면 HTTP 상태코드가 500으로 처리된다.
// 발생하는 에러에 따라서 400, 404 등 다른 상태코드로, 메시지, 형식 등을 api 마다 다르게 처리하고 싶다.

// 상태코드 변환
// 예를들어 IllegalArgumentException 을 처리하지 못해서 컨트롤러 밖으로 넘어간 경우 400 으로 처리

// HandlerExceptionResolver
// 스프링 MVC 는 컨트롤러(핸들러) 밖으로 에러가 던저진 경우 예외를 해결하고, 동작을 새로 정의할 수 있는 방법을 제공한다.
// 줄여서 ExceptionResolver 라고 한다.

// ExceptionResolver 적용전
// 1.preHandle -> 2.handle(handler) -> 3.예외발생 x -> 예외전달 x -> postHandle() 호출 x
// 5.afterCompletion(ex) -> 6.예외전달 x

// ExceptionResolver 적용후
// 1.preHandle -> 2.handle(handler) -> 3.예외발생 x -> 예외전달 x -> 5. 예외 해결시도 ExceptionResolver
// 6.render(model) 호출 -> View -> HTML 응답 -> 7.afterCompletion(ex) -> 8.정상응답
// 참고 : ExceptionResolver 로 예외를 해결해서 postHandle() 은 호출되지 않는다.

/** 스프링 API 예외처리 ExceptionResolver */
// 스프링은 HandlerExceptionResolverComposite 에 다음 순서로 등록한다.
// 1. ExceptionHandlerExceptionResolver
//    : @ExceptionHandler 를 처리한다. API 예외 처리는 대부분 이 기능으로 해결한다.
// 2. ResponseStatusExceptionResolver
//    : HTTP 상태 코드를 지정해준다.
//    : 예) @ResponseStatus(value = HttpStatus.NOT_FOUND)
// 3. DefaultHandlerExceptionResolver -> 우선 순위 가장 낮다
//    : 스프링 내부 기본 예외를 처리한다.

// DefaultHandlerExceptionResolver
// 스프링 내부에서 발생하는 스프링 예외를 해결
// 대표적으로 파라미터 바인딩 시점에 타입이 맞지 않으면 TypeMismatchException 발생
// 이 경우, 예외가 발생했기 때문에 그냥 두면 서블릿 컨테이너까지 에러가 올라가고, 500 발생한다.
// 그런데 파라미터 바인딩은 대부분 클라이언트가 HTTP 요청 정보를 잘못 호출해서 발생하는 문제이다.
// HTTP 에서는 이런 경우 HTTP 상태 코드 400 을 사용하도록 되어 있다.
// DefaultHandlerExceptionResolver 는 이것을 500 에러가 아니라 400 에러로 변경한다.
// 스프링 내부 에러를 어떻게 처리할지 수 많은 내용이 정의되어 있다.

// 코드 확인
// response.sendError(HttpServletResponse.SC_BAD_REQUEST)(400) -> response.sendError() 이용
// sendError(400) 를 호출했기 때문에 WAS 에서 다시 에러페이지 /error 를 내부 요청한다.

// 정리
// HTTP 상태 코드를 변경하고, 스프링 내부 예외의 상태코드를 변경하는 기능이 있다.
// 그런데 HandlerExceptionResolver 를 직접 사용하기에는 복잡하다.
// API 에러응답의 경우 response 에 직접 데이터를 넣어야해서 복잡하고 번거롭다.
// ModelAndView 를 반환해야 하는 것도 API 에는 잘 맞지 않는다.
// 스프링은 이 문제를 해결하기 위해 @ExceptionHandler 라는 예외처리 기능을 제공한다.
// 이것이 ExceptionHandlerExceptionResolver 이다.