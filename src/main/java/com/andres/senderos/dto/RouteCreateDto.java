package com.andres.senderos.dto;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;

import com.andres.senderos.persistence.entity.Difficulty;

import java.math.BigDecimal;

public record RouteCreateDto(
        @NotBlank String name,
        @NotBlank String region,
        @NotNull @Positive BigDecimal distanceKm,
        @NotNull @PositiveOrZero Integer elevationGainM,
        @NotNull Difficulty difficulty,
        @DecimalMin("-90.0") @DecimalMax("90.0") BigDecimal latitude,
        @DecimalMin("-180.0") @DecimalMax("180.0") BigDecimal longitude
) {}