package toy.ktx.web;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import toy.ktx.web.interceptor.signInInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new signInInterceptor())
                .order(1)
                .addPathPatterns("/**")
                .excludePathPatterns("/", "/sign-up", "/sign-in", "/css/**",
                        "/*.ico", "/error", "/schedule");
    }
}
