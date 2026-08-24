package com.andres.senderos.dto;

import java.time.LocalDate;

public record WeatherForecastDto(
        LocalDate date,
        Double temperatureMaxC,
        Double temperatureMinC,
        Double precipitationMm,
        String description
) {}
