package com.example.servicedirectory.common;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI serviceDirectoryOpenApi() {
        return new OpenAPI().info(new Info()
                .title("Service Directory API")
                .description("Enterprise service directory: teams and the services they own.")
                .version("v1"));
    }
}