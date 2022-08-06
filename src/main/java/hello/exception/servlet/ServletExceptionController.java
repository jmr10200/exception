package hello.exception.servlet;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Controller
public class ServletExceptionController {

    @GetMapping("/error-ex")
    public void errorException() {
        throw new RuntimeException("Exception 발생");
        // 실행해보면 tomcat 이 기본으로 제공하는 에러 화면을 볼 수 있다.
        // HTTP Status 500 - Internal Server Error
        // 브라우저의 개발자 모드로 확인해보면 HTTP status code = 500 이다
        // 아무 디렉토리로 접근 해보면 (예) localhost:8080/page-nothing
        // HTTP Status 404 - Not Found
    }

    @GetMapping("/error-404")
    public void error404(HttpServletResponse response) throws IOException {
        response.sendError(404, "404 error!!");

    }

    @GetMapping("/error-500")
    public void error500(HttpServletResponse response) throws IOException {
        response.sendError(500);
    }
}

/* 서블릿 예외 처리 */
// 스프링이 아닌 순수 서블릿 컨테이너의 예외 처리
// 1. Exception (예외)
// 2. response.sendError(HTTP 상태 코드, 오류 메시지)

// Exception (예외)
// ・자바 직접 실행
// 자바의 메인 메소드를 직접 실행하는 경우 main 이라는 이름의 스레드가 실행된다.
// 실행 도중에 예외를 잡지 못하고 처음 실행한 main() 메소드를 넘어서 예외가 던져지면, 예외 정보를 남기고 해당 스레드는 종료된다.

// ・웹 어플리케이션
// 웹 어플리케이션은 사용자 요청별로 스레드가 할당되고, 서블릿 컨테이너 안에서 실행된다.
// 어플리케이션에서 예외가 발생했는데, 어디서가 try ~ catch 로 예외를 잡아서 처리하면 문제가 없다.
// 만약 어플리케이션에서 예외를 못잡고 서블릿 밖으로 까지 예외가 전달되면?
// WAS(여기까지 전파) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
// tomcat 같은 WAS 까지 예외가 전달된다. WAS 가 제공하는 기본 에러페이지가 보여진다.

// response.sendError(HTTP 상태 코드, 오류 메시지)
// 오류가 발생했을 때 HttpServletResponse 가 제공하는 sendError 라는 메소드 사용해도 된다.
// 이를 호출한다고 당장 예외가 발생하는 것은 아니지만, 서블릿 컨테이너에게 오류가 발생했다는 것을 전달할 수 있다.
// 이 메소드로 HTTP 상태 코드와 에러 메시지도 추가할 수 있다.
// response.sendError(HTTP Status Code)
// response.sendError(HTTP Status Code, Error Message)

// WAS(sendError 호출 기록 확인) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러
// response.sendError() 를 호출하면 response 내부에는 오류가 발생했다는 상태를 저장해둔다.
// 서블릿 컨테이너는 고객에게 응답 전에 response 에 sendError() 가 호출되었는지 확인한다.
// 그리고 호출되었다면 설정한 오류 코드에 맞추어 기본 오류 페이지를 보여준다.

// => 서블릿 컨테이너가 제공하는 기본 예외 처리 화면은 사용자가 보기에 불편하므로 의미 있는 에러 페이지를 제공해야 한다.

// 서블릿은 Exception 이 발생해서 서블릿 밖으로 전달되거나 response.sendError() 가 호출 될 때 에러 처리 기능을 제공한다.
// 스프링 부트를 통해 서블릿 컨테이너를 실행하기 때문에 스프링 부트를 이용하여 서블릿 에러 페이지를 등록하자.