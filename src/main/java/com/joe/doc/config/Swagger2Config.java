package com.joe.doc.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.service.*;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;

import java.util.ArrayList;
import java.util.List;


/**
 * 在Swagger2的securityContexts中通过正则表达式，设置需要使用参数的接口（或者说，是去除掉不需要使用参数的接口），
 *
 *
 * @author JoeBlackZ
 */
@EnableSwagger2
@Configuration
public class Swagger2Config {

    private static final String TOKEN_PARAM_NAME = "Authorization";

    @Bean
    public Docket createRestApi() {

        return new Docket(DocumentationType.SWAGGER_2)
                .apiInfo(this.apiInfo())
                .select()
                .apis(RequestHandlerSelectors.basePackage("com.joe.doc.controller"))
                .build()
                .securitySchemes(this.securitySchemes())
                .securityContexts(this.securityContexts())
                ;
    }

    private ApiInfo apiInfo() {
        return new ApiInfoBuilder()
                .title("Doc-search API")
                .description("This is a restful api document of Doc-search.")
                .version("1.0")
                .build();
    }

    private List<ApiKey> securitySchemes() {
        List<ApiKey> apiKeyList = new ArrayList<>(1);
        apiKeyList.add(new ApiKey(TOKEN_PARAM_NAME, TOKEN_PARAM_NAME, "header"));
        return apiKeyList;
    }

    private List<SecurityContext> securityContexts() {
        List<SecurityContext> securityContexts = new ArrayList<>(1);
        SecurityContext securityContext = SecurityContext.builder()
                .securityReferences(this.defaultAuth())
                // 所有包含"auth"的接口不需要使用securitySchemes。即不需要使用上文中设置的名为Authorization,type为header的参数。
                .forPaths(PathSelectors.regex("^(?!auth).*$"))
                .build();
        securityContexts.add(securityContext);
        return securityContexts;
    }

    List<SecurityReference> defaultAuth() {
        AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
        AuthorizationScope[] authorizationScopes = new AuthorizationScope[]{authorizationScope};
        List<SecurityReference> securityReferences = new ArrayList<>();
        securityReferences.add(new SecurityReference(TOKEN_PARAM_NAME, authorizationScopes));
        return securityReferences;
    }

}
