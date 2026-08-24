package com.andres.senderos.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record HikeHistoryCreateDto(
        @NotNull Long routeId,
        @NotNull LocalDate completedAt
) {}