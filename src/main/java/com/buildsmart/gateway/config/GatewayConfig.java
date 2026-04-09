package com.buildsmart.gateway.config;

import com.buildsmart.gateway.filter.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()

                // IAM - Public auth endpoints (no JWT filter)
                .route("iam-auth-public", r -> r
                        .path("/api/auth/signup", "/api/auth/login",
                                "/api/auth/forgot-password", "/api/auth/reset-password",
                                "/api/auth/validate-reset-token/**")
                        .uri("lb://iam-service"))

                // IAM - Protected auth endpoints (JWT filter applied)
                .route("iam-auth-protected", r -> r
                        .path("/api/auth/logout")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://iam-service"))

                // IAM - User endpoints (JWT required)
                .route("iam-users", r -> r
                        .path("/users/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://iam-service"))

                // IAM - Admin endpoints (JWT required)
                .route("iam-admin", r -> r
                        .path("/admin/**")
                        .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                        .uri("lb://iam-service"))

                // Future: Add other service routes here
                // .route("project-service", r -> r
                //         .path("/projects/**")
                //         .filters(f -> f.filter(jwtAuthenticationFilter.apply(new JwtAuthenticationFilter.Config())))
                //         .uri("lb://project-service"))

                .build();
    }
}
