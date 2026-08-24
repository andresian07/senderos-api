package com.andres.senderos.dto;

import com.andres.senderos.persistence.entity.Difficulty;

import java.time.LocalDate;

public record HikeHistoryResponseDto(
        Long id,
        Long userId,
        Long routeId,
        String routeName,
        Difficulty difficulty,
        LocalDate completedAt
) {}