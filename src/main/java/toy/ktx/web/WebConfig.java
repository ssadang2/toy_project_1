package toy.ktx.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.ktx.web.interceptor.signInInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // "/css/**" << 이거 빼고 css 적용됨. 아마 강의 찍은 뒤에 패치되지 않았을까
        registry.addInterceptor(new signInInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/sign-up", "/sign-in", "/css/**",
                        "/*.ico", "/error", "/schedule");
    }
}
