package com.andres.senderos.client;

import com.andres.senderos.client.dto.OpenMeteoResponseDto;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.math.BigDecimal;

@Component
public class OpenMeteoClient {

    private final RestClient openMeteoRestClient;

    public OpenMeteoClient(RestClient openMeteoRestClient) {
        this.openMeteoRestClient = openMeteoRestClient;
    }

    public OpenMeteoResponseDto getForecast(BigDecimal latitude, BigDecimal longitude) {
        return this.openMeteoRestClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/forecast")
                        .queryParam("latitude", latitude)
                        .queryParam("longitude", longitude)
                        .queryParam("daily", "temperature_2m_max,temperature_2m_min,precipitation_sum,weathercode")
                        .queryParam("timezone", "auto")
                        .build())
                .retrieve()
                .body(OpenMeteoResponseDto.class);
    }
}
