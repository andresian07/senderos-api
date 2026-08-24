package com.andres.senderos.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record OpenMeteoResponseDto(
        Double latitude,
        Double longitude,
        DailyDto daily
) {
    public record DailyDto(
            List<String> time,

            @JsonProperty("temperature_2m_max")
            List<Double> temperatureMaxC,

            @JsonProperty("temperature_2m_min")
            List<Double> temperatureMinC,

            @JsonProperty("precipitation_sum")
            List<Double> precipitationSumMm,

            @JsonProperty("weathercode")
            List<Integer> weatherCode
    ) {}
}
