package com.dp.gateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableSwagger2
public class SwaggerConfig {
    @Bean
    public Docket docket(){

        return new Docket(DocumentationType.SWAGGER_2).apiInfo(apiInfo()).enable(true)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.huahuaxiaomuzhu.j2ee.controller")).build();
    }
    private ApiInfo apiInfo(){
        Contact contact=new Contact("花花小母猪","","huahuaxiaomuzhu@qq.com");
        return new ApiInfo(
                "贴心的后端写的接口页面",
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
