package hello.exception.resolver;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
public class MyHandlerExceptionResolver implements HandlerExceptionResolver {

    // handler : 핸들러(컨트롤러) 정보, Exception ex : 핸들러(컨트롤러)에서 발생한 예외
    @Override
    public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) {
        // ExceptionResolver 가 ModelAndView 를 반환하는 이유는 마치 try, catch 처럼 Exception 을 처리해서 정상 흐름처럼 변경하는 것이 목적이다.
        // 이름 그대로 Exception 을 Resolver (해결) 하는 것이 목적이다.
        try {
            // IllegalArgumentException 이 발생하면
            if (ex instanceof IllegalArgumentException) {
                log.info("IllegalArgumentException resolver to 400");
                // response.sendError(400) 을 호출해서 Http status code 를 400으로 지정
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, ex.getMessage());
                // 빈 ModelAndView 반환
                return new ModelAndView();
            }
        } catch (IOException e) {
            log.info("resolver ex", e);
        }

        return null;
        /* 반환 값에 따른 동작 방식 */
        // 빈 ModelAndView : 뷰를 렌더링 하지 않고, 정상 흐름으로 서블릿이 리턴한다.
        // ModelAndView 지정 : ModelAndView 에 View, Model 등의 정보를 지정해서 반환하면 렌더링 한다.
        // null : null 은 다음 ExceptionResolver 를 찾아서 실행한다. 만약 ExceptionResolver 가 없으면 예외 처리가 안되고, 기존에 발생한 예외를 서블릿 밖으로 던진다.
    }
}
/* ExceptionResolver 활용 */
// ・예외 상태 코드 변환
// 예외를 response.sendError(xxx) 호출로 변경해서 서블릿에서 상태 코드에 따른 에러를 처리하도록 위임
// 이후 WAS 는 서블릿 에러페이지를 찾아서 내부 호출, 예를들어 스프링 부트가 기본으로 설정한 /error 가 호출됨
// ・뷰 템플릿 처리
// ModelAndView 에 값을 채워서 예외에 따른 새로운 에러화면 뷰 렌더링해서 고객에게 제공
// ・API 응답 처리
// response.getWriter().println("hello"); 처럼 HTTP 응답 body 에 직접 데이터를 넣어주는 것도 가능하다.
// 여기에 JSON 으로 응답하면 API 응답 처리를 할 수 있다.