package com.weave.weaveserver.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.Contact;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class SwaggerConfig extends WebMvcConfigurationSupport {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }

    @Bean
    public Docket api(){
        return new Docket(DocumentationType.SWAGGER_2)
                .useDefaultResponseMessages(false)
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.weave.weaveserver"))
                .paths(PathSelectors.any())
                .build()
                .apiInfo(metaData());
    }
    private ApiInfo metaData() {
        return new ApiInfoBuilder()
                .title("WEAVE REST API TEST")
                .description("Project Portpolio")
                .version("0.1.0")
                .termsOfServiceUrl("Terms of service")
                .contact(new Contact("codefolio", "https://github.com/UMC-WEAVE", ""))
                .license("Apache License Version 2.0")
                .licenseUrl("https://www.apache.org/licenses/LICENSE-2.0")
                .build();
    }

    private ApiInfo apiInfo() {
        String description = "Welcome WEAVE Api Test";
        return new ApiInfoBuilder()
                .title("SWAGGER TEST")
                .description(description)
                .version("1.0")
                .build();
    }

}