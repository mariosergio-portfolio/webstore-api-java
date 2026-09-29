package com.mycompany.webstore.infrastructure.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {


    // http://localhost:8080/webstore/swagger-ui/index.html (yml context-path)
    @Bean
    public OpenAPI webstoreOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Web Store API (JAVA)")
                        .description("REST API for the Web Store e-commerce system — Catalog module")
                        .version("1.0.0"));
    }
}
