package hello.exception.filter;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.UUID;

@Slf4j
public class LogFilter implements Filter {
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        log.info("log filter init");
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        String requestURI = httpServletRequest.getRequestURI();

        String uuid = UUID.randomUUID().toString();
        try {
            // DispatchType 로그출력 추가
            log.info("REQUEST [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
            chain.doFilter(request, response);
        } catch (Exception e) {
            throw e;
        } finally {
            // DispatchType 로그출력 추가
            log.info("RESPONSE [{}][{}][{}]", uuid, request.getDispatcherType(), requestURI);
        }
    }

    @Override
    public void destroy() {
        log.info("log filter destroy");
    }
}
/* 서블릿 예외 처리 - 필터 */
// 예외 처리에 따른 필터, 인터셉터 와 서블릿이 제공하는 DispatchType 이해하기

// 예외 발생과 오류 페이지 요청 흐름
// 1. WAS(여기까지 전달됨) <- 필터 <- 서블릿 <- 인터셉터 <- 컨트롤러(예외발생)
// 2. WAS("/error-page/500" 다시요청) -> 필터 -> 서블릿 -> 인터셉터 -> 컨트롤러(/error-page/500) -> View
// 에러가 발생하면 에러 페이지를 출력하기 위해 WAS 내부에서 다시한번 호출이 발생한다.
// 이때 필터, 서블릿, 인터셉터, 컨트롤러도 모두 다시 호출된다.
// 그런데, 로그인 인증 체크 같은 경우, 이미 한번 필터나 인터셉터에서 로그인 체크를 완료했다.
// 따라서 서버 내부에서 에러 페이지를 호출한다고 해서 해당 필터나 인터셉터를 또 호출하는 것은 비효율적이다.
// 결국 클라이언트로부터 발생한 정상 요청인지, 아니면 에러 페이지를 출력하기위한 내부 요청인지 구분해야 한다.
// 서블릿은 이런 문제를 해결하기위해 DispatchType 이라는 추가 정보를 제공한다.

