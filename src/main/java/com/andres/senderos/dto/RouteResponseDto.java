package com.andres.senderos.dto;

import com.andres.senderos.persistence.entity.Difficulty;

import java.math.BigDecimal;

public record RouteResponseDto(
        Long id,
        String name,
        String region,
        BigDecimal distanceKm,
        Integer elevationGainM,
        Difficulty difficulty,
        BigDecimal latitude,
        BigDecimal longitude
) {}