package com.uom.Software_design_competition.application.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    @Value("${server.port:5509}")
    private String serverPort;

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Transformer Thermal Inspection API")
                        .version("1.0.0")
                        .description("API documentation for Transformer Thermal Inspection System - A comprehensive platform for managing transformer inspections, baseline/thermal image uploads, and AI-powered thermal anomaly analysis.")
                        .contact(new Contact()
                                .name("Team Arbitary")
                                .email("support@arbitary.com")
                                .url("https://github.com/Team-Arbitary"))
                        .license(new License()
                                .name("MIT License")
                                .url("https://opensource.org/licenses/MIT")))
                .servers(List.of(
                        new Server()
                                .url("http://localhost:" + serverPort)
                                .description("Local Development Server"),
                        new Server()
                                .url("https://arbit-backend.vercel.app")
                                .description("Production Server (if deployed)")
                ));
    }
}
