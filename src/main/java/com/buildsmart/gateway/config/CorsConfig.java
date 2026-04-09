package com.buildsmart.gateway.config;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;

@Configuration
public class CorsConfig {

    private static final String ALLOWED_ORIGIN = "http://localhost:3000";

    /**
     * CORS configuration handled at the gateway level.
     */
    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowedOrigins(Arrays.asList(ALLOWED_ORIGIN));
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        config.setAllowedHeaders(Arrays.asList("*"));
        config.setAllowCredentials(true);
        config.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return new CorsWebFilter(source);
    }

    /**
     * Global filter that strips CORS headers added by downstream services
     * BEFORE the gateway adds its own, preventing duplicate header values.
     */
    @Bean
    public GlobalFilter stripDownstreamCorsHeadersFilter() {
        return new GlobalFilter() {
            @Override
            public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
                return chain.filter(exchange).then(Mono.fromRunnable(() -> {
                    ServerHttpResponse response = exchange.getResponse();
                    HttpHeaders headers = response.getHeaders();

                    // Remove duplicate CORS headers from downstream, keep only gateway's
                    dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_ORIGIN);
                    dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_CREDENTIALS);
                    dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_METHODS);
                    dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_ALLOW_HEADERS);
                    dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_EXPOSE_HEADERS);
                    dedupeHeader(headers, HttpHeaders.ACCESS_CONTROL_MAX_AGE);
                }));
            }

            private void dedupeHeader(HttpHeaders headers, String name) {
                var values = headers.get(name);
                if (values != null && values.size() > 1) {
                    headers.set(name, values.get(0));
                }
            }
        };
    }
}
