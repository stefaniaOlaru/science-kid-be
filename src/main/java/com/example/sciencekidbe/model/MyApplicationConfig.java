package com.example.sciencekidbe.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class MyApplicationConfig {
    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
