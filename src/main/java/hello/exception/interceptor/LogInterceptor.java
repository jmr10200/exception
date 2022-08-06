package hello.exception.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {

    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String requestURI = request.getRequestURI();
        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, uuid);

        log.info("REQUEST [{}][{}][{}][{}]", uuid, request.getDispatcherType(), requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        log.info("postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String) request.getAttribute(LOG_ID);

        log.info("RESPONSE [{}][{}][{}]", logId, request.getDispatcherType(), requestURI);
        if (ex != null) {
            log.error("afterCompletion error!!", ex);
        }
    }
}

/* 서블릿 예외 처리 - 인터셉터 */
// 필터의 경우 필터를 등록할 때 어떤 DispatcherType 인 경우에 필터를 적용할지 선택할 수 있었다.
// 인터셉터는 서블릿이 제공하는 기능이 아니라 스프링이 제공하기 때문에 DispatcherType 과 무관하게 항상 호출된다.
// 대신, 인터셉터는 다음과 같이 요청 경로에 따라서 추가하거나 제외하기 쉽게 되어있다.
// excludePathPatterns() 를 사용해서 제거해주면된다.

// 흐름 정리
// /hello 정상 요청
// WAS ( /hello, dispatcherType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러 -> View
// /error-ex 에러 요청
// ・필터 : DispatchType 으로 중복 호출 제거 (dispatchType=REQUEST)
// ・인터셉터 : 경로 정보로 중복 호출 제거 (excludePathPatterns("/error-page/**")
// 1. WAS ("/error-ex", dispatchType=REQUEST) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러
// 2. WAS (여기까지 전달) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
// 3. WAS 에러 페이지 확인
// 4. WAS ("/error-page/500", dispatchType=ERROR) -> 필터(x) -> 서블릿 -> 인터셉터(x) -> 컨트롤러(/error-page/500) -> View
