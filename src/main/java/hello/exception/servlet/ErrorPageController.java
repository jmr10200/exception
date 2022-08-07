package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 에러 페이지 컨트롤러
 */
@Slf4j
@Controller
public class ErrorPageController {

    // RequestDispatcher 상수로 정의되어 있음
    // 예외
    public static final String ERROR_EXCEPTION = "javax.servlet.error.exception";
    // 예외타입
    public static final String ERROR_EXCEPTION_TYPE = "javax.servlet.error.exception_type";
    // 에러 메세지
    public static final String ERROR_MESSAGE = "javax.servlet.error.message";
    // 클라이언트 요청 URI
    public static final String ERROR_REQUEST_URI = "javax.servlet.error.request_uri";
    // 에러가 발생한 서블릿 이름
    public static final String ERROR_SERVLET_NAME = "javax.servlet.error.servlet_name";
    // HTTP 상태 코드
    public static final String ERROR_STATUS_CODE = "javax.servlet.error.status_code";

    @RequestMapping("/error-page/404")
    public String errorPage404(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 404");
        return "error-page/404";
    }

    @RequestMapping("/error-page/500")
    public String errorPage500(HttpServletRequest request, HttpServletResponse response) {
        log.info("errorPage 500");
        return "error-page/500";
    }

    private void printErrorInfo(HttpServletRequest request) {
        log.info("ERROR_EXCEPTION: ex=", request.getAttribute(ERROR_EXCEPTION));
        log.info("ERROR_EXCEPTION_TYPE: {}", request.getAttribute(ERROR_EXCEPTION_TYPE));
        // ex의 경우 NestedServletException 스프링이 한번 감싸서 반환
        log.info("ERROR_MESSAGE: {}", request.getAttribute(ERROR_MESSAGE));
        log.info("ERROR_REQUEST_URI: {}", request.getAttribute(ERROR_REQUEST_URI));
        log.info("ERROR_SERVLET_NAME: {}", request.getAttribute(ERROR_SERVLET_NAME));
        log.info("ERROR_STATUS_CODE: {}", request.getAttribute(ERROR_STATUS_CODE));
        log.info("dispatchType={}", request.getDispatcherType());
    }
}
/* 서블릿 예외 처리 - 에러 페이지 작동 원리 */
// 서블릿은 Exception 가 발생해서 서블릿 밖으로 전달되거나 또는 response.sendError() 가 호출 되면 에러 페이지를 띄운다.

// 예외 발생 흐름
// WAS(여기까지 전달됨) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)

// sendError() 흐름
// WAS(sendError() 호출 확인) <- 필터 <- 서블릿 < 인터셉터 <- 컨트롤러

// WAS 는 해당 예외를 처리하는 오류 페이지 정보를 확인한다.
// new ErrorPage(RuntimeException.class, "error-page/500")

// 예를들어 RuntimeException 예외가 WAS 까지 전달되면, WAS 는 에러 페이지 정보를 확인한다.
// 확인했을때 RuntimeException 의 에러 페이지로 /error-page/500 이 지정되어 있다.
// WAS 는 에러 페이지를 출력하기 위해 /error-page/500 을 다시 요청한다.
// 1. WAS(여기까지 전달됨) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
// 2. WAS("/error-page/500" 다시요청) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View

// 중요 포인트
// 웹 브라우저(클라이언트)는 서버 내부에서 이런 일이 일어나는지 전혀 모른다는 점이다.
// 오직 서버 내부에서 오류 페이지를 찾기 위해 추가적인 호출을 한다.
// 정리하면
// 1. 예외가 발생해서 WAS 까지 에러가 전달된다.
// 2. WAS 는 에러 페이지 경로를 찾아내서 에러 페이지를 호출한다. 이때 에러 페이지 경로로 필터, 서블릿, 인터셉터, 컨트롤러가 모두 다시 호출된다.

// 에러 정보 추가
// WAS 는 에러 페이지를 단순히 다시 요청만 하는 것이 아니라, 에러 정보를 request 의 attribute 에 추가해서 넘긴다.
// 필요하면 에러 페이지에 이렇게 전달된 에러 정보를 표시할 수 있다.


/* DispatchType */
// 클라이언트로부터 발생한 정상 요청인지, 에러 페이지를 출력하기위한 내부 요청인지 구분하기 위한,
// 필터가 제공하는 옵션 : dispatchTypes
// log.info("dispatchType={}", request.getDispatcherType());
// 출력해보면 dispatchType=ERROR 으로 나온다.
// 유저가 처음 요청하면 dispatchType=REQUEST 이다. 즉, 서버가 내부에서 구분할 수 있게 해준다.
// ・javax.servlet.DispatcherType
// public enum DispatcherType {
// FORWARD, : 서블릿에서 다른 서블릿이나 JSP 를 호출할 때. RequestDispatcher.forward(request, response)
// INCLUDE, : 서블릿에서 다르 서블릿이나 JSP 결과를 포함할 때. RequestDispatcher.include(request, response)
// REQUEST, : 클라이언트 요청
// ASYNC, : 서블릿 비동기 호출
// ERROR : 에러 요청

/* 스프링 부트의 에러 페이지 */
// 지금까지 예외 처리 페이지를 만들기 위해서 다음과 같은 복잡한 과정을 거쳤다.
// 1. WebServerCustomer 생성
// 2. 예외 종류에 따라 ErrorPage 추가
// 3. 예외 처리용 컨트롤러 ErrorPageController 생성

// 스프링 부트는 이런 과정을 모두 기본으로 제공한다.
// ErrorPage 를 자동으로 등록한다. 이때 /error 라는 경로로 기본 에러 페이지를 설정한다.
// -> new ErrorPage("/error") , 상태코드와 예외를 설정하지 않으면 기본 에러 페이지로 사용된다.
// -> 서블릿 밖으로 예외가 발생하거나, response.sendError(...) 가 호출되면 모든 오류는 /error 를 호출하게 된다.
// BasicErrorController 라는 스프링 컨트롤러를 자동으로 등록한다.
// -> ErrorPage 에서 등록한 /error 를 매핑해서 처리하는 컨트롤러이다.

// 참고 : ErrorMvcAutoConfiguration 이라는 클래스가 에러 페이지를 자동으로 등록하는 역할을 한다.
// 스프링을 사용하기 위해서 WebServerCustomizer 의 @Component 를 주석처리
// 스프링은 BasicErrorController 를 빈으로 자동등록하며, /error 경로로 기본 에러페이지를 설정함
// 즉, 개발자는 BasicErrorController 가 제공하는 룰과 우선순위에따라 에러 페이지만 등록하면 된다.
// 정적 HTML 이면 정적 리소스, 뷰 템플릿으로 동적으로 만들고 싶으면 뷰 템플릿 경로에 만들자.

/* 뷰 선택 우선순위 (BasicErrorController 의 처리순서) */
// 1. 뷰 템플릿
// ・resources/templates/error/500.html
// ・resources/templates/error/5xx.html
// 2. 정적 리소스 ( static , public )
// ・resources/static/error/400.html
// ・resources/static/error/404.html
// ・resources/static/error/4xx.html
// 3. 적용 대상이 없을 때 뷰 이름 ( error )
// ・resources/templates/error.html
// 해당 경로에 HTTP 상태 코드 이름의 뷰 파일을 넣어두면 된다.
// 뷰 템플릿이 정적 리소스보다 우선순위가 높고, 404, 500 처럼 구체적인 것이 5xx 보다 우선순위가 높다.
// 5xx, 4xx 라고 하면 각각 500대, 400대 에러를 처리해준다.

// 테스트 결과
// error-404 : 404.html
// error-400 : 4xx.html (400 에러페이지가 없지만 4xx가 있음)
// error-500 : 500.html
// error-ex : 500.html (예외는 500으로 처리)