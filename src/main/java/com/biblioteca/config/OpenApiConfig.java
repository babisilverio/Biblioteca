package com.biblioteca.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.Contact;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI customOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("API da Biblioteca")
                        .description("API para gerenciamento de biblioteca")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Babi")
                                .email("babi10costa@gmail.com")));
    }
}