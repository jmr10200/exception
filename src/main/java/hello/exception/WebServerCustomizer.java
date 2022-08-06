package hello.exception;

import org.springframework.boot.web.server.ConfigurableWebServerFactory;
import org.springframework.boot.web.server.ErrorPage;
import org.springframework.boot.web.server.WebServerFactoryCustomizer;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

@Component
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
