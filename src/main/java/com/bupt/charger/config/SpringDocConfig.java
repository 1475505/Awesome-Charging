package com.bupt.charger.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author ll （ created: 2023-05-27 14:42 )
 */
@Configuration
public class SpringDocConfig {

    private String title = "BUPT Charge Sys";
    private String description = "authors: ll, ...";
    private String version = "v0.0.1";

    @Bean
    public OpenAPI chargerOpenAPI() {
        return new OpenAPI()
                .info(new Info().title(title)
                        .description(description)
                        .version(version));
    }
}