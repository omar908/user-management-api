package com.example.demo.config;

import com.example.demo.util.EmailValidator;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableConfigurationProperties(DemoProperties.class)
public class AppConfig {

    @Bean
    public EmailValidator emailValidator() {
        return new EmailValidator();
    }
}


