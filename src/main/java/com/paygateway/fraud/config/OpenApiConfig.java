package com.paygateway.fraud.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.Components;
import jakarta.servlet.ServletContext;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI fraudGatewayOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Payment Gateway — Fraud Detection & Risk API")
                        .version("2.0.0")
                        .description("Mock server for the Payment Gateway Fraud Detection and Risk "
                                + "Management service. Covers real-time risk scoring, manual review "
                                + "workflows, and configurable fraud rules.\n\n"
                                + "## Authentication\nUse your secret API key via HTTP Basic Auth "
                                + "(username = key, password empty), or a Bearer JWT.")
                        .contact(new Contact()
                                .name("Payment Gateway Support")
                                .email("api-support@paygateway.example.com")
                                .url("https://docs.paygateway.example.com/fraud"))
                        .license(new License()
                                .name("Proprietary")
                                .url("https://paygateway.example.com/terms")))
                .addSecurityItem(new SecurityRequirement().addList("BasicAuth"))
                .addSecurityItem(new SecurityRequirement().addList("BearerAuth"))
                .components(new Components()
                        .addSecuritySchemes("BasicAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("basic")
                                .description("Use secret key as username, empty password"))
                        .addSecuritySchemes("BearerAuth", new SecurityScheme()
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
