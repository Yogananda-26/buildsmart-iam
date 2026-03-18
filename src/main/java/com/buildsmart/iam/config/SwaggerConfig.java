package com.buildsmart.iam.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class SwaggerConfig {
    
    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("BuildSmart IAM Service API")
                        .description("Identity and Access Management API for BuildSmart Construction Project Planning & Site Operations Management System")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("BuildSmart Development Team")
                                .email("dev@buildsmart.com")
                                .url("https://buildsmart.com"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:8081/api/v1")
                                .description("Development Server"),
                        new Server()
                                .url("https://api.buildsmart.com/api/v1")
                                .description("Production Server")
                ))
                .components(new Components()
                        .addSecuritySchemes("bearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")
                                .description("JWT Authentication token. Format: Bearer {token}"))
                )
                .addSecurityItem(new SecurityRequirement().addList("bearerAuth"));
    }
}
