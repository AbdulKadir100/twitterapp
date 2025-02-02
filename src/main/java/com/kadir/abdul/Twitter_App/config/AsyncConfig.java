package com.kadir.abdul.Twitter_App.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

import io.swagger.v3.oas.annotations.ExternalDocumentation;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Contact;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.info.License;

@OpenAPIDefinition(info = @Info(title = "Spring Boot Twitter App REST APIs",
 description = "Spring Boot Twitter App REST APIs Documentation",
  version = "v1.0", contact = @Contact(name = "Abdul Kadir", 
  email = "abdulabdul82524@gmail.com", url = "bio.link/abdulk"), 
  license = @License(name = "Apache 2.0", url = ""), 
  termsOfService = "This app is all about learning and POC purpose"),
  externalDocs = @ExternalDocumentation(description = "Spring Boot Blog App Documentation",
   url = "https://github.com/AbdulKadir100/blog_web_app/tree/bugfix/blog-rest-api"))
@Configuration
@Async
public class AsyncConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
