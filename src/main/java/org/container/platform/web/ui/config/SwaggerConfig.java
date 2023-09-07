package org.container.platform.web.ui.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Swagger Config
 *
 * @author kjh
 * @version 1.0
 * @since 2020.10.16
 **/
@Configuration
@EnableSwagger2
public class SwaggerConfig {

    private static final Set<String> DEFAULT_PRODUCES_AND_CONSUMES = new HashSet<>(
            Arrays.asList("application/json"));

    @Bean
    public Docket api() {

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("v1.0")
                .select()
                .apis(RequestHandlerSelectors.basePackage("org.container.platform.web.ui"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(apiInfo())
                .produces(DEFAULT_PRODUCES_AND_CONSUMES)
                .consumes(DEFAULT_PRODUCES_AND_CONSUMES);

    }

    @Bean
    public ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("PaaS-TA Container Platform UI Docs")
                .version("v1.0")
                .description("This is a API Document created with swagger.")
                .license("Apache2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

}