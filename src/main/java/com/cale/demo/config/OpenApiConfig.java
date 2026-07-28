package com.cale.demo.config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Blog API",
                version = "1.0",
                description = "API REST para la gestión de usuarios, posts, categorías y comentarios.",
                contact = @Contact(
                        name = "Gastón",
                        email = "gaston@mail.com"
                )
        )
)
public class OpenApiConfig {
}
