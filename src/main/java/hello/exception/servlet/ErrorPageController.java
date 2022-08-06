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