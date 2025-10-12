package app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfiguration implements WebMvcConfigurer {

    private final SessionCheckInterceptor sessionCheckInterceptor;

    @Autowired
    public WebConfiguration(SessionCheckInterceptor sessionCheckInterceptor) {
        this.sessionCheckInterceptor = sessionCheckInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {

        // /** - всичко след
        // .addPathPatterns - къде да бъде приложен този interceptor
        // .excludePathPatterns - къде да не бъде приложен този interceptor
        registry.addInterceptor(sessionCheckInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns("/css/**","/images/**");
    }
}
