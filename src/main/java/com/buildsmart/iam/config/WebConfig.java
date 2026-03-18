package com.buildsmart.iam.config;

import com.buildsmart.iam.audit.AuditInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    
    @Autowired
    private AuditInterceptor auditInterceptor;
    
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(auditInterceptor)
                .addPathPatterns("/api/v1/**")
                .excludePathPatterns("/api/v1/auth/**", "/auth/**",
                                  "/api/v1/swagger-ui/**", "/api/v1/v3/api-docs/**", 
                                  "/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html", "/api-docs/**");
    }
}
