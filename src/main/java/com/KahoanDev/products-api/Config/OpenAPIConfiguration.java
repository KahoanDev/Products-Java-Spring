package com.KahoanDev.poc_crud_api.Config;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import org.springframework.context.annotation.Configuration;

@Configuration
@OpenAPIDefinition(
        info = @Info(
                title = "Estoque API",
                version = "v1",
                contact = @Contact(
                        name = "Kahoan Oliveira",
                        email = "kahoan9@gmail.com",
                        url = "librabryapi.com"
                )
        )
)
public class OpenAPIConfiguration {
}
