package hello.exception.api;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class ApiExceptionController {

    @GetMapping("/api/members/{id}")
    public MemberDto getMember(@PathVariable("id") String id) {

        if (id.equals("ex")) {
            throw new RuntimeException("잘못된 사용자");
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