package hello.exception;

import hello.exception.filter.LogFilter;
import hello.exception.interceptor.LogInterceptor;
import hello.exception.resolver.MyHandlerExceptionResolver;
import hello.exception.resolver.UserHandlerExceptionResolver;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import javax.servlet.DispatcherType;
import javax.servlet.Filter;
import java.util.List;

@Configuration
public class WebConfig implements WebMvcConfigurer {

//    @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new LogFilter());
        filterRegistrationBean.setOrder(1);
        filterRegistrationBean.addUrlPatterns("/*");
        // DispatcherType 추가 설정
        // 클라이언트 요청, 에러페이지 요청에서도 필터가 호출됨.
        // default 값 : DispatcherType.REQUEST (= 클라이언트 요청이 있을때만 필터 적용)
        // 즉, 특별히 에러 페이지 경로도 필터를 적용할 것이 아니라면 기본 값 그대로 사용하면 된다.
        // 에러 페이지 요청 전용 필터를 적용하고 있으면 DispatcherType.ERROR 만 지정하면 됨.
        filterRegistrationBean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.ERROR);
        return filterRegistrationBean;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**", "/*.ico", "/error", "/error-page/**"); // 에러 페이지 경로
    // -> /error-page/** 를 제거하면, error-page/500 같은 내부 호출의 경우에도 인터셉터 호출됨
    }

    /**
     * ExceptionResolver 활용 : 기본 설정을 유지하면서 추가
     */
    @Override
    public void extendHandlerExceptionResolvers(List<HandlerExceptionResolver> resolvers) {
        resolvers.add(new MyHandlerExceptionResolver());
        // 참고 : configureHandlerExceptionResolver(...) 를 사용하면 스프링이 기본으로 등록하는
        // ExceptionResolver 가 제거되므로 주의. extendHandlerExceptionResolver() 를 사용해야 한다.

        // 사용자 에러 추가
        resolvers.add(new UserHandlerExceptionResolver());
    }

}
