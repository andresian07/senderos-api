package com.andres.senderos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

import com.andres.senderos.persistence.entity.Difficulty;

import java.math.BigDecimal;

public record RouteUpdateDto(
        String name,
        String region,
        BigDecimal distanceKm,
        Integer elevationGainM,
        Difficulty difficulty,
        @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude
) {}
