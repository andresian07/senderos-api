package com.andres.senderos.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

@Configuration
public class RestClientConfig {

    @Bean
    public RestClient openMeteoRestClient() {
        return RestClient.builder()
                .baseUrl("https://api.open-meteo.com")
                .build();
    }
}