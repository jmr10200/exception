package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

// 스프링을 사용하기 위해 주석처리
// 스프링은 BasicErrorController 를 빈으로 자동등록하며, /error 경로로 기본 에러페이지를 설정함
// 즉, 개발자는 BasicErrorController 가 제공하는 룰과 우선순위에따라 에러 페이지만 등록하면 된다.
// 정적 HTML 이면 정적 리소스, 뷰 템플릿으로 동적으로 만들고 싶으면 뷰 템플릿 경로에 만들자.
//@Component
public class WebServerCustomizer implements WebServerFactoryCustomizer<ConfigurableWebServerFactory> {

    @Override
    public void customize(ConfigurableWebServerFactory factory) {

        ErrorPage errorPage404 = new ErrorPage(HttpStatus.NOT_FOUND, "/error-page/404");
        ErrorPage errorPage500 = new ErrorPage(HttpStatus.INTERNAL_SERVER_ERROR, "/error-page/500");
        // 500 - Internal Server Error : 여기서는 예외가 발생한 경우도 서버 내부 발생오류로 넘기도록했다.
        // RuntimeException 또는 그 자식 타입의 예외 : errorPageEx 호출
        // 에러 페이지는 예외를 다룰 때 해당 예외와 그 자식 타입의 오류를 함께 처리한다.
        // 즉, RuntimeException 뿐만아니라 그 자식 타입도 처리한다.
        ErrorPage errorPageEx = new ErrorPage(RuntimeException.class, "/error-page/500");
        factory.addErrorPages(errorPage404, errorPage500, errorPageEx);

    }
}
/* API 예외처리 */
// HTML 의 경우, 4xx,5xx 와 같은 에러페이지만 있으면 대부분의 문제가 해결가능하다.
// API 의 경우에는 각 에러 상황에 맞는 응답 스펙을 정하고, JSON 으로 데이터를 전달해줘야 한다.
