package toy.ktx.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.ktx.web.converter.StringToLocalDateTimeConverter;
import toy.ktx.web.interceptor.SignInInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addFormatters(FormatterRegistry registry) {
        //api controller에서 search condition으로 dateTime이 넘어올 때 LocalDateTime으로 바로 받을 수 있게 하기 위한 컨버터
        registry.addConverter(new StringToLocalDateTimeConverter());
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        //로그인 인증 인터셉터
        registry.addInterceptor(new SignInInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/sign-up", "/sign-in", "/css/**", "/js/**", "/*.ico", "/error", "/api/**");
    }
}
