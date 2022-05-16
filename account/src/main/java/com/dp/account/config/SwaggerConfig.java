package com.dp.account.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.oas.annotations.EnableOpenApi;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket() {

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.dp.account.controller")).build();
    }

    private ApiInfo apiInfo() {
        Contact contact = new Contact("后端同学", "", "");
        return new ApiInfo(
                "接口页面",
                "",
                "0.0.1",
                "",
                contact,
                "",
                "",
                new ArrayList<>()
        );
    }
}