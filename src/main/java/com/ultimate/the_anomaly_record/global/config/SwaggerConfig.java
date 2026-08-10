package com.ultimate.the_anomaly_record.global.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SwaggerConfig {
    @Bean
    public OpenAPI openAPI(){
        Info info = new Info()
                .version("v1.0") //버전
                .title("User API")
                .description("Unreal5 클라이어트 내장서버");
        return new OpenAPI()
                .info(info);
    }

}
